package com.taller.dominio.evento;

/**
 * Para qué sirve: El vehículo salió del taller. Lo emite OrdenTrabajo; lo escuchan M1 y M4
 * (programa el mantenimiento preventivo).
 * <p>PATRÓN Eventos de dominio (DDD): hecho inmutable del negocio; los manejadores que lo escuchan
 *     están indicados arriba.
 */
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
