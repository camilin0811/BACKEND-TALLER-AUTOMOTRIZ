package com.taller.dominio.evento;

/**
 * Para qué sirve: Terminó la reparación. Lo emite OrdenTrabajo; lo escuchan M1, M2 (aviso al
 * cliente) y M4 (emite la factura).
 * <p>PATRÓN Eventos de dominio (DDD): hecho inmutable del negocio; los manejadores que lo escuchan
 *     están indicados arriba.
 */
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
