#!/bin/bash
# ============================================================
# 02_setup_hosts.sh
# Agregar las entradas del cluster a /etc/hosts
# Ejecutar en las 4 VMs
# ============================================================

set -e

HOSTS_BLOCK="
# Cluster Hadoop HDFS
10.43.98.223   MIG29
10.43.100.179  MIG476
10.43.100.24   MIG322
10.43.99.161   MIG220
"

if grep -q "Cluster Hadoop HDFS" /etc/hosts; then
    echo "[!] /etc/hosts ya contiene las entradas del cluster"
else
    echo "[*] Agregando entradas al /etc/hosts..."
    echo "$HOSTS_BLOCK" | sudo tee -a /etc/hosts > /dev/null
    echo "[OK] Entradas agregadas"
fi

echo ""
echo "[*] Probando conectividad por hostname..."
for host in MIG29 MIG476 MIG322 MIG220; do
    if ping -c 1 -W 2 "$host" > /dev/null 2>&1; then
        echo "    [OK] $host responde"
    else
        echo "    [FAIL] $host no responde"
    fi
done
