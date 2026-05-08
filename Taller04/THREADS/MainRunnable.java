/* ***********************************************************************************************
 *                              = PONTIFICIA UNIVERSIDAD JAVERIANA =
 *
 *      Autores: Juan Diego Ariza Lopez, Juan Diego Pardo, Juan Sebastian Urbano
 *      Materia: Introduccion a los Sistemas Distribuidos
 *      Profesor: J. Corredor Franco
 *      Taller 04: Hilos y Sockets
 *      Fecha: 8 de mayo de 2026
 *
 *      Archivo: MainRunnable.java
 *      Descripcion: Version PARALELA usando la INTERFAZ Runnable. A diferencia de
 *                   MainThread (que extiende Thread), aqui la clase implementa
 *                   Runnable y se le pasa a un new Thread(...). Esta es la opcion
 *                   recomendada cuando la clase ya extiende de otra, ya que Java
 *                   no soporta herencia multiple. La cajera reusa la clase Cajera
 *                   secuencial; el paralelismo lo aporta el Thread que la envuelve.
 * *********************************************************************************************** */

package tallerThreads;

public class MainRunnable implements Runnable {

    private Cliente cliente;
    private Cajera cajera;
    private long initialTime;

    public MainRunnable(Cliente cliente, Cajera cajera, long initialTime) {
        this.cajera = cajera;
        this.cliente = cliente;
        this.initialTime = initialTime;
    }

    public static void main(String[] args) {

        Cliente cliente1 = new Cliente("Cliente 1", new int[] { 2, 2, 1, 5, 2, 3 });
        Cliente cliente2 = new Cliente("Cliente 2", new int[] { 1, 3, 5, 1, 1 });

        Cajera cajera1 = new Cajera("Cajera 1");
        Cajera cajera2 = new Cajera("Cajera 2");

        long initialTime = System.currentTimeMillis();

        // Cada proceso es un Runnable que envuelve a la cajera y al cliente
        Runnable proceso1 = new MainRunnable(cliente1, cajera1, initialTime);
        Runnable proceso2 = new MainRunnable(cliente2, cajera2, initialTime);

        // Se crean los hilos y se inician
        new Thread(proceso1).start();
        new Thread(proceso2).start();
    }

    /**
     * Metodo que ejecuta el hilo. Reutiliza la logica de la Cajera secuencial.
     */
    @Override
    public void run() {
        this.cajera.procesarCompra(this.cliente, this.initialTime);
    }
}
