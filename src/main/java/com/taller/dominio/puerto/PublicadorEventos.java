package com.taller.dominio.puerto;

import com.taller.dominio.evento.EventoDominio;
import java.util.List;

/** Salida de los eventos de dominio hacia sus manejadores. */
/**
 * Para qué sirve: Publica los eventos de dominio. Lo implementa PublicadorEventosEnProceso.
 * <p>PATRÓN Arquitectura hexagonal (puertos y adaptadores): puerto: interfaz que define el dominio y
 *     que la infraestructura implementa. El dominio depende de la interfaz, no de la tecnología
 *     (inversión de dependencias).
 * <p>PATRÓN Observer (publicar/suscribir): es el emisor (sujeto) visto desde el dominio: quien publica
 *     no conoce a los suscriptores.
 */
public interface PublicadorEventos {

    void publicar(EventoDominio evento);

    default void publicarTodos(List<? extends EventoDominio> eventos) {
        eventos.forEach(this::publicar);
    }
}
