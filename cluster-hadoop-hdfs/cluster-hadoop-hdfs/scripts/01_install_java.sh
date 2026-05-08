#!/bin/bash
# ============================================================
# 01_install_java.sh
# Instalacion de OpenJDK 8 + utilidades necesarias
# Ejecutar en las 4 VMs (MIG29, MIG476, MIG322, MIG220)
# ============================================================

set -e

echo "[*] Actualizando paquetes..."
sudo apt update

echo "[*] Instalando OpenJDK 8 + ssh + pdsh + net-tools..."
sudo apt install -y openjdk-8-jdk ssh pdsh net-tools

echo ""
echo "[!] Si tenias otra version de Java instalada, ejecuta:"
echo "    sudo update-alternatives --config java"
echo "    sudo update-alternatives --config javac"
echo "    y selecciona java-8-openjdk-amd64"
echo ""

echo "[*] Verificando version de Java..."
java -version
javac -version

echo ""
echo "[OK] Java 8 instalado correctamente"
