package com.taller.dominio.modelo;

/**
 * Para qué sirve: Vehículo de cuatro ruedas: puertas, combustible, pasajeros y transmisión.
 * <p>PATRÓN Herencia y polimorfismo: especializa Vehiculo e implementa getTipo().
 */
public class Automovil extends Vehiculo {

    private final int numeroPuertas;
    private final String tipoCombustible;
    private final int numeroPasajeros;
    private final String tipoTransmision;

    public Automovil(String id, String placa, String marca, String linea, int anio, int kilometraje,
                     String vin, Integer cilindrada, String idCliente,
                     int numeroPuertas, String tipoCombustible, int numeroPasajeros, String tipoTransmision) {
        super(id, placa, marca, linea, anio, kilometraje, vin, cilindrada, idCliente);
        this.numeroPuertas = numeroPuertas;
        this.tipoCombustible = tipoCombustible;
        this.numeroPasajeros = numeroPasajeros;
        this.tipoTransmision = tipoTransmision;
    }

    @Override
    public TipoVehiculo getTipo() {
        return TipoVehiculo.AUTOMOVIL;
    }

    public int getNumeroPuertas() {
        return numeroPuertas;
    }

    public String getTipoCombustible() {
        return tipoCombustible;
    }

    public int getNumeroPasajeros() {
        return numeroPasajeros;
    }

    public String getTipoTransmision() {
        return tipoTransmision;
    }
}
