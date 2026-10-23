package org.example;

public class Usuario extends Thread {
    private int idUsuario;
    private int origen;
    private int destino;
    private Ascensor ascensor;

    public Usuario(int idUsuario, int origen, int destino, Ascensor ascensor) {
        this.idUsuario = idUsuario;
        this.origen = origen;
        this.destino = destino;
        this.ascensor = ascensor;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public int getOrigen() {
        return origen;
    }

    public int getDestino() {
        return destino;
    }

    @Override
    public void run() {
        try {
            // El usuario espera su turno, espera que el ascensor llegue a su piso de origen y sube
            ascensor.subir(this);
            // El usuario espera dentro del ascensor a que llegue a su destino y baja
            ascensor.bajar(this);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
