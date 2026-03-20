/************************************************************************************************
 * = Pontificia Universidad Javeriana =
 *
 * Autores:         Juan Diego Ariza, Nicolas Leon, Juan Diego Pardo, Juan Sebastian Urbano
 * Curso:           Sistemas Distribuidos - 2026-10
 * Fecha:           Marzo 2026
 *
 * Archivo:         moduloMPI.c
 * Descripcion:
 *   Funciones auxiliares para la multiplicacion de matrices usando MPI y OpenMP.
 *   Tenemos dos versiones del algoritmo:
 *     - mxmOmpFxT: Filas x Transpuesta, mas rapida porque aprovecha la localidad de cache.
 *     - mxmOmpFxC: Filas x Columnas, la clasica pero mas lenta por los saltos en memoria.
 *
 * Compilacion:
 *   mpicc -o ejecutable main.c moduloMPI.c -lm -fopenmp
 ************************************************************************************************/

#include <mpi.h>
#include <omp.h>
#include <stdio.h>
#include <stdlib.h>
#include <time.h>
#include <sys/time.h>
#include "moduloMPI.h"

/* Variables globales para medir el tiempo de ejecucion */
struct timeval inicio, fin;


/************************************************************************************************
 * impMatrix
 *
 * Imprime la matriz en consola. Solo lo hacemos si la dimension es menor a 13
 * para no inundar la terminal cuando trabajemos con matrices grandes.
 ************************************************************************************************/
void impMatrix(double *mat, int n){
    if(n < 13){
        printf("\n====================================================================");
        for (int i = 0; i < n*n; i++, mat++) {
            if(i % n == 0) printf("\n");    /* Salto de linea al inicio de cada fila */
            printf("%0.3f ", *mat);
        }
        printf("\n====================================================================\n");
    }
}


/************************************************************************************************
 * matrixTRP
 *
 * Calcula la transpuesta de B y la guarda en mT (mT[i][j] = mB[j][i]).
 * Esto es clave para FxT: al transponer B podemos recorrerla linealmente
 * en el producto punto, lo que reduce los cache misses considerablemente.
 ************************************************************************************************/
void matrixTRP(int N, double *mB, double *mT){
    for(int i = 0; i < N; i++)
        for(int j = 0; j < N; j++)
            mT[i*N+j] = mB[j*N+i];    /* Intercambiamos filas por columnas */

    impMatrix(mT, N);    /* Verificamos como quedo si la matriz es pequena */
}


/************************************************************************************************
 * mxmOmpFxT  —  Filas x Transpuesta (Optimizada)
 *
 * Multiplica A por B usando la transpuesta de B. Al hacer esto, tanto pA como pB
 * avanzan secuencialmente en memoria durante el producto punto, lo que aprovecha
 * mejor la cache del procesador comparado con FxC.
 *
 * El bucle externo (filas) se reparte entre los hilos OpenMP disponibles.
 * Al final liberamos mT para no dejar fugas de memoria.
 ************************************************************************************************/
void mxmOmpFxT(double *mA, double *mB, double *mC, int tw, int D, int nH){

    /* Reservar memoria para la transpuesta e inicializarla en cero */
    double *mT = (double *)calloc(D*D, sizeof(double));
    matrixTRP(D, mB, mT);

    omp_set_num_threads(nH);
    #pragma omp parallel
    {
        /* Repartimos las filas entre los hilos disponibles */
        #pragma omp for
        for(int i = 0; i < tw; i++){
            for(int j = 0; j < D; j++){
                double *pA, *pB, Suma = 0.0;
                pA = mA + i*D;    /* Inicio de la fila i en A          */
                pB = mT + j*D;    /* Inicio de la fila j en transpuesta */

                /* Ambos punteros avanzan de a 1 → acceso secuencial a memoria */
                for(int k = 0; k < D; k++, pA++, pB++)
                    Suma += *pA * *pB;

                mC[i*D+j] = Suma;
            }
        }
    }
    /* Liberar la transpuesta para evitar memory leaks */
    free(mT);
}


/************************************************************************************************
 * mxmOmpFxC  —  Filas x Columnas (Clasica)
 *
 * Multiplicacion tradicional sin transponer B. El problema es que para leer
 * cada columna de B hay que saltar D posiciones en memoria, lo que genera
 * muchos cache misses y se nota bastante en el tiempo con matrices grandes.
 * El bucle externo tambien esta paralelizado con OpenMP.
 ************************************************************************************************/
void mxmOmpFxC(double *mA, double *mB, double *mC, int tw, int D, int nH){

    omp_set_num_threads(nH);
    #pragma omp parallel
    {
        #pragma omp for
        for(int i = 0; i < tw; i++){
            for(int j = 0; j < D; j++){
                double *pA, *pB, Suma = 0.0;
                pA = mA + i*D;    /* Inicio de la fila i en A              */
                pB = mB + j;      /* Inicio de la columna j en B           */

                /* pA avanza de a 1, pB salta D posiciones → mas cache misses */
                for(int k = 0; k < D; k++, pA++, pB += D)
                    Suma += *pA * *pB;

                mC[i*D+j] = Suma;
            }
        }
    }
}


/************************************************************************************************
 * mensajeVerifica
 *
 * Imprime como el Master esta dividiendo el trabajo entre los workers.
 * Muestra la dimension de la matriz, cuantos workers hay y el tamano de tajada.
 * Solo activo si N < 13 para no saturar la salida en experimentos reales.
 ************************************************************************************************/
void mensajeVerifica(int N, int cantidadW){
    if(N < 13){
        printf("\n");
        printf("********************************************************************\n");
        printf("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++\n");
        printf("++++++ \t\tDimension de Matrix NxN \t  = %dx%d \t++++\n", N, N);
        printf("++++++ \t\tCantidad de Workers (np - MASTER) = %d \t\t++++\n", cantidadW);
        printf("++++++ \t\tTajada de matriz A para Workers   = %dx%d \t++++\n", N/cantidadW, N);
        printf("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++\n");
        printf("********************************************************************\n");
        printf("\n");
    }
}


/************************************************************************************************
 * iniMatrix
 *
 * Llena A y B con valores fijos basados en el indice en lugar de aleatorios puros.
 * Asi las pruebas son reproducibles y podemos verificar que el resultado
 * matematico siempre sea correcto entre ejecuciones.
 ************************************************************************************************/
void iniMatrix(double *mA, double *mB, int D){
    srand(time(NULL));
    for(int i = 0; i < D*D; i++, mA++, mB++){
        *mA = 0.08 * i;
        *mB = 0.02 * i;
    }
}


/************************************************************************************************
 * iniTime
 *
 * Guarda el tiempo exacto antes de empezar el computo usando gettimeofday().
 * Precision en microsegundos. Debe llamarse justo antes de la seccion a medir.
 ************************************************************************************************/
void iniTime(){
    gettimeofday(&inicio, (void *)0);
}


/************************************************************************************************
 * endTime
 *
 * Calcula el tiempo transcurrido desde iniTime() y lo imprime en microsegundos.
 * Lo imprimimos directo por stdout para que lanzador.pl lo capture facilmente
 * y lo guarde en los archivos CSV de resultados.
 ************************************************************************************************/
void endTime(){
    gettimeofday(&fin, (void *)0);
    fin.tv_usec -= inicio.tv_usec;
    fin.tv_sec  -= inicio.tv_sec;

    double tiempo = (double)(fin.tv_sec*1000000 + fin.tv_usec);
    printf("%9.0f \n", tiempo);
}


/************************************************************************************************
 * argumentos
 *
 * Verifica que se pasen bien los parametros por consola al ejecutar el programa.
 * Si faltan argumentos, muestra el formato correcto y termina la ejecucion
 * antes de que falle de forma inesperada.
 ************************************************************************************************/
void argumentos(int cantidad){
    if (cantidad != 3){
        printf("Ingreso de Argumentos: \n\n");
        printf("\t\t$mpirun -hostfile file -np p ./ejecutable DimMatriz NumHilos \n\n");
        printf("\nfile: Archivo de Master y Workers \n");
        printf("\np: procesos Master+Workers\n");
        exit(0);
    }
}


/************************************************************************************************
 * verificarDiv
 *
 * Valida que las filas de la matriz se puedan repartir de forma exacta entre los workers.
 * Si el modulo no es cero, hay que abortar todo el entorno MPI para evitar deadlocks
 * o que un worker se quede esperando datos que nunca van a llegar.
 ************************************************************************************************/
void verificarDiv(int qworkers, int Dim){
    if ((qworkers < 1) || (Dim % qworkers != 0)) {
        printf("Error: NxN (%d) debe ser divisible por cantidad de workers (%d)\n", Dim, qworkers);
        printf("Error: Número de procesos (%d) > 1 \n", qworkers);
        MPI_Abort(MPI_COMM_WORLD, 1);    /* Termina todos los procesos MPI de forma segura */
        exit(0);
    }
}
