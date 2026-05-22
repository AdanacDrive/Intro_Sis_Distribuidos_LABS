# medir_bd.py — cuenta eventos en la BD replica en un intervalo de tiempo
import sqlite3
import sys
import time

def medir(duracion=120):
    conn = sqlite3.connect("bd_replica.db")
    c = conn.cursor()
    c.execute("SELECT COUNT(*) FROM eventos")
    inicio = c.fetchone()[0]
    print(f"[MEDICION] Eventos al inicio: {inicio}")
    print(f"[MEDICION] Midiendo durante {duracion} segundos...")
    time.sleep(duracion)
    c.execute("SELECT COUNT(*) FROM eventos")
    fin = c.fetchone()[0]
    total = fin - inicio
    print(f"[MEDICION] Eventos al final: {fin}")
    print(f"[MEDICION] Eventos en {duracion}s: {total}")
    conn.close()
    return total

if __name__ == "__main__":
    duracion = int(sys.argv[1]) if len(sys.argv) > 1 else 120
    medir(duracion)
