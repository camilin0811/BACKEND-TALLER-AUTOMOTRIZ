package com.taller.dominio.modelo;

/**
 * Para qué sirve: Estados posibles de una cotización.
 * <p>Patrones: ninguno de diseño específico; es una pieza sencilla de su capa.
 */
public enum EstadoCotizacion {
    EMITIDA,
    APROBADA,
    RECHAZADA,
    VENCIDA
}
