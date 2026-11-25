package com.taller.dominio.evento;

/**
 * Para qué sirve: El cliente rechazó la cotización. Lo emite OrdenTrabajo; lo escucha M1.
 * <p>PATRÓN Eventos de dominio (DDD): hecho inmutable del negocio; los manejadores que lo escuchan
 *     están indicados arriba.
 */
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
