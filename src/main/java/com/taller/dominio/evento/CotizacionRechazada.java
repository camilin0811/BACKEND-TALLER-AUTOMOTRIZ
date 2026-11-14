package com.taller.dominio.evento;

public class CotizacionRechazada extends EventoDominio {

    private final String idOrden;
    private final String motivo;

    public CotizacionRechazada(String idOrden, String motivo) {
        this.idOrden = idOrden;
        this.motivo = motivo;
    }

    public String getIdOrden() {
        return idOrden;
    }

    public String getMotivo() {
        return motivo;
    }
}
