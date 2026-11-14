package com.taller.dominio.evento;

public class VehiculoEntregado extends EventoDominio {

    private final String idOrden;
    private final String placa;
    private final int kilometraje;
    private final com.taller.dominio.modelo.TipoVehiculo tipoVehiculo;

    public VehiculoEntregado(String idOrden, String placa, int kilometraje, com.taller.dominio.modelo.TipoVehiculo tipoVehiculo) {
        this.idOrden = idOrden;
        this.placa = placa;
        this.kilometraje = kilometraje;
        this.tipoVehiculo = tipoVehiculo;
    }

    public String getIdOrden() {
        return idOrden;
    }

    public String getPlaca() {
        return placa;
    }

    public int getKilometraje() {
        return kilometraje;
    }

    public com.taller.dominio.modelo.TipoVehiculo getTipoVehiculo() {
        return tipoVehiculo;
    }
}
