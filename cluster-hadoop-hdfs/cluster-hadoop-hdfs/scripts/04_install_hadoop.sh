#!/bin/bash
# ============================================================
# 04_install_hadoop.sh
# Descarga e instala Apache Hadoop 3.3.6
# Ejecutar en las 4 VMs
# ============================================================

set -e

HADOOP_VERSION="3.3.6"
HADOOP_TARBALL="hadoop-${HADOOP_VERSION}.tar.gz"
HADOOP_URL="https://dlcdn.apache.org/hadoop/common/hadoop-${HADOOP_VERSION}/${HADOOP_TARBALL}"
HADOOP_DIR="/opt/hadoop"

if [ -d "$HADOOP_DIR" ] && [ -f "$HADOOP_DIR/bin/hadoop" ]; then
    echo "[!] Hadoop ya esta instalado en $HADOOP_DIR"
else
    cd ~

    if [ ! -f "$HADOOP_TARBALL" ]; then
        echo "[*] Descargando Hadoop ${HADOOP_VERSION}..."
        wget "$HADOOP_URL"
    else
        echo "[!] $HADOOP_TARBALL ya existe, se omite descarga"
    fi

    echo "[*] Extrayendo..."
    tar -xzf "$HADOOP_TARBALL"

    echo "[*] Moviendo a $HADOOP_DIR..."
    sudo mv "hadoop-${HADOOP_VERSION}" "$HADOOP_DIR"
    sudo chown -R "$USER:$USER" "$HADOOP_DIR"
fi

echo "[*] Creando directorios de datos..."
sudo mkdir -p "$HADOOP_DIR/data/namenode"
sudo mkdir -p "$HADOOP_DIR/data/datanode"
sudo chown -R "$USER:$USER" "$HADOOP_DIR/data"

echo ""
echo "[*] Configurando JAVA_HOME en hadoop-env.sh..."
JAVA_HOME_LINE='export JAVA_HOME=/usr/lib/jvm/java-8-openjdk-amd64'
HADOOP_ENV="$HADOOP_DIR/etc/hadoop/hadoop-env.sh"

if grep -q "^export JAVA_HOME=/usr/lib/jvm/java-8-openjdk-amd64" "$HADOOP_ENV"; then
    echo "[!] JAVA_HOME ya esta configurado"
else
    echo "$JAVA_HOME_LINE" >> "$HADOOP_ENV"
fi

echo ""
echo "[!] Recordatorio: agregar las variables de entorno a ~/.bashrc:"
echo "    cat config/bashrc.snippet >> ~/.bashrc"
echo "    source ~/.bashrc"
echo ""
echo "[*] Verificando version (despues del source)..."
"$HADOOP_DIR/bin/hadoop" version

echo ""
echo "[OK] Hadoop ${HADOOP_VERSION} instalado en $HADOOP_DIR"
