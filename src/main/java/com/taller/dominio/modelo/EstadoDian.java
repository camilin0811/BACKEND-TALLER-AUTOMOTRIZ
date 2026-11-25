package com.taller.dominio.modelo;

/**
 * Para qué sirve: Estados de una factura frente a la DIAN.
 * <p>Patrones: ninguno de diseño específico; es una pieza sencilla de su capa.
 */
public enum EstadoDian {
    PENDIENTE_ENVIO,
    ENVIADA,
    VALIDADA,
    RECHAZADA
}
