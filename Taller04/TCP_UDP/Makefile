# ***********************************************************************************************
#                               = PONTIFICIA UNIVERSIDAD JAVERIANA =
#
#       Autores: Juan Diego Ariza Lopez, Juan Diego Pardo, Juan Sebastian Urbano
#       Materia: Introduccion a los Sistemas Distribuidos
#       Profesor: J. Corredor Franco
#       Taller 04: Hilos y Sockets
#       Fecha: 8 de mayo de 2026
#
#       Archivo de automatizacion de compilacion Makefile <JAVA> para TCP/UDP
# ***********************************************************************************************

JAVAC = javac
JAVA  = java
BIN   = bin
SOURCES = serTCPsocket.java cliTCPsocket.java serUDPsocket.java cliUDPsocket.java

all:
	@mkdir -p $(BIN)
	$(JAVAC) -d $(BIN) $(SOURCES)

serTCP:
	$(JAVA) -cp $(BIN) serTCPsocket

cliTCP:
	$(JAVA) -cp $(BIN) cliTCPsocket $(HOST)

serUDP:
	$(JAVA) -cp $(BIN) serUDPsocket

cliUDP:
	$(JAVA) -cp $(BIN) cliUDPsocket $(HOST)

clean:
	$(RM) -r $(BIN)
