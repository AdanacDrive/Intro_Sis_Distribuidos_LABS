# Taller 04 — Hilos y Sockets

[![Java](https://img.shields.io/badge/Java-21-orange.svg)](https://openjdk.org/)
[![Build](https://img.shields.io/badge/build-make-blue.svg)](https://www.gnu.org/software/make/)
[![Course](https://img.shields.io/badge/curso-Sistemas%20Distribuidos-red.svg)](#)

Taller 04 de la asignatura **Introducción a los Sistemas Distribuidos** de la
Pontificia Universidad Javeriana. Implementación y comparación de:

1. **Sockets TCP vs UDP** en Java — comunicación orientada a conexión vs no orientada a conexión.
2. **Ejecución secuencial vs paralela con hilos** en Java — usando `extends Thread` y `implements Runnable`.

## Equipo

| Integrante               |
|--------------------------|
| Juan Diego Ariza Lopez   |
| Juan Diego Pardo         |
| Juan Sebastian Urbano    |

**Docente:** Ing. John Corredor Franco, Ph.D.
**Universidad:** Pontificia Universidad Javeriana — Facultad de Ingeniería
**Periodo:** 2026-30

---

## Tabla de contenido

- [Estructura del repositorio](#estructura-del-repositorio)
- [Requisitos](#requisitos)
- [Compilación](#compilación)
- [Ejecución](#ejecución)
  - [TCP cliente y servidor](#tcp-cliente-y-servidor)
  - [UDP cliente y servidor](#udp-cliente-y-servidor)
  - [Versión secuencial (sin hilos)](#versión-secuencial-sin-hilos)
  - [Versión con hilos (extends Thread)](#versión-con-hilos-extends-thread)
  - [Versión con Runnable](#versión-con-runnable)
- [Resultados experimentales](#resultados-experimentales)
- [Tabla comparativa TCP vs UDP](#tabla-comparativa-tcp-vs-udp)
- [Limpieza](#limpieza)
- [Notas](#notas)

---

## Estructura del repositorio

```
Taller04/
├── README.md
├── .gitignore
├── TCP_UDP/
│   ├── Makefile
│   ├── serTCPsocket.java   # Servidor TCP en puerto 6001
│   ├── cliTCPsocket.java   # Cliente TCP
│   ├── serUDPsocket.java   # Servidor UDP en puerto 6000
│   └── cliUDPsocket.java   # Cliente UDP
└── THREADS/
    ├── Makefile
    ├── Cliente.java        # Modelo de cliente con su carro de compra
    ├── Cajera.java         # Cajera secuencial
    ├── CajeraThread.java   # Cajera que extiende Thread
    ├── Main.java           # Main secuencial
    ├── MainThread.java     # Main paralelo (extends Thread)
    └── MainRunnable.java   # Main paralelo (implements Runnable)
```

---

## Requisitos

- **JDK 11 o superior** (probado con OpenJDK 21.0.10)
- **GNU Make**
- Sistema Linux/macOS/WSL recomendado

Verificar instalación:

```bash
java -version
javac -version
make --version
```

---

## Compilación

Cada carpeta tiene su propio `Makefile`. Para compilar ambas:

```bash
cd TCP_UDP && make all && cd ..
cd THREADS && make all && cd ..
```

Esto genera los `.class` dentro de la carpeta `bin/` de cada módulo.

---

## Ejecución

### TCP cliente y servidor

**Terminal 1 — servidor:**

```bash
cd TCP_UDP
make serTCP
```

**Terminal 2 — cliente:**

```bash
cd TCP_UDP
make cliTCP HOST=localhost
```

Luego escribir mensajes y terminar con `fin`.

### UDP cliente y servidor

**Terminal 1 — servidor:**

```bash
cd TCP_UDP
make serUDP
```

**Terminal 2 — cliente:**

```bash
cd TCP_UDP
make cliUDP HOST=localhost
```

### Versión secuencial (sin hilos)

```bash
cd THREADS
make secuencial
```

### Versión con hilos (extends Thread)

```bash
cd THREADS
make threads
```

### Versión con Runnable

```bash
cd THREADS
make runnable
```

---

## Resultados experimentales

Caso de prueba: dos cajeras atendiendo dos clientes simultáneamente.

| Cliente   | Carro                  | Tiempo total |
|-----------|------------------------|--------------|
| Cliente 1 | { 2, 2, 1, 5, 2, 3 }   | 15 s         |
| Cliente 2 | { 1, 3, 5, 1, 1 }      | 11 s         |

Tiempos medidos con `/usr/bin/time -f "%e"`:

| Versión                       | Tiempo total | Mejora vs secuencial |
|-------------------------------|--------------|----------------------|
| Secuencial (`Main`)           | **26.14 s**  | —                    |
| Threads (`MainThread`)        | **15.12 s**  | ~42% más rápido      |
| Runnable (`MainRunnable`)     | **15.12 s**  | ~42% más rápido      |

La versión paralela reduce el tiempo total al **máximo** entre los dos clientes,
mientras que la secuencial **suma** los tiempos.

---

## Tabla comparativa TCP vs UDP

| Característica          | TCP                       | UDP                    |
|-------------------------|---------------------------|------------------------|
| Conexión previa         | Sí (3-way handshake)      | No                     |
| Confiabilidad           | Sí (ACK + retransmisión)  | No (best effort)       |
| Orden de mensajes       | Garantizado               | No garantizado         |
| Control de flujo        | Sí                        | No                     |
| Velocidad               | Más lenta                 | Más rápida             |
| Sobrecarga              | Alta                      | Baja                   |
| Pierde si destino cae   | Detecta y lanza excepción | No detecta nada        |
| Caso de uso típico      | Web, correo, SSH          | Streaming, DNS, juegos |

**Prueba clave:** al ejecutar el cliente sin levantar el servidor, TCP lanza
inmediatamente `Connection refused`, mientras que UDP envía los datagramas al
vacío sin reportar ningún error. Esta es la evidencia más contundente de la
diferencia entre los dos protocolos.

---

## Limpieza

Para eliminar los archivos compilados de cada módulo:

```bash
cd TCP_UDP && make clean && cd ..
cd THREADS && make clean && cd ..
```

---

## Notas

- Los archivos `.class`, ejecutables y archivos comprimidos **no** están versionados.
- El directorio `bin/` lo crea `make` al compilar y se elimina con `make clean`.
- Las pruebas se realizaron con cliente y servidor en la misma máquina (`localhost`).
  En una red real las diferencias entre TCP y UDP serían aún más notables debido
  a la latencia y a la posibilidad real de pérdida de paquetes.

---

## Licencia

Trabajo académico. Pontificia Universidad Javeriana, 2026.
