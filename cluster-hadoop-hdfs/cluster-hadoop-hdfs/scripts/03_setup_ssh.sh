#!/bin/bash
# ============================================================
# 03_setup_ssh.sh
# Generar llave SSH y distribuirla a los 4 nodos
# Ejecutar SOLO en MIG29 (el NameNode)
# ============================================================

set -e

USER_NAME="estudiante"
NODES=("MIG29" "MIG476" "MIG322" "MIG220")

if [ ! -f ~/.ssh/id_rsa ]; then
    echo "[*] Generando par de llaves RSA..."
    ssh-keygen -t rsa -P "" -f ~/.ssh/id_rsa
else
    echo "[!] Ya existe ~/.ssh/id_rsa, se omite la generacion"
fi

echo ""
echo "[*] Distribuyendo llave publica a los 4 nodos..."
echo "    (te pedira password de cada VM una sola vez)"
echo ""

for host in "${NODES[@]}"; do
    echo "    -> $host"
    ssh-copy-id -o StrictHostKeyChecking=no "${USER_NAME}@${host}"
done

echo ""
echo "[*] Pre-poblando known_hosts..."
for host in "${NODES[@]}"; do
    ssh-keyscan -H "$host" >> ~/.ssh/known_hosts 2>/dev/null
done

echo ""
echo "[*] Verificando SSH sin password..."
for host in "${NODES[@]}"; do
    if ssh -o BatchMode=yes "$host" "hostname" 2>/dev/null; then
        echo "    [OK] $host responde sin password"
    else
        echo "    [FAIL] $host pide password"
    fi
done

echo ""
echo "[OK] SSH passwordless configurado"
