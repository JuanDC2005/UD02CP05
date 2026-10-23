package org.example;

public class Main {
    public static void main(String[] args) {
        // Creamos la instancia del ascensor
        Ascensor ascensor = new Ascensor();

        // Creamos los usuarios según los datos del caso de prueba
        Usuario u1 = new Usuario(1, 0, 3, ascensor);
        Usuario u2 = new Usuario(2, 2, -1, ascensor);
        Usuario u3 = new Usuario(3, 1, 5, ascensor);

        // Añadimos los usuarios a la cola del ascensor
        ascensor.agregarUsuario(u1);
        ascensor.agregarUsuario(u2);
        ascensor.agregarUsuario(u3);

        // Iniciamos el hilo del ascensor y los hilos de los usuarios
        ascensor.start();
        u1.start();
        u2.start();
        u3.start();

        // Esperamos a que todos los hilos terminen su ejecución
        try {
            u1.join();
            u2.join();
            u3.join();
            ascensor.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
