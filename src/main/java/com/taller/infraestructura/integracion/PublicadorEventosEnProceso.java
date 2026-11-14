package com.taller.infraestructura.integracion;

import com.taller.aplicacion.manejador.ManejadorEventos;
import com.taller.dominio.evento.EventoDominio;
import com.taller.dominio.puerto.PublicadorEventos;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Bus de eventos en proceso.
 *
 * <p>Para 40 ordenes diarias una cola externa seria infraestructura sin
 * problema que resolver (ADR-002). El contrato es el puerto, asi que sustituir
 * esto por RabbitMQ o Kafka no toca ni el dominio ni los servicios.
 *
 * <p>El fallo de un manejador no detiene a los demas: se registra y se sigue.
 * Una notificacion caida no puede impedir que el inventario se actualice.
 */
@Component
public class PublicadorEventosEnProceso implements PublicadorEventos {

    private static final Logger log = LoggerFactory.getLogger(PublicadorEventosEnProceso.class);

    private final List<ManejadorEventos> manejadores;

    public PublicadorEventosEnProceso(List<ManejadorEventos> manejadores) {
        this.manejadores = manejadores;
    }

    @Override
    public void publicar(EventoDominio evento) {
        if (evento == null) {
            return;
        }
        for (ManejadorEventos manejador : manejadores) {
            if (!manejador.soporta(evento.getTipo())) {
                continue;
            }
            try {
                manejador.manejar(evento);
            } catch (RuntimeException e) {
                log.error("El manejador {} fallo procesando {} (id {}): {}",
                        manejador.nombre(), evento.getTipo(), evento.getId(), e.getMessage(), e);
            }
        }
    }
}
