# broker_zmq_multihilo.py
# Broker ZeroMQ multihilos — version modificada para pruebas de desempeno
# Autores: Juan Diego Ariza, Nicolas Leon, Juan Diego Pardo, Juan Sebastian Urbano
# Sistemas Distribuidos — Pontificia Universidad Javeriana 2026

import zmq
import json
import threading
import time

TOPICOS = ["espira", "camara", "gps"]
contadores = {"espira": 0, "camara": 0, "gps": 0, "total": 0}
lock_contadores = threading.Lock()

def cargar_config(ruta="config.json"):
    with open(ruta, "r") as f:
        return json.load(f)

def hilo_topico(topico, puerto_interno, socket_pub, lock_pub):
    context = zmq.Context()
    socket_sub = context.socket(zmq.SUB)
    socket_sub.connect(f"tcp://localhost:{puerto_interno}")
    socket_sub.setsockopt_string(zmq.SUBSCRIBE, topico)
    print(f"[BROKER-MT] Hilo {topico} listo")
    try:
        while True:
            mensaje = socket_sub.recv_string()
            with lock_pub:
                socket_pub.send_string(mensaje)
            with lock_contadores:
                contadores[topico] += 1
                contadores["total"] += 1
            print(f"[BROKER-MT] [{topico}] reenviado (total={contadores['total']})")
    except zmq.ZMQError as e:
        if e.errno != zmq.ETERM:
            print(f"[BROKER-MT] Error hilo {topico}: {e}")

def hilo_estadisticas(intervalo=30):
    while True:
        time.sleep(intervalo)
        with lock_contadores:
            print(f"[BROKER-MT] Stats — espira={contadores['espira']} camara={contadores['camara']} gps={contadores['gps']} total={contadores['total']}")

def main():
    config = cargar_config()
    puerto_sub = config["red"]["puerto_broker_sub"]
    puerto_pub = config["red"]["puerto_broker_pub"]
    puerto_interno = 5599  # puerto interno para distribuir a los hilos

    context = zmq.Context()

    # socket que recibe de los sensores
    socket_entrada = context.socket(zmq.SUB)
    socket_entrada.bind(f"tcp://*:{puerto_sub}")
    socket_entrada.setsockopt_string(zmq.SUBSCRIBE, "")

    # socket interno que redistribuye a los hilos
    socket_interno_pub = context.socket(zmq.PUB)
    socket_interno_pub.bind(f"tcp://localhost:{puerto_interno}")

    # socket que publica hacia PC2
    socket_pub_salida = context.socket(zmq.PUB)
    socket_pub_salida.bind(f"tcp://*:{puerto_pub}")
    lock_pub = threading.Lock()

    print(f"[BROKER-MT] Escuchando sensores en puerto {puerto_sub}")
    print(f"[BROKER-MT] Publicando hacia PC2 en puerto {puerto_pub}")
    print(f"[BROKER-MT] Puerto interno: {puerto_interno}")

    time.sleep(0.5)

    # iniciar hilos por topico
    for topico in TOPICOS:
        t = threading.Thread(
            target=hilo_topico,
            args=(topico, puerto_interno, socket_pub_salida, lock_pub),
            daemon=True
        )
        t.start()

    # hilo de estadisticas
    threading.Thread(target=hilo_estadisticas, args=(30,), daemon=True).start()

    print(f"[BROKER-MT] {len(TOPICOS)} hilos activos — broker multihilo listo")

    # loop principal: recibe de sensores y reenvía al socket interno
    try:
        while True:
            mensaje = socket_entrada.recv_string()
            socket_interno_pub.send_string(mensaje)
    except KeyboardInterrupt:
        print(f"\n[BROKER-MT] Detenido — total: {contadores['total']}")
    finally:
        socket_entrada.close()
        socket_interno_pub.close()
        socket_pub_salida.close()
        context.term()

if __name__ == "__main__":
    main()
