# Cluster Hadoop HDFS — Sistema de Ficheros Distribuidos

Implementación de un cluster Apache Hadoop HDFS de 4 nodos sobre máquinas virtuales con Ubuntu 22.04 LTS, como parte de la asignatura **Introducción a los Sistemas Distribuidos** de la Pontificia Universidad Javeriana (2026).

## 👥 Integrantes

- Juan Diego Ariza
- Nicolas Leon
- Juan Diego Pardo
- Juan Sebastian Urbano

## 🎯 Objetivo

Construir un cluster HDFS funcional de 4 nodos que demuestre los conceptos fundamentales de los sistemas de ficheros distribuidos: replicación automática de bloques, tolerancia a fallos y transparencia de acceso.

## 🖥️ Arquitectura

| Hostname | IP | Rol | Daemons |
|---|---|---|---|
| MIG29 | 10.43.98.223 | NameNode (master) | NameNode, SecondaryNameNode, ResourceManager |
| MIG476 | 10.43.100.179 | DataNode 1 | DataNode, NodeManager |
| MIG322 | 10.43.100.24 | DataNode 2 | DataNode, NodeManager |
| MIG220 | 10.43.99.161 | DataNode 3 | DataNode, NodeManager |

**Factor de replicación:** 3 (cada bloque se almacena en los 3 DataNodes)

## 🛠️ Software

- Ubuntu 22.04 LTS
- OpenJDK 8
- Apache Hadoop 3.3.6
- SSH (autenticación por clave pública)

## 📂 Estructura del Repositorio

```
cluster-hadoop-hdfs/
├── README.md                  # Este archivo
├── .gitignore
├── informe/                   # Informe LaTeX y PDF
│   ├── main.tex
│   ├── main.pdf
│   └── imagenes/              # 21 capturas del proceso
├── config/                    # Archivos de configuración del cluster
│   ├── core-site.xml
│   ├── hdfs-site.xml
│   ├── mapred-site.xml
│   ├── yarn-site.xml
│   ├── workers
│   └── bashrc.snippet
└── scripts/                   # Scripts de instalación
    ├── 01_install_java.sh
    ├── 02_setup_hosts.sh
    ├── 03_setup_ssh.sh
    ├── 04_install_hadoop.sh
    └── 05_start_cluster.sh
```

## 🚀 Cómo reproducir el cluster

### Pre-requisitos

- 4 máquinas virtuales con Ubuntu 22.04 en la misma red
- Un usuario común en las 4 máquinas (en este caso `estudiante`)
- Conectividad de red entre las 4 VMs

### Paso 1: Instalar Java 8 (en las 4 VMs)

```bash
sudo apt update
sudo apt install -y openjdk-8-jdk ssh pdsh net-tools
sudo update-alternatives --config java   # elegir Java 8
sudo update-alternatives --config javac  # elegir Java 8
java -version  # verificar: 1.8.0_xxx
```

### Paso 2: Configurar /etc/hosts (en las 4 VMs)

Agregar al final de `/etc/hosts`:

```
10.43.98.223   MIG29
10.43.100.179  MIG476
10.43.100.24   MIG322
10.43.99.161   MIG220
```

Verificar conectividad:

```bash
ping -c 2 MIG29
ping -c 2 MIG476
ping -c 2 MIG322
ping -c 2 MIG220
```

### Paso 3: SSH sin contraseña (solo en MIG29)

```bash
ssh-keygen -t rsa -P "" -f ~/.ssh/id_rsa
ssh-copy-id estudiante@MIG29
ssh-copy-id estudiante@MIG476
ssh-copy-id estudiante@MIG322
ssh-copy-id estudiante@MIG220

# Verificar (debe responder sin pedir password)
ssh MIG476 "hostname"
```

### Paso 4: Instalar Hadoop 3.3.6 (en las 4 VMs)

```bash
cd ~
wget https://dlcdn.apache.org/hadoop/common/hadoop-3.3.6/hadoop-3.3.6.tar.gz
tar -xzf hadoop-3.3.6.tar.gz
sudo mv hadoop-3.3.6 /opt/hadoop
sudo chown -R estudiante:estudiante /opt/hadoop
```

Agregar las variables de entorno al final de `~/.bashrc` desde `config/bashrc.snippet`:

```bash
cat config/bashrc.snippet >> ~/.bashrc
source ~/.bashrc
hadoop version  # verificar: Hadoop 3.3.6
```

### Paso 5: Aplicar configuración (en las 4 VMs)

Copiar los archivos de `config/` a `/opt/hadoop/etc/hadoop/`:

```bash
cp config/core-site.xml /opt/hadoop/etc/hadoop/
cp config/hdfs-site.xml /opt/hadoop/etc/hadoop/
cp config/mapred-site.xml /opt/hadoop/etc/hadoop/
cp config/yarn-site.xml /opt/hadoop/etc/hadoop/
```

El archivo `workers` solo se copia en el NameNode (MIG29):

```bash
# Solo en MIG29
cp config/workers /opt/hadoop/etc/hadoop/
```

Editar `/opt/hadoop/etc/hadoop/hadoop-env.sh` y descomentar/editar la línea:

```bash
export JAVA_HOME=/usr/lib/jvm/java-8-openjdk-amd64
```

### Paso 6: Crear directorios de datos (en las 4 VMs)

```bash
sudo mkdir -p /opt/hadoop/data/namenode
sudo mkdir -p /opt/hadoop/data/datanode
sudo chown -R estudiante:estudiante /opt/hadoop/data
```

### Paso 7: Formatear y arrancar (solo en MIG29)

```bash
hdfs namenode -format
export PDSH_RCMD_TYPE=ssh
echo "export PDSH_RCMD_TYPE=ssh" >> ~/.bashrc
start-dfs.sh
start-yarn.sh
```

### Paso 8: Verificar el cluster

En MIG29:

```bash
jps
# Debe mostrar: NameNode, SecondaryNameNode, ResourceManager

hdfs dfsadmin -report
# Debe mostrar: Live datanodes (3)
```

En cada DataNode (MIG476, MIG322, MIG220):

```bash
jps
# Debe mostrar: DataNode, NodeManager
```

**Web UI:** http://10.43.98.223:9870

## ✅ Pruebas realizadas

- [x] Estado del cluster con 3 DataNodes activos
- [x] Acceso a la interfaz web del NameNode
- [x] Subida de archivos al HDFS
- [x] Replicación de bloques con factor 3 (verificada con `hdfs fsck`)
- [x] Tolerancia a fallos: caída de un DataNode no afecta la disponibilidad de los datos
- [x] Recuperación automática del cluster tras reactivar el DataNode

### Comandos de prueba

```bash
# Crear estructura y subir archivos
hdfs dfs -mkdir -p /user/estudiante/pruebas
echo "Hola desde el cluster Hadoop" > prueba1.txt
hdfs dfs -put prueba1.txt /user/estudiante/pruebas/

# Generar archivo grande para verificar replicación
dd if=/dev/urandom of=archivo_grande.dat bs=1M count=200
hdfs dfs -put archivo_grande.dat /user/estudiante/pruebas/

# Verificar que cada bloque está replicado en 3 DataNodes
hdfs fsck /user/estudiante/pruebas/archivo_grande.dat \
    -files -blocks -locations

# Simular caída de un DataNode (en MIG220)
hdfs --daemon stop datanode

# Esperar ~50 segundos y verificar
hdfs dfsadmin -report   # Live: 2, Dead: 1
hdfs dfs -cat /user/estudiante/pruebas/prueba1.txt   # Sigue funcionando

# Recuperar
hdfs --daemon start datanode
```

Ver el informe completo en `informe/main.pdf` para detalles, capturas y resultados.

## 📚 Referencias

- [HDFS Architecture Guide](https://hadoop.apache.org/docs/r3.3.6/hadoop-project-dist/hadoop-hdfs/HdfsDesign.html)
- [Hadoop Cluster Setup](https://hadoop.apache.org/docs/r3.3.6/hadoop-project-dist/hadoop-common/ClusterSetup.html)
- White, T. (2015). *Hadoop: The Definitive Guide* (4th ed.). O'Reilly Media.

## 📝 Asignatura

**Introducción a los Sistemas Distribuidos** — Profesor: John Corredor, Ph.D.
Pontificia Universidad Javeriana — Mayo 2026
