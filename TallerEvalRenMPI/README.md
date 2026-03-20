# Taller de Evaluación de Rendimiento MPI
**Sistemas Distribuidos — 2026-10**  
Pontificia Universidad Javeriana | Facultad de Ingeniería  
Profesor: John Corredor, Ph.D.

**Integrantes:**
- Juan Diego Ariza
- Nicolas Leon
- Juan Diego Pardo
- Juan Sebastian Urbano

---

## Descripción

Taller de evaluación de rendimiento que compara dos algoritmos de multiplicación de matrices cuadradas implementados con **MPI + OpenMP** sobre un cluster de 4 máquinas físicas con Linux.

| Algoritmo | Descripción |
|---|---|
| `mxmOmpMPIfxt` | Filas x Transpuesta — transpone B antes de multiplicar para mejorar localidad de caché |
| `mxmOmpMPIfxc` | Filas x Columnas — multiplicación clásica, acceso por columnas a B |

Ambos usan MPI para distribuir trabajo entre nodos y OpenMP para paralelizar dentro de cada nodo.

---

## Estructura del repositorio

```
TallerEvalRenMPI/
├── moduloMPI.h          # Cabecera con declaración de funciones
├── moduloMPI.c          # Implementación de funciones auxiliares (FxT, FxC, tiempo, etc.)
├── mxmOmpMPIfxt.c       # Programa principal — algoritmo Filas x Transpuesta
├── mxmOmpMPIfxc.c       # Programa principal — algoritmo Filas x Columnas
├── Makefile             # Compilación de los dos ejecutables
├── filehost             # Archivo de hosts del cluster para mpirun
├── lanzador.pl          # Script Perl para automatizar la batería de experimentos
├── resultados_fxt.csv   # Tiempos medidos del algoritmo FxT (900 ejecuciones)
├── resultados_fxc.csv   # Tiempos medidos del algoritmo FxC (900 ejecuciones)
└── TallerEvalRenMPI.pdf # Informe de evaluación de rendimiento
```

---

## Cluster utilizado

| Nodo | Hostname | IP | Rol | Cores |
|---|---|---|---|---|
| Nodo 0 | MIG322 | 10.43.100.24  | MASTER | 4 |
| Nodo 1 | MIG476 | 10.43.100.179 | Worker | 4 |
| Nodo 2 | MIG29  | 10.43.98.223  | Worker | 4 |
| Nodo 3 | MIG220 | 10.43.99.161  | Worker | 4 |

---

## Compilación

```bash
make
```

Genera los ejecutables `mxmOmpMPIfxt` y `mxmOmpMPIfxc`.

Para limpiar:
```bash
make clean
```

---

## Ejecución

```bash
mpirun -hostfile filehost -np <p> ./mxmOmpMPIfxt <DimMatriz> <NumHilos>
```

**Ejemplo** — 3 workers, matriz 1024x1024, 4 hilos OpenMP:
```bash
mpirun -hostfile filehost -np 4 ./mxmOmpMPIfxt 1024 4
```

> **Nota:** N debe ser divisible por el número de workers (np - 1).

---

## Automatización de experimentos

```bash
perl lanzador.pl
```

Ejecuta la batería completa de experimentos (30 repeticiones por configuración) y genera:
- `resultados_fxt.csv`
- `resultados_fxc.csv`

---

## Batería de experimentos

| np | Workers | N | nH | Reps |
|---|---|---|---|---|
| 2 | 1 | 512, 1024, 2048 | 1, 2, 4 | 30 |
| 4 | 3 | 768, 1536 | 1, 2, 4 | 30 |

Total: **900 ejecuciones por algoritmo**.

---

## Resultados principales

- **FxT es hasta 4.47x más rápido que FxC** para N=2048, debido a mejor aprovechamiento de caché.
- Agregar hilos OpenMP no mejora significativamente FxT (speedup máximo ~1.01x).
- FxC sí mejora con hilos para matrices grandes (~14% con 4 hilos, N=2048).
- El overhead de comunicación MPI limita el speedup al distribuir entre workers.
