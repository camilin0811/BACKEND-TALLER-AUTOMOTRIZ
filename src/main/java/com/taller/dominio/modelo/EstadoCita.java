package com.taller.dominio.modelo;

/**
 * Para qué sirve: Estados posibles de una cita.
 * <p>Patrones: ninguno de diseño específico; es una pieza sencilla de su capa.
 */
public enum EstadoCita {
    PENDIENTE,
    CONFIRMADA,
    ATENDIDA,
    CANCELADA
}
