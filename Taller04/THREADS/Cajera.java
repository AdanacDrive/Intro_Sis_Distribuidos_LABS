/* ***********************************************************************************************
 *                              = PONTIFICIA UNIVERSIDAD JAVERIANA =
 *
 *      Autores: Juan Diego Ariza Lopez, Juan Diego Pardo, Juan Sebastian Urbano
 *      Materia: Introduccion a los Sistemas Distribuidos
 *      Profesor: J. Corredor Franco
 *      Taller 04: Hilos y Sockets
 *      Fecha: 8 de mayo de 2026
 *
 *      Archivo: Cajera.java
 *      Descripcion: Representa una cajera del supermercado en su version SECUENCIAL.
 *                   La cajera procesa los productos del cliente uno a uno y bloquea
 *                   el hilo principal mientras lo hace (Thread.sleep). Por eso, cuando
 *                   se invoca cajera1.procesarCompra() y luego cajera2.procesarCompra()
 *                   la segunda solo arranca cuando la primera termina.
 * *********************************************************************************************** */

package tallerThreads;

public class Cajera {

    private String nombre;

    public Cajera(String nombre) {
        this.nombre = nombre;
    }

    /**
     * Procesa la compra de un cliente de forma secuencial (sin hilos).
     * @param cliente cliente a atender
     * @param timeStamp tiempo inicial de referencia
     */
    public void procesarCompra(Cliente cliente, long timeStamp) {
        System.out.println("La cajera " + this.nombre +
                " comienza a procesar la compra del cliente " + cliente.getNombre() +
                " en el tiempo: " + (System.currentTimeMillis() - timeStamp) / 1000 + "seg");

        // Procesa producto por producto, esperando el tiempo correspondiente
        for (int i = 0; i < cliente.getCarroCompra().length; i++) {
            this.esperarXsegundos(cliente.getCarroCompra()[i]);
            System.out.println("Procesado el producto " + (i + 1) +
                    " del cliente " + cliente.getNombre() +
                    " ->Tiempo: " + (System.currentTimeMillis() - timeStamp) / 1000 + "seg");
        }

        System.out.println("La cajera " + this.nombre +
                " ha terminado de procesar " + cliente.getNombre() +
                " en el tiempo: " + (System.currentTimeMillis() - timeStamp) / 1000 + "seg");
    }

    /**
     * Bloquea el hilo actual durante el numero de segundos indicado.
     * Simula el tiempo de procesamiento de un producto.
     */
    private void esperarXsegundos(int segundos) {
        try {
            Thread.sleep(segundos * 1000);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
    }
}
