package com.taller.dominio.modelo;

/**
 * Para qué sirve: Estados de una orden de compra a un proveedor.
 * <p>Patrones: ninguno de diseño específico; es una pieza sencilla de su capa.
 */
public enum EstadoOrdenCompra {
    BORRADOR,
    ENVIADA,
    RECIBIDA_PARCIAL,
    RECIBIDA,
    ANULADA
}
