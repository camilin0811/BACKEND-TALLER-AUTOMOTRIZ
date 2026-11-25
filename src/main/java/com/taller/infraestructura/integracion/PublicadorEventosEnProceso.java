package com.taller.infraestructura.integracion;

import com.taller.aplicacion.manejador.ManejadorEventos;
import com.taller.dominio.evento.EventoDominio;
import com.taller.dominio.puerto.PublicadorEventos;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Component;

/**
 * Bus de eventos en proceso.
 *
 * <p>Para 40 ordenes diarias una cola externa seria infraestructura sin
 * problema que resolver (ADR-002). El contrato es el puerto, asi que sustituir
 * esto por RabbitMQ o Kafka no toca ni el dominio ni los servicios.
 *
 * <p>Los manejadores se resuelven <strong>al publicar</strong>, no al construir
 * el bus. No es un detalle: un manejador puede depender del mismo servicio que
 * emite el evento —ManejadorFacturacion usa FacturacionService, que a su vez
 * publica FacturaValidada— y pedirlos por constructor formaria un ciclo de
 * dependencias. Un bus no necesita conocer a sus suscriptores para existir.
 *
 * <p>El fallo de un manejador no detiene a los demas: se registra y se sigue.
 * Una notificacion caida no puede impedir que el inventario se actualice.
 *
 * <p>Para qué sirve: Bus de eventos dentro del mismo proceso: entrega cada evento a los manejadores
 * que lo soportan.
 * <p>PATRÓN Observer (publicar/suscribir): es el sujeto: recorre los observadores (ManejadorEventos) y
 *     notifica a los que declaran soportar el evento. Si uno falla, los demás siguen.
 * <p>PATRÓN Adapter: implementa el puerto PublicadorEventos; se podría cambiar por RabbitMQ o Kafka
 *     sin tocar el dominio.
 */
@Component
public class PublicadorEventosEnProceso implements PublicadorEventos {

    private static final Logger log = LoggerFactory.getLogger(PublicadorEventosEnProceso.class);

    private final ObjectProvider<ManejadorEventos> manejadores;

    public PublicadorEventosEnProceso(ObjectProvider<ManejadorEventos> manejadores) {
        this.manejadores = manejadores;
    }

    @Override
    // PATRÓN Observer: notifica a cada suscriptor que soporta el evento.
    public void publicar(EventoDominio evento) {
        if (evento == null) {
            return;
        }
        manejadores.stream()
                .filter(manejador -> manejador.soporta(evento.getTipo()))
                .forEach(manejador -> despachar(manejador, evento));
    }

    private void despachar(ManejadorEventos manejador, EventoDominio evento) {
        try {
            manejador.manejar(evento);
        } catch (RuntimeException e) {
            log.error("El manejador {} fallo procesando {} (id {}): {}",
                    manejador.nombre(), evento.getTipo(), evento.getId(), e.getMessage(), e);
        }
    }
}
