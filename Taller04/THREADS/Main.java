/* ***********************************************************************************************
 *                              = PONTIFICIA UNIVERSIDAD JAVERIANA =
 *
 *      Autores: Juan Diego Ariza Lopez, Juan Diego Pardo, Juan Sebastian Urbano
 *      Materia: Introduccion a los Sistemas Distribuidos
 *      Profesor: J. Corredor Franco
 *      Taller 04: Hilos y Sockets
 *      Fecha: 8 de mayo de 2026
 *
 *      Archivo: Main.java
 *      Descripcion: Punto de entrada de la version SECUENCIAL del taller. Crea dos
 *                   clientes y dos cajeras, y solicita procesar las compras una
 *                   detras de la otra (sin hilos). Por tanto, la cajera 2 NO
 *                   empieza hasta que la cajera 1 termina.
 * *********************************************************************************************** */

package tallerThreads;

public class Main {

    public static void main(String[] args) {

        // Dos clientes con carros de compra diferentes (cada numero = segundos)
        Cliente cliente1 = new Cliente("Cliente 1", new int[] { 2, 2, 1, 5, 2, 3 });
        Cliente cliente2 = new Cliente("Cliente 2", new int[] { 1, 3, 5, 1, 1 });

        Cajera cajera1 = new Cajera("Cajera 1");
        Cajera cajera2 = new Cajera("Cajera 2");

        long initialTime = System.currentTimeMillis();

        // Llamadas SECUENCIALES: la segunda espera a la primera
        cajera1.procesarCompra(cliente1, initialTime);
        cajera2.procesarCompra(cliente2, initialTime);
    }
}
