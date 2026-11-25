package com.taller.aplicacion.manejador;

import com.taller.dominio.evento.CotizacionAprobada;
import com.taller.dominio.evento.CotizacionEmitida;
import com.taller.dominio.evento.EventoDominio;
import com.taller.dominio.evento.FacturaValidada;
import com.taller.dominio.evento.OrdenCerrada;
import com.taller.dominio.puerto.ClienteRepositorio;
import com.taller.dominio.puerto.NotificadorCliente;
import com.taller.dominio.puerto.OrdenTrabajoRepositorio;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Set;

/** M2: traduce eventos en mensajes al cliente. */
/**
 * Para qué sirve: M2: avisa al cliente por WhatsApp de la cotización, de su aprobación y de que el
 * vehículo está listo.
 * <p>PATRÓN Observer (publicar/suscribir): es un observador concreto: soporta() declara qué eventos le
 *     interesan y manejar() reacciona; el servicio que emite el evento no sabe que existe.
 */
@Component
public class ManejadorNotificaciones extends ManejadorEventos {

    private static final Logger log = LoggerFactory.getLogger(ManejadorNotificaciones.class);

    private static final Set<String> SOPORTADOS = Set.of(
            "CotizacionEmitida", "CotizacionAprobada", "OrdenCerrada", "FacturaValidada");

    private final NotificadorCliente notificador;
    private final ClienteRepositorio clientes;
    private final OrdenTrabajoRepositorio ordenes;

    public ManejadorNotificaciones(NotificadorCliente notificador, ClienteRepositorio clientes,
                                   OrdenTrabajoRepositorio ordenes) {
        this.notificador = notificador;
        this.clientes = clientes;
        this.ordenes = ordenes;
    }

    @Override
    public boolean soporta(String tipoEvento) {
        return SOPORTADOS.contains(tipoEvento);
    }

    @Override
    public void manejar(EventoDominio evento) {
        switch (evento) {
            case CotizacionEmitida e -> {
                if (e.getTelefonoCliente() != null) {
                    notificador.enviarCotizacion(e.getTelefonoCliente(), e.getNumeroCotizacion(), e.getTotal());
                }
            }
            case CotizacionAprobada e -> telefonoDeLaOrden(e.getIdOrden()).ifPresent(tel ->
                    notificador.enviarMensaje(tel, "Cotización aprobada. Su vehículo entró a reparación."));
            case OrdenCerrada e -> clientes.buscarPorId(e.getIdCliente()).ifPresent(cliente -> {
                if (cliente.getTelefono() != null) {
                    notificador.enviarMensaje(cliente.getTelefono(),
                            "Su vehículo está listo para recoger. Total: %s".formatted(e.getTotal()));
                }
            });
            case FacturaValidada e -> log.info("Factura {} validada con CUFE {}", e.getIdFactura(), e.getCufe());
            default -> log.debug("Evento {} ignorado por {}", evento.getTipo(), nombre());
        }
    }

    /** El telefono se lee de la copia que la orden guarda del cliente. */
    private java.util.Optional<String> telefonoDeLaOrden(String idOrden) {
        return ordenes.buscarPorId(idOrden)
                .map(com.taller.dominio.modelo.OrdenTrabajo::getCliente)
                .map(com.taller.dominio.modelo.ReferenciaCliente::telefono);
    }
}
