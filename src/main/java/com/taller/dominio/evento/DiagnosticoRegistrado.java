package com.taller.dominio.evento;

public class DiagnosticoRegistrado extends EventoDominio {

    private final String idOrden;
    private final java.util.List<String> codigosFalla;

    public DiagnosticoRegistrado(String idOrden, java.util.List<String> codigosFalla) {
        this.idOrden = idOrden;
        this.codigosFalla = codigosFalla;
    }

    public String getIdOrden() {
        return idOrden;
    }

    public java.util.List<String> getCodigosFalla() {
        return codigosFalla;
    }
}
