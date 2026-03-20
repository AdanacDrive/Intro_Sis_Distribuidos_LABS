# ==============================================================================================
# = Pontificia Universidad Javeriana =
#
# Autores:         Juan Diego Ariza, Nicolas Leon, Juan Diego Pardo, Juan Sebastian Urbano
# Curso:           Sistemas Distribuidos - 2026-10
# Fecha:           Marzo 2026
#
# Archivo:         Makefile
# Descripcion:
#   Automatiza la compilacion de los dos ejecutables del taller.
#   Usa mpicc (compilador MPI) con soporte para OpenMP (-fopenmp) y matematicas (-lm).
#
# Uso:
#   make          -> compila los dos ejecutables
#   make clean    -> elimina los ejecutables generados
# ==============================================================================================

# Compilador MPI 
MPICC = mpicc

# Flags de compilacion:
#   -lm       : enlaza la libreria matematica
#   -fopenmp  : habilita las directivas OpenMP (#pragma omp)
FLAGS = -lm -fopenmp

# Modulo auxiliar compartido por los dos programas principales
MODULO = moduloMPI.c

# Lista de ejecutables a generar
PROGRAMAS = mxmOmpMPIfxc mxmOmpMPIfxt

# Regla por defecto: compilar todos los programas
all: ${PROGRAMAS}

# Compilar el ejecutable FxC (Filas x Columnas)
mxmOmpMPIfxc:
	${MPICC} -o $@ $@.c ${MODULO} ${FLAGS}

# Compilar el ejecutable FxT (Filas x Transpuesta)
mxmOmpMPIfxt:
	${MPICC} -o $@ $@.c ${MODULO} ${FLAGS}

# Eliminar los ejecutables compilados
clean:
	$(RM) ${PROGRAMAS}
