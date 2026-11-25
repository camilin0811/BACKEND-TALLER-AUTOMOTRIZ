package com.taller.dominio.evento;

import java.time.Instant;
import java.util.UUID;

/**
 * Hecho ocurrido en el negocio. Inmutable y expresado en participio pasado.
 *
 * <p>Los servicios de negocio publican eventos; los manejadores reaccionan.
 * Ningun servicio conoce a sus manejadores (ADR-002).
 *
 * <p>Para qué sirve: Base de todos los eventos de dominio: identificador único y fecha en que ocurrió.
 * <p>PATRÓN Eventos de dominio (DDD): cada evento es un hecho inmutable, nombrado en pasado
 *     (OrdenCerrada, CotizacionAprobada...).
 * <p>PATRÓN Observer (publicar/suscribir): es el mensaje que el emisor (PublicadorEventos) entrega a
 *     los observadores (subclases de ManejadorEventos).
 */
public abstract class EventoDominio {

    private final String id;
    private final Instant fechaHora;

    protected EventoDominio() {
        this.id = UUID.randomUUID().toString();
        this.fechaHora = Instant.now();
    }

    public String getId() {
        return id;
    }

    public Instant getFechaHora() {
        return fechaHora;
    }

    /** Nombre simple de la clase, usado como discriminador al persistir. */
    public String getTipo() {
        return getClass().getSimpleName();
    }
}
