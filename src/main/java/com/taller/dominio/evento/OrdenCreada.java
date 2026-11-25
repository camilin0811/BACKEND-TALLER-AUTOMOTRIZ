package com.taller.dominio.evento;

/**
 * Para qué sirve: Se recibió un vehículo y se abrió su orden. Lo emite OrdenTrabajo; lo escucha M1.
 * <p>PATRÓN Eventos de dominio (DDD): hecho inmutable del negocio; los manejadores que lo escuchan
 *     están indicados arriba.
 */
public class OrdenCreada extends EventoDominio {

    private final String idOrden;
    private final String numeroOrden;
    private final String placa;
    private final String idCliente;

    public OrdenCreada(String idOrden, String numeroOrden, String placa, String idCliente) {
        this.idOrden = idOrden;
        this.numeroOrden = numeroOrden;
        this.placa = placa;
        this.idCliente = idCliente;
    }

    public String getIdOrden() {
        return idOrden;
    }

    public String getNumeroOrden() {
        return numeroOrden;
    }

    public String getPlaca() {
        return placa;
    }

    public String getIdCliente() {
        return idCliente;
    }
}
