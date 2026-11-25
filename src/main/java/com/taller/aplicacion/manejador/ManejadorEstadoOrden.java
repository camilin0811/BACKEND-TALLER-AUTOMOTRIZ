package com.taller.aplicacion.manejador;

import com.taller.dominio.evento.EventoDominio;
import com.taller.dominio.puerto.OrdenTrabajoRepositorio;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * M1: deja la traza de cada cambio de estado de la orden.
 *
 * <p>La validacion de las transiciones vive en el agregado; este manejador se
 * ocupa de la observabilidad: registrar que paso, cuando y sobre que orden.
 *
 * <p>Para qué sirve: M1: deja en el registro (log) la traza de cada evento de la orden.
 * <p>PATRÓN Observer (publicar/suscribir): es un observador concreto: soporta() declara qué eventos le
 *     interesan y manejar() reacciona; el servicio que emite el evento no sabe que existe.
 */
@Component
public class ManejadorEstadoOrden extends ManejadorEventos {

    private static final Logger log = LoggerFactory.getLogger(ManejadorEstadoOrden.class);

    private static final Set<String> SOPORTADOS = Set.of(
            "OrdenCreada", "DiagnosticoRegistrado", "CotizacionEmitida", "CotizacionAprobada",
            "CotizacionRechazada", "RepuestoConsumido", "OrdenCerrada", "VehiculoEntregado");

    private final OrdenTrabajoRepositorio ordenes;

    public ManejadorEstadoOrden(OrdenTrabajoRepositorio ordenes) {
        this.ordenes = ordenes;
    }

    @Override
    public boolean soporta(String tipoEvento) {
        return SOPORTADOS.contains(tipoEvento);
    }

    @Override
    public void manejar(EventoDominio evento) {
        log.info("[{}] evento {} id={} en {}", nombre(), evento.getTipo(), evento.getId(), evento.getFechaHora());
    }

    public long ordenesRegistradas() {
        return ordenes.contar();
    }
}
