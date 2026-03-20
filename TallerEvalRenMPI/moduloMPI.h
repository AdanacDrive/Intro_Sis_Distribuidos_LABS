/************************************************************************************************
 * = Pontificia Universidad Javeriana =
 *
 * Autores:         Juan Diego Ariza, Nicolas Leon, Juan Diego Pardo, Juan Sebastian Urbano
 * Curso:           Sistemas Distribuidos - 2026-10
 * Fecha:           Marzo 2026
 *
 * Archivo:         moduloMPI.h
 * Descripcion:
 *   Cabecera del modulo de soporte para la multiplicacion de matrices con MPI + OpenMP.
 *   Declara todas las funciones usadas por mxmOmpMPIfxt.c y mxmOmpMPIfxc.c.
 ************************************************************************************************/

#ifndef __MODULOMPI_H__
#define __MODULOMPI_H__


/* Transpone la matriz mB (NxN) y guarda el resultado en mT. Usada por FxT para mejorar cache. */
void matrixTRP(int N, double *mB, double *mT);

/* Multiplicacion Filas x Transpuesta con OpenMP. Transpone B antes de multiplicar. */
void mxmOmpFxT(double *mA, double *mB, double *mC, int tw, int D, int nH);

/* Multiplicacion Filas x Columnas con OpenMP. Acceso por columnas sin transponer B. */
void mxmOmpFxC(double *mA, double *mB, double *mC, int tw, int D, int nH);

/* Imprime la matriz en consola. Solo activo si n < 13 para verificacion. */
void impMatrix(double *matA, int n);

/* Inicializa A y B con valores deterministas para reproducibilidad de experimentos. */
void iniMatrix(double *mA, double *mB, int D);

/* Registra el tiempo de inicio de la medicion con precision de microsegundos. */
void iniTime();

/* Calcula e imprime el tiempo transcurrido desde iniTime() en microsegundos. */
void endTime();

/* Verifica que se pasen los argumentos correctos al ejecutar el programa. */
void argumentos(int cantidad);

/* Verifica que N sea divisible por el numero de workers. Si no, aborta MPI. */
void verificarDiv(int qworkers, int Dim);

/* Imprime los parametros de distribucion del trabajo (tajada por worker). */
void mensajeVerifica(int N, int cantidadW);


#endif /* __MODULOMPI_H__ */
