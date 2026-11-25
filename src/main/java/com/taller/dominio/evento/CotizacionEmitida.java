package com.taller.dominio.evento;

/**
 * Para qué sirve: Se emitió la cotización. Lo emite OrdenTrabajo; lo escuchan M1 y M2 (envía la
 * cotización por WhatsApp).
 * <p>PATRÓN Eventos de dominio (DDD): hecho inmutable del negocio; los manejadores que lo escuchan
 *     están indicados arriba.
 */
public class CotizacionEmitida extends EventoDominio {

    private final String idOrden;
    private final String numeroCotizacion;
    private final java.math.BigDecimal total;
    private final String telefonoCliente;

    public CotizacionEmitida(String idOrden, String numeroCotizacion, java.math.BigDecimal total, String telefonoCliente) {
        this.idOrden = idOrden;
        this.numeroCotizacion = numeroCotizacion;
        this.total = total;
        this.telefonoCliente = telefonoCliente;
    }

    public String getIdOrden() {
        return idOrden;
    }

    public String getNumeroCotizacion() {
        return numeroCotizacion;
    }

    public java.math.BigDecimal getTotal() {
        return total;
    }

    public String getTelefonoCliente() {
        return telefonoCliente;
    }
}
