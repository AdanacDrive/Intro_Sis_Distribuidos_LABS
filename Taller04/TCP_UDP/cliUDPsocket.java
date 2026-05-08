/* ***********************************************************************************************
 *                              = PONTIFICIA UNIVERSIDAD JAVERIANA =
 *
 *      Autores: Juan Diego Ariza Lopez, Juan Diego Pardo, Juan Sebastian Urbano
 *      Materia: Introduccion a los Sistemas Distribuidos
 *      Profesor: J. Corredor Franco
 *      Taller 04: Hilos y Sockets
 *      Fecha: 8 de mayo de 2026
 *
 *      Archivo: cliUDPsocket.java
 *      Descripcion: Cliente UDP que envia datagramas a un servidor en el puerto 6000.
 *                   No establece una conexion previa: cada mensaje se envia de forma
 *                   independiente y sin garantia de entrega ni orden.
 * *********************************************************************************************** */

import java.net.*;
import java.io.*;

public class cliUDPsocket {

    public static void main(String argv[]) {

        if (argv.length == 0) {
            System.err.println("Java socketudpcli servidor");
            System.exit(1);
        }

        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));

        System.out.println("Prueba de sockets UDP (cliente)");

        DatagramSocket socket;
        InetAddress address;
        byte[] mensaje_bytes;
        String mensaje = "";
        DatagramPacket paquete;

        try {
            // Creacion del socket UDP (sin puerto fijo: el SO asigna uno)
            System.out.print("Creando socket... ");
            socket = new DatagramSocket();
            System.out.println("ok");

            // Resolucion de la direccion del servidor
            System.out.print("Capturando direccion de host... ");
            address = InetAddress.getByName(argv[0]);
            System.out.println("ok");

            System.out.println("Introduce mensajes a enviar:");

            // Bucle de envio de datagramas
            do {
                mensaje = in.readLine();

                mensaje_bytes = mensaje.getBytes();

                // Cada datagrama lleva direccion y puerto destino
                paquete = new DatagramPacket(mensaje_bytes, mensaje.length(), address, 6000);

                socket.send(paquete);
            } while (!mensaje.startsWith("fin"));

            socket.close();
        }
        catch (Exception e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }
}
