package com.taller.dominio.evento;

/**
 * Para qué sirve: Se usó un repuesto en la orden. Lo emite OrdenTrabajo; lo escucha M1.
 * <p>PATRÓN Eventos de dominio (DDD): hecho inmutable del negocio; los manejadores que lo escuchan
 *     están indicados arriba.
 */
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
