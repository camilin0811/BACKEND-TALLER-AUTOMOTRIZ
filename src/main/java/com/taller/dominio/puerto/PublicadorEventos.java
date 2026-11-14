package com.taller.dominio.puerto;

import com.taller.dominio.evento.EventoDominio;
import java.util.List;

/** Salida de los eventos de dominio hacia sus manejadores. */
public interface PublicadorEventos {

    void publicar(EventoDominio evento);

    default void publicarTodos(List<? extends EventoDominio> eventos) {
        eventos.forEach(this::publicar);
    }
}
