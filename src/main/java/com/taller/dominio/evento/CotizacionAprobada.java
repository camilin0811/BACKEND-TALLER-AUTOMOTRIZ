package com.taller.dominio.evento;

/**
 * Para qué sirve: El cliente aprobó la cotización (por WhatsApp, portal o en persona). Lo emite
 * OrdenTrabajo; lo escuchan M1 y M2 (aviso al cliente).
 * <p>PATRÓN Eventos de dominio (DDD): hecho inmutable del negocio; los manejadores que lo escuchan
 *     están indicados arriba.
 */
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
