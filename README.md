# Gestión Inteligente de Tráfico Urbano
**Introducción a los Sistemas Distribuidos — 2026-30**
Pontificia Universidad Javeriana · Departamento de Ingeniería de Sistemas

**Integrantes:**
- Juan Diego Ariza
- Nicolas Leon
- Juan Diego Pardo
- Juan Sebastian Urbano

**Profesor:** John Corredor, Ph.D.

## Descripción
Sistema distribuido que simula la gestión inteligente del tráfico urbano en una ciudad representada como una matriz de 5×6 intersecciones (30 intersecciones, 90 sensores, 30 semáforos). Los componentes se comunican mediante ZeroMQ y se distribuyen en tres máquinas físicas del laboratorio.

## Arquitectura
| Máquina | Hostname | IP | Componentes |
|---------|----------|----|-------------|
| PC1 | MIG220 | 10.43.99.161 | Sensores, Broker ZeroMQ, Semáforos |
| PC2 | MIG322 | 10.43.100.24 | Analítica, Control Semáforos, BD Réplica, Health Check |
| PC3 | MIG29  | 10.43.98.223 | BD Principal, Monitoreo y Consulta |

## Requisitos
```bash
pip install pyzmq
```

## Ejecución
Iniciar en este orden: PC3 → PC2 → PC1

**PC3:** `python3 pc3/bd_principal.py` y `python3 pc3/monitoreo_consulta.py`

**PC2:** `python3 pc2/servicio_analitica.py`, `python3 pc2/control_semaforos.py`, `python3 pc2/health_check.py`

**PC1:** `python3 pc1/broker_zmq.py`, luego los sensores y semáforo
