/* ***********************************************************************************************
 *                              = PONTIFICIA UNIVERSIDAD JAVERIANA =
 *
 *      Autores: Juan Diego Ariza Lopez, Juan Diego Pardo, Juan Sebastian Urbano
 *      Materia: Introduccion a los Sistemas Distribuidos
 *      Profesor: J. Corredor Franco
 *      Taller 04: Hilos y Sockets
 *      Fecha: 8 de mayo de 2026
 *
 *      Archivo: MainThread.java
 *      Descripcion: Punto de entrada de la version PARALELA usando HERENCIA de Thread.
 *                   Cada cajera se instancia como un objeto CajeraThread y se lanza
 *                   con start(), de modo que las dos cajeras procesan a sus clientes
 *                   simultaneamente.
 * *********************************************************************************************** */

package tallerThreads;

public class MainThread {

    public static void main(String[] args) {

        Cliente cliente1 = new Cliente("Cliente 1", new int[] { 2, 2, 1, 5, 2, 3 });
        Cliente cliente2 = new Cliente("Cliente 2", new int[] { 1, 3, 5, 1, 1 });

        long initialTime = System.currentTimeMillis();

        // Cada cajera es un Thread independiente
        CajeraThread cajera1 = new CajeraThread("Cajera 1", cliente1, initialTime);
        CajeraThread cajera2 = new CajeraThread("Cajera 2", cliente2, initialTime);

        // start() invoca run() en un nuevo hilo, no bloquea
        cajera1.start();
        cajera2.start();
    }
}
