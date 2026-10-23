package org.example;

import java.util.LinkedList;

public class Ascensor extends Thread {
    private int pisoActual;
    private LinkedList<Usuario> colaUsuarios;
    private Usuario usuarioActual;
    private boolean listoParaSubir;
    private boolean listoParaBajar;
    private boolean usuarioDentro;

    public Ascensor() {
        this.pisoActual = 0; // Comienza en el piso 0
        this.colaUsuarios = new LinkedList<>();
        this.usuarioActual = null;
        this.listoParaSubir = false;
        this.listoParaBajar = false;
        this.usuarioDentro = false;
    }

    // Método para añadir usuarios a la cola del ascensor
    public synchronized void agregarUsuario(Usuario usuario) {
        colaUsuarios.add(usuario);
    }

    // Método sincronizado para que el usuario espere y suba al ascensor
    public synchronized void subir(Usuario u) throws InterruptedException {
        // Espera hasta que sea el turno de este usuario
        while (usuarioActual != u) {
            wait();
        }

        System.out.println("Usuario " + u.getIdUsuario() + " espera en piso " + u.getOrigen());

        // Espera a que el ascensor llegue al piso de origen y esté listo para recibirlo
        while (pisoActual != u.getOrigen() || !listoParaSubir) {
            wait();
        }

        System.out.println("Usuario " + u.getIdUsuario() + " sube al ascensor.");
        usuarioDentro = true;
        notifyAll(); // Notifica al ascensor de que el usuario ya ha subido
    }

    // Método sincronizado para que el usuario espere y baje en su destino
    public synchronized void bajar(Usuario u) throws InterruptedException {
        // Espera a que el ascensor llegue al piso de destino y esté listo para que baje
        while (pisoActual != u.getDestino() || !listoParaBajar) {
            wait();
        }

        System.out.println("Usuario " + u.getIdUsuario() + " baja en piso " + u.getDestino());
        usuarioDentro = false;
        notifyAll(); // Notifica al ascensor de que el usuario ya ha bajado
    }

    // Movimiento piso a piso del ascensor
    private void moverseA(int pisoDestino) {
        while (pisoActual != pisoDestino) {
            if (pisoActual < pisoDestino) {
                pisoActual++;
            } else {
                pisoActual--;
            }
            System.out.println("Ascensor en piso " + pisoActual);
            try {
                Thread.sleep(400); // Pausa para simular el desplazamiento piso a piso
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void run() {
        try {
            while (!colaUsuarios.isEmpty()) {
                Usuario u = colaUsuarios.poll();

                // 1. Asignar el siguiente usuario como usuario actual y avisar
                synchronized (this) {
                    usuarioActual = u;
                    notifyAll();
                }

                // Pequeña pausa para asegurar la concurrencia y el orden de los mensajes
                Thread.sleep(100);

                // 2. Desplazarse al piso de origen del usuario si no está ya en él
                if (pisoActual != u.getOrigen()) {
                    moverseA(u.getOrigen());
                }

                // 3. Indicar que está listo para que el usuario suba y esperar a que suba
                synchronized (this) {
                    listoParaSubir = true;
                    notifyAll();
                    while (!usuarioDentro) {
                        wait();
                    }
                    listoParaSubir = false;
                }

                // 4. Desplazarse al piso de destino del usuario
                moverseA(u.getDestino());

                // 5. Indicar que está listo para que el usuario baje y esperar a que baje
                synchronized (this) {
                    listoParaBajar = true;
                    notifyAll();
                    while (usuarioDentro) {
                        wait();
                    }
                    listoParaBajar = false;
                    usuarioActual = null;
                }
            }

            // 6. Cuando no hay más usuarios pendientes, bajar solo hasta el piso -1 y finalizar
            if (pisoActual != -1) {
                moverseA(-1);
            }
            System.out.println("Ascensor llegó a piso -1. Fin del programa.");

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
