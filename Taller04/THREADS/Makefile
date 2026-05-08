# ***********************************************************************************************
#                               = PONTIFICIA UNIVERSIDAD JAVERIANA =
#
#       Autores: Juan Diego Ariza Lopez, Juan Diego Pardo, Juan Sebastian Urbano
#       Materia: Introduccion a los Sistemas Distribuidos
#       Profesor: J. Corredor Franco
#       Taller 04: Hilos y Sockets
#       Fecha: 8 de mayo de 2026
#
#       Archivo de automatizacion de compilacion Makefile <JAVA> para THREADS
# ***********************************************************************************************

JAVAC = javac
JAVA  = java
PKG   = tallerThreads
BIN   = bin
SOURCES = *.java

all:
	@mkdir -p $(BIN)
	$(JAVAC) -d $(BIN) $(SOURCES)

secuencial:
	@echo "\n >>> EJECUCION version Secuencial: <<< \n"
	$(JAVA) -cp $(BIN) $(PKG).Main
	@echo ""

threads:
	@echo "\n >>> EJECUCION version con Threads (extends Thread): <<< \n"
	$(JAVA) -cp $(BIN) $(PKG).MainThread
	@echo ""

runnable:
	@echo "\n >>> EJECUCION version con Runnable (implements Runnable): <<< \n"
	$(JAVA) -cp $(BIN) $(PKG).MainRunnable
	@echo ""

clean:
	$(RM) -r $(BIN)
