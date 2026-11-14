package com.taller.dominio.evento;

public class OrdenCerrada extends EventoDominio {

    private final String idOrden;
    private final java.math.BigDecimal total;
    private final String idCliente;

    public OrdenCerrada(String idOrden, java.math.BigDecimal total, String idCliente) {
        this.idOrden = idOrden;
        this.total = total;
        this.idCliente = idCliente;
    }

    public String getIdOrden() {
        return idOrden;
    }

    public java.math.BigDecimal getTotal() {
        return total;
    }

    public String getIdCliente() {
        return idCliente;
    }
}
