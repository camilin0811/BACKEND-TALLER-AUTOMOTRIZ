package com.taller.dominio.evento;

public class RepuestoConsumido extends EventoDominio {

    private final String idOrden;
    private final String codigoRepuesto;
    private final int cantidad;

    public RepuestoConsumido(String idOrden, String codigoRepuesto, int cantidad) {
        this.idOrden = idOrden;
        this.codigoRepuesto = codigoRepuesto;
        this.cantidad = cantidad;
    }

    public String getIdOrden() {
        return idOrden;
    }

    public String getCodigoRepuesto() {
        return codigoRepuesto;
    }

    public int getCantidad() {
        return cantidad;
    }
}
