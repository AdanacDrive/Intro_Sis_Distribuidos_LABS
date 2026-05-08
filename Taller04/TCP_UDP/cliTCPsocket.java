/* ***********************************************************************************************
 *                              = PONTIFICIA UNIVERSIDAD JAVERIANA =
 *
 *      Autores: Juan Diego Ariza Lopez, Juan Diego Pardo, Juan Sebastian Urbano
 *      Materia: Introduccion a los Sistemas Distribuidos
 *      Profesor: J. Corredor Franco
 *      Taller 04: Hilos y Sockets
 *      Fecha: 8 de mayo de 2026
 *
 *      Archivo: cliTCPsocket.java
 *      Descripcion: Cliente TCP que se conecta a un servidor en el puerto 6001
 *                   y envia mensajes leidos desde la entrada estandar hasta que
 *                   se envia el mensaje "fin".
 * *********************************************************************************************** */

import java.net.*;
import java.io.*;

public class cliTCPsocket {
    public static void main(String argv[]) {

        if (argv.length == 0) {
            System.err.println("JAVA cliTCPsocket <<SERVIDOR>>");
            System.exit(1);
        }

        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));

        System.out.println("Prueba de sockets TCP (CLIENTE)");

        Socket socket;
        InetAddress address;
        String mensaje = "";

        try {
            // Resolucion de la direccion IP del servidor
            System.out.print("Capturando direccion de host... ");
            address = InetAddress.getByName(argv[0]);
            System.out.println("ok");

            // Establecimiento de la conexion TCP (three-way handshake)
            System.out.print("Creando socket... ");
            socket = new Socket(address, 6001);
            System.out.println("ok");

            // Flujo de salida para enviar mensajes al servidor
            DataOutputStream out =
                new DataOutputStream(socket.getOutputStream());

            System.out.println("Introduce mensajes a enviar:");

            // Bucle de envio de mensajes
            do {
                mensaje = in.readLine();
                out.writeUTF(mensaje);
            } while (!mensaje.startsWith("fin"));

            socket.close();
        }
        catch (Exception e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }
}
