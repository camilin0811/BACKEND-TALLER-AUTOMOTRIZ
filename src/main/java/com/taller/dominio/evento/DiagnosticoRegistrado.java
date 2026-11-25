package com.taller.dominio.evento;

/**
 * Para qué sirve: Se registró un diagnóstico con sus códigos de falla. Lo emite OrdenTrabajo; lo
 * escucha M1.
 * <p>PATRÓN Eventos de dominio (DDD): hecho inmutable del negocio; los manejadores que lo escuchan
 *     están indicados arriba.
 */
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
