/* ***********************************************************************************************
 *                              = PONTIFICIA UNIVERSIDAD JAVERIANA =
 *
 *      Autores: Juan Diego Ariza Lopez, Juan Diego Pardo, Juan Sebastian Urbano
 *      Materia: Introduccion a los Sistemas Distribuidos
 *      Profesor: J. Corredor Franco
 *      Taller 04: Hilos y Sockets
 *      Fecha: 8 de mayo de 2026
 *
 *      Archivo: serUDPsocket.java
 *      Descripcion: Servidor UDP que escucha en el puerto 6000 y recibe datagramas
 *                   enviados por un cliente. La comunicacion no es orientada a la
 *                   conexion: cada mensaje se procesa de forma independiente.
 * *********************************************************************************************** */

import java.net.*;
import java.io.*;

public class serUDPsocket {

    public static void main(String argv[]) {

        System.out.println("Prueba de sockets UDP (servidor)");

        DatagramSocket socket;
        boolean fin = false;

        try {
            // Se crea el socket UDP en el puerto 6000
            System.out.print("Creando socket... ");
            socket = new DatagramSocket(6000);
            System.out.println("ok");

            System.out.println("Recibiendo mensajes... ");

            // Bucle de recepcion de datagramas
            do {
                byte[] mensaje_bytes = new byte[256];

                // Cada datagrama es independiente
                DatagramPacket paquete = new DatagramPacket(mensaje_bytes, 256);

                socket.receive(paquete);

                String mensaje = new String(mensaje_bytes, 0, paquete.getLength());

                System.out.println(mensaje);

                if (mensaje.startsWith("fin")) fin = true;
            } while (!fin);

            socket.close();
        }
        catch (Exception e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }
}
