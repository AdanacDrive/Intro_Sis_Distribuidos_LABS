/************************************************************************************************
 * = Pontificia Universidad Javeriana =
 *
 * Autores:         Juan Diego Ariza, Nicolas Leon, Juan Diego Pardo, Juan Sebastian Urbano
 * Curso:           Sistemas Distribuidos - 2026-10
 * Fecha:           Marzo 2026
 *
 * Archivo:         mxmOmpMPIfxt.c
 * Descripcion:
 *   Programa principal para multiplicacion de matrices con MPI + OpenMP usando
 *   el algoritmo Filas x Transpuesta (FxT).
 *
 *   El modelo es MASTER-WORKER:
 *     - MASTER (rank 0): inicializa todo, reparte tajadas de A a los workers,
 *                        recoge los resultados y mide el tiempo total.
 *     - WORKERS (rank > 0): reciben su pedazo de A y B completa, calculan
 *                           su parte de C con FxT + OpenMP y la devuelven.
 
 * Compilacion:
 *   mpicc -o mxmOmpMPIfxt mxmOmpMPIfxt.c moduloMPI.c -lm -fopenmp
 *
 * Ejecucion:
 *   mpirun -hostfile filehost -np <p> ./mxmOmpMPIfxt <DimMatriz> <NumHilos>
 ************************************************************************************************/

#include <mpi.h>
#include <stdlib.h>
#include "moduloMPI.h"

#define MASTER      0    /* El proceso con rank 0 es el coordinador */
#define FROM_MASTER 1    /* Etiqueta para mensajes del Master        */
#define FROM_WORKER 2    /* Etiqueta para mensajes de los Workers    */

int main(int argc, char *argv[]) {

    /*====================================================================================
     * 0. Verificar argumentos de entrada
     * Necesitamos exactamente: DimMatriz y NumHilos. Si no, el programa explica el uso.
     *===================================================================================*/
    argumentos(argc);
    int N  = (int) atoi(argv[1]);    /* Dimension N de la matriz NxN  */
    int nH = (int) atoi(argv[2]);    /* Numero de hilos OpenMP        */

    double *matrixA = NULL, *matrixB = NULL, *matrixC = NULL;
    int cantProcesos;    /* Total de procesos lanzados con -np          */
    int idProceso;       /* Rank del proceso actual (0 = MASTER)        */
    int tW;              /* Filas de A que le tocan a cada worker       */

    MPI_Init(&argc, &argv);
    MPI_Comm_size(MPI_COMM_WORLD, &cantProcesos);
    MPI_Comm_rank(MPI_COMM_WORLD, &idProceso);

    int cantidadW = cantProcesos - 1;    /* Workers = total - MASTER */

    /*====================================================================================
     * 1. MASTER: inicializar matrices y calcular la tajada de cada worker
     * Solo el rank 0 entra acá. Verifica que N sea divisible por workers,
     * reserva memoria, inicializa A y B, y calcula cuántas filas le tocan a cada uno.
     *===================================================================================*/
    if (idProceso == MASTER) {
        verificarDiv(cantidadW, N);    /* N debe ser divisible por workers */

        matrixA = (double *)calloc(N*N, sizeof(double));
        matrixB = (double *)calloc(N*N, sizeof(double));
        matrixC = (double *)calloc(N*N, sizeof(double));

        iniMatrix(matrixA, matrixB, N);    /* Llenar A y B con valores fijos */

        tW = N / cantidadW;    /* Filas por worker */

        /* Verificacion visual si N < 13 */
        impMatrix(matrixA, N);
        impMatrix(matrixB, N);
        mensajeVerifica(N, cantidadW);
    }

    /*====================================================================================
     * 2. Difundir N y tW a todos los procesos
     * Los workers necesitan saber el tamano de la matriz y su tajada
     * antes de poder reservar memoria y recibir datos.
     *===================================================================================*/
    MPI_Bcast(&N,  1, MPI_INT, 0, MPI_COMM_WORLD);
    MPI_Bcast(&tW, 1, MPI_INT, 0, MPI_COMM_WORLD);

    /*====================================================================================
     * 3. MASTER: enviar trabajo a los workers y recoger resultados
     * - Manda matrixB completa a todos (la necesitan entera para multiplicar)
     * - Envia a cada worker su tajada de A (filas consecutivas)
     * - Espera y recibe el resultado parcial de C de cada worker
     * El tiempo se mide desde antes del primer Bcast hasta recibir el ultimo resultado.
     *===================================================================================*/
    if (idProceso == MASTER) {
        iniTime();    /* Inicio de la medicion */

        MPI_Bcast(matrixB, N*N, MPI_DOUBLE, 0, MPI_COMM_WORLD);

        /* Enviar la tajada correspondiente de A a cada worker */
        for (int w = 1; w < cantProcesos; w++) {
            int iniSlice = (w-1) * tW;
            MPI_Send(matrixA + iniSlice*N, tW*N, MPI_DOUBLE, w, 0, MPI_COMM_WORLD);
        }

        /* Recibir el resultado parcial de C de cada worker */
        for (int w = 1; w < cantProcesos; w++) {
            int iniSlice = (w-1) * tW;
            MPI_Recv(matrixC + iniSlice*N, tW*N, MPI_DOUBLE, w, 1, MPI_COMM_WORLD, MPI_STATUS_IGNORE);
        }

        endTime();    /* Fin de la medicion — imprime microsegundos */

        impMatrix(matrixC, N);    /* Verificar resultado si N < 13 */
    }

    /*====================================================================================
     * 4. WORKERS: recibir datos y calcular su parte de C
     * Cada worker reserva memoria para su tajada de A, B completa y su parte de C.
     * Recibe B por Bcast y su tajada de A por Recv.
     *===================================================================================*/
    if (idProceso > MASTER){
        double *matA = (double *)malloc(tW*N * sizeof(double));
        double *matB = (double *)malloc(N*N  * sizeof(double));
        double *matC = (double *)malloc(tW*N * sizeof(double));

        MPI_Bcast(matB, N*N, MPI_DOUBLE, 0, MPI_COMM_WORLD);
        MPI_Recv(matA, tW*N, MPI_DOUBLE, 0, 0, MPI_COMM_WORLD, MPI_STATUS_IGNORE);

        /*==================================================================================
         * 5. Multiplicar con FxT + OpenMP
         * mxmOmpFxT transpone B, luego paraleliza el bucle de filas con nH hilos.
         * El acceso secuencial a la transpuesta reduce los cache misses vs FxC.
         *=================================================================================*/
        mxmOmpFxT(matA, matB, matC, tW, N, nH);

        MPI_Send(matC, tW*N, MPI_DOUBLE, 0, 1, MPI_COMM_WORLD);

        /* Liberar memoria del worker */
        free(matA);
        free(matB);
        free(matC);
    }

    /*====================================================================================
     * 6. MASTER: liberar memoria antes de finalizar MPI
     *===================================================================================*/
    if (idProceso == MASTER) {
        free(matrixA);
        free(matrixB);
        free(matrixC);
    }

    MPI_Finalize();
    return 0;
}
