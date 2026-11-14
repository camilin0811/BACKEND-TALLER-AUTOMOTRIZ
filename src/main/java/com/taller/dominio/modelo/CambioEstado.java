package com.taller.dominio.modelo;

import java.time.Instant;

public record CambioEstado(EstadoOrden estado, Instant fecha, String usuario) {
}
