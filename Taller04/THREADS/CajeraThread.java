/* ***********************************************************************************************
 *                              = PONTIFICIA UNIVERSIDAD JAVERIANA =
 *
 *      Autores: Juan Diego Ariza Lopez, Juan Diego Pardo, Juan Sebastian Urbano
 *      Materia: Introduccion a los Sistemas Distribuidos
 *      Profesor: J. Corredor Franco
 *      Taller 04: Hilos y Sockets
 *      Fecha: 8 de mayo de 2026
 *
 *      Archivo: CajeraThread.java
 *      Descripcion: Version PARALELA de la cajera. Esta clase EXTIENDE de Thread,
 *                   por lo que cada cajera se ejecuta en su propio hilo de
 *                   ejecucion. El metodo run() contiene la logica de procesamiento
 *                   y se invoca al llamar al metodo start().
 *                   Asi, dos cajeras pueden atender clientes EN PARALELO.
 * *********************************************************************************************** */

package tallerThreads;

public class CajeraThread extends Thread {

    private String nombre;
    private Cliente cliente;
    private long initialTime;

    public CajeraThread(String nombre, Cliente cliente, long initialTime) {
        this.nombre = nombre;
        this.cliente = cliente;
        this.initialTime = initialTime;
    }

    /**
     * Metodo que se ejecuta al invocar start() sobre la cajera.
     * Procesa los productos del cliente como un hilo independiente.
     */
    @Override
    public void run() {
        System.out.println("La cajera " + this.nombre +
                " comienza a procesar la compra del cliente " + cliente.getNombre() +
                " en el tiempo: " + (System.currentTimeMillis() - this.initialTime) / 1000 + "seg");

        for (int i = 0; i < cliente.getCarroCompra().length; i++) {
            this.esperarXsegundos(cliente.getCarroCompra()[i]);
            System.out.println("Procesado el producto " + (i + 1) +
                    " del cliente " + cliente.getNombre() +
                    " ->Tiempo: " + (System.currentTimeMillis() - this.initialTime) / 1000 + "seg");
        }

        System.out.println("La cajera " + this.nombre +
                " ha terminado de procesar " + cliente.getNombre() +
                " en el tiempo: " + (System.currentTimeMillis() - this.initialTime) / 1000 + "seg");
    }

    private void esperarXsegundos(int segundos) {
        try {
            Thread.sleep(segundos * 1000);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
    }
}
