/* ***********************************************************************************************
 *                              = PONTIFICIA UNIVERSIDAD JAVERIANA =
 *
 *      Autores: Juan Diego Ariza Lopez, Juan Diego Pardo, Juan Sebastian Urbano
 *      Materia: Introduccion a los Sistemas Distribuidos
 *      Profesor: J. Corredor Franco
 *      Taller 04: Hilos y Sockets
 *      Fecha: 8 de mayo de 2026
 *
 *      Archivo: Cliente.java
 *      Descripcion: Representa un cliente del supermercado. Cada cliente tiene
 *                   un nombre y un carro de compra (arreglo de enteros), donde
 *                   cada elemento simula los segundos que tarda en procesarse
 *                   un producto en la caja.
 * *********************************************************************************************** */

package tallerThreads;

public class Cliente {

    private String nombre;
    private int[] carroCompra;

    /**
     * Constructor de la clase Cliente.
     * @param nombre nombre del cliente
     * @param carroCompra arreglo con los tiempos (en segundos) de cada producto
     */
    public Cliente(String nombre, int[] carroCompra) {
        this.nombre = nombre;
        this.carroCompra = carroCompra;
    }

    public String getNombre() {
        return nombre;
    }

    public int[] getCarroCompra() {
        return carroCompra;
    }
}
