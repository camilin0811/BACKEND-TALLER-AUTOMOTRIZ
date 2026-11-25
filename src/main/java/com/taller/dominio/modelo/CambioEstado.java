package com.taller.dominio.modelo;

import java.time.Instant;

/**
 * Para qué sirve: Registro de un cambio de estado de la orden: a qué estado, cuándo y quién lo
 * hizo.
 * <p>PATRÓN Objeto de valor (DDD): record inmutable que se identifica por sus valores, no por un id.
 */
public record CambioEstado(EstadoOrden estado, Instant fecha, String usuario) {
}
