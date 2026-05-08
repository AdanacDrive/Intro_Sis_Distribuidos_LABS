/* ***********************************************************************************************
 *                              = PONTIFICIA UNIVERSIDAD JAVERIANA =
 *
 *      Autores: Juan Diego Ariza Lopez, Juan Diego Pardo, Juan Sebastian Urbano
 *      Materia: Introduccion a los Sistemas Distribuidos
 *      Profesor: J. Corredor Franco
 *      Taller 04: Hilos y Sockets
 *      Fecha: 8 de mayo de 2026
 *
 *      Archivo: serTCPsocket.java
 *      Descripcion: Servidor TCP que escucha en el puerto 6001 y recibe mensajes
 *                   enviados por un cliente mediante una conexion orientada a flujo.
 * *********************************************************************************************** */

import java.net.*;
import java.io.*;

public class serTCPsocket {
    public static void main(String argv[]) {

        System.out.println("\n\n\t=**SOCKETS TCP <<SERVIDOR>>");

        ServerSocket socket;
        boolean fin = false;

        try {
            // Se crea el socket de servidor en el puerto 6001
            socket = new ServerSocket(6001);

            // El servidor queda bloqueado hasta que un cliente se conecta
            Socket socket_cli = socket.accept();

            // Flujo de entrada para leer los datos enviados por el cliente
            DataInputStream in =
                new DataInputStream(socket_cli.getInputStream());

            // Bucle de recepcion de mensajes
            do {
                String mensaje = "";
                mensaje = in.readUTF();
                System.out.println(mensaje);
                if (mensaje.startsWith("fin")) fin = true;
            } while (!fin);

            socket_cli.close();
            socket.close();
        }
        catch (Exception e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }
}
