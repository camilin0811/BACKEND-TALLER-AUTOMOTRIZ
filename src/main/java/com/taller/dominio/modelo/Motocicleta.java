package com.taller.dominio.modelo;

/**
 * Para qué sirve: Vehículo de dos ruedas: tipo de motor, tiempos, categoría y parrilla.
 * <p>PATRÓN Herencia y polimorfismo: especializa Vehiculo e implementa getTipo().
 */
public class Motocicleta extends Vehiculo {

    private final String tipoMotor;
    private final int numeroTiempos;
    private final String categoria;
    private final boolean llevaParrilla;

    public Motocicleta(String id, String placa, String marca, String linea, int anio, int kilometraje,
                       String vin, Integer cilindrada, String idCliente,
                       String tipoMotor, int numeroTiempos, String categoria, boolean llevaParrilla) {
        super(id, placa, marca, linea, anio, kilometraje, vin, cilindrada, idCliente);
        this.tipoMotor = tipoMotor;
        this.numeroTiempos = numeroTiempos;
        this.categoria = categoria;
        this.llevaParrilla = llevaParrilla;
    }

    @Override
    public TipoVehiculo getTipo() {
        return TipoVehiculo.MOTOCICLETA;
    }

    public String getTipoMotor() {
        return tipoMotor;
    }

    public int getNumeroTiempos() {
        return numeroTiempos;
    }

    public String getCategoria() {
        return categoria;
    }

    public boolean isLlevaParrilla() {
        return llevaParrilla;
    }
}
