#!/bin/bash
# ============================================================
# 05_start_cluster.sh
# Formatear NameNode y arrancar el cluster
# Ejecutar SOLO en MIG29 (el NameNode)
# ============================================================

set -e

NAMENODE_DIR="/opt/hadoop/data/namenode"

# Verificar que las variables de entorno esten cargadas
if [ -z "$HADOOP_HOME" ]; then
    echo "[FAIL] HADOOP_HOME no esta definido"
    echo "       Ejecuta: source ~/.bashrc"
    exit 1
fi

# Forzar pdsh a usar SSH (evita Permission denied)
export PDSH_RCMD_TYPE=ssh

# Formatear NameNode (solo si esta vacio)
if [ -z "$(ls -A $NAMENODE_DIR 2>/dev/null)" ]; then
    echo "[*] Formateando NameNode (primera vez)..."
    hdfs namenode -format -nonInteractive
else
    echo "[!] NameNode ya formateado, se omite el formateo"
fi

echo ""
echo "[*] Arrancando HDFS (NameNode + SecondaryNameNode + DataNodes)..."
start-dfs.sh

echo ""
echo "[*] Arrancando YARN (ResourceManager + NodeManagers)..."
start-yarn.sh

echo ""
echo "[*] Esperando a que los daemons reporten..."
sleep 5

echo ""
echo "[*] Daemons activos en este nodo (master):"
jps

echo ""
echo "[*] Reporte del cluster:"
hdfs dfsadmin -report | head -20

echo ""
echo "[OK] Cluster arrancado"
echo ""
echo "    Web UI: http://10.43.98.223:9870"
echo ""
echo "    Para detener: stop-yarn.sh && stop-dfs.sh"
