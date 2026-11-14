package com.taller.dominio.evento;

public class FacturaValidada extends EventoDominio {

    private final String idFactura;
    private final String idOrden;
    private final String cufe;

    public FacturaValidada(String idFactura, String idOrden, String cufe) {
        this.idFactura = idFactura;
        this.idOrden = idOrden;
        this.cufe = cufe;
    }

    public String getIdFactura() {
        return idFactura;
    }

    public String getIdOrden() {
        return idOrden;
    }

    public String getCufe() {
        return cufe;
    }
}
