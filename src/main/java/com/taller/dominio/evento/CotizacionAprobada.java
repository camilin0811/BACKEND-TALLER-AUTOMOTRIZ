package com.taller.dominio.evento;

public class CotizacionAprobada extends EventoDominio {

    private final String idOrden;
    private final com.taller.dominio.modelo.CanalAprobacion canal;

    public CotizacionAprobada(String idOrden, com.taller.dominio.modelo.CanalAprobacion canal) {
        this.idOrden = idOrden;
        this.canal = canal;
    }

    public String getIdOrden() {
        return idOrden;
    }

    public com.taller.dominio.modelo.CanalAprobacion getCanal() {
        return canal;
    }
}
