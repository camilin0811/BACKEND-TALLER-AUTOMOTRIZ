package com.taller.dominio.modelo;

import com.taller.dominio.evento.CotizacionAprobada;
import com.taller.dominio.evento.CotizacionEmitida;
import com.taller.dominio.evento.EventoDominio;
import com.taller.dominio.evento.OrdenCerrada;
import com.taller.dominio.evento.OrdenCreada;
import com.taller.dominio.evento.RepuestoConsumido;
import com.taller.dominio.evento.VehiculoEntregado;
import com.taller.dominio.excepcion.ReglaNegocioException;
import com.taller.dominio.excepcion.TransicionInvalidaException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Orden de trabajo")
class OrdenTrabajoTest {

    @Nested
    @DisplayName("ciclo de vida")
    class CicloDeVida {

        @Test
        @DisplayName("recorre el flujo completo hasta la entrega")
        void flujoCompleto() {
            OrdenTrabajo orden = OrdenTrabajo.recibir(
                    OrdenTrabajoMother.cliente(), OrdenTrabajoMother.automovil(), 48500, "cita-1", "recepcion01");
            assertThat(orden.getEstado()).isEqualTo(EstadoOrden.RECIBIDA);

            orden.registrarDiagnostico(Diagnostico.manual(List.of("P0128"), "frenos"), "mecanico03");
            assertThat(orden.getEstado()).isEqualTo(EstadoOrden.EN_DIAGNOSTICO);

            orden.agregarServicio(OrdenTrabajoMother.servicioFrenos());
            orden.emitirCotizacion("COT-0100", 48, "recepcion01");
            assertThat(orden.getEstado()).isEqualTo(EstadoOrden.COTIZADA);

            orden.aprobarCotizacion(CanalAprobacion.WHATSAPP, "sistema");
            assertThat(orden.getEstado()).isEqualTo(EstadoOrden.APROBADA);

            orden.asignarMecanico(OrdenTrabajoMother.mecanico(), "recepcion01");
            assertThat(orden.getEstado()).isEqualTo(EstadoOrden.EN_REPARACION);

            orden.registrarRepuesto(OrdenTrabajoMother.pastillas());
            orden.cerrar("mecanico03");
            assertThat(orden.getEstado()).isEqualTo(EstadoOrden.CERRADA);

            orden.marcarFacturada("fac-1", "recepcion01");
            orden.entregar("recepcion01");

            assertThat(orden.getEstado()).isEqualTo(EstadoOrden.ENTREGADA);
            assertThat(orden.getFechaEntrega()).isNotNull();
            assertThat(orden.getHistorialEstados()).hasSize(8);
        }

        @Test
        @DisplayName("el historial guarda cada cambio con su usuario")
        void historialConUsuario() {
            OrdenTrabajo orden = OrdenTrabajoMother.cotizada();
            assertThat(orden.getHistorialEstados())
                    .extracting(CambioEstado::estado)
                    .containsExactly(EstadoOrden.RECIBIDA, EstadoOrden.EN_DIAGNOSTICO, EstadoOrden.COTIZADA);
            assertThat(orden.getHistorialEstados().get(1).usuario()).isEqualTo("mecanico03");
        }

        @Test
        @DisplayName("una orden rechazada puede entregarse sin pasar por facturacion")
        void rechazadaSeEntrega() {
            OrdenTrabajo orden = OrdenTrabajoMother.cotizada();
            orden.rechazarCotizacion("Muy costoso", "sistema");
            assertThat(orden.getEstado()).isEqualTo(EstadoOrden.RECHAZADA);

            orden.entregar("recepcion01");
            assertThat(orden.getEstado()).isEqualTo(EstadoOrden.ENTREGADA);
        }

        @Test
        @DisplayName("la espera de repuesto se reanuda y vuelve a reparacion")
        void esperaYReanudacion() {
            OrdenTrabajo orden = OrdenTrabajoMother.enReparacion();
            orden.marcarEsperaRepuesto("mecanico03");
            assertThat(orden.getEstado()).isEqualTo(EstadoOrden.EN_ESPERA_REPUESTO);

            orden.reanudarReparacion("recepcion01");
            assertThat(orden.getEstado()).isEqualTo(EstadoOrden.EN_REPARACION);
        }
    }

    @Nested
    @DisplayName("transiciones invalidas")
    class TransicionesInvalidas {

        @Test
        @DisplayName("no se puede saltar de RECIBIDA a FACTURADA")
        void saltoIlegal() {
            OrdenTrabajo orden = OrdenTrabajoMother.recibida();

            assertThatThrownBy(() -> orden.marcarFacturada("fac-1", "recepcion01"))
                    .isInstanceOf(ReglaNegocioException.class)
                    .hasMessageContaining("cotizacion aprobada");
        }

        @Test
        @DisplayName("el mensaje de error enumera las transiciones validas")
        void mensajeEnumeraTransiciones() {
            OrdenTrabajo orden = OrdenTrabajoMother.recibida();

            assertThatThrownBy(() -> orden.cerrar("mecanico03"))
                    .isInstanceOf(TransicionInvalidaException.class)
                    .hasMessageContaining("RECIBIDA")
                    .hasMessageContaining("CERRADA")
                    .hasMessageContaining("EN_DIAGNOSTICO");
        }

        @Test
        @DisplayName("no se asigna mecanico a una orden sin aprobar")
        void mecanicoSinAprobacion() {
            OrdenTrabajo orden = OrdenTrabajoMother.cotizada();

            assertThatThrownBy(() -> orden.asignarMecanico(OrdenTrabajoMother.mecanico(), "recepcion01"))
                    .isInstanceOf(ReglaNegocioException.class)
                    .hasMessageContaining("aprobada");
        }

        @Test
        @DisplayName("no se cierra una orden en espera de repuesto")
        void cierreEnEspera() {
            OrdenTrabajo orden = OrdenTrabajoMother.enReparacion();
            orden.marcarEsperaRepuesto("mecanico03");

            assertThatThrownBy(() -> orden.cerrar("mecanico03"))
                    .isInstanceOf(TransicionInvalidaException.class);
        }

        @Test
        @DisplayName("una orden entregada no admite cambios")
        void entregadaEsSoloLectura() {
            OrdenTrabajo orden = OrdenTrabajoMother.entregada();

            assertThatThrownBy(() -> orden.registrarDiagnostico(
                    Diagnostico.manual(List.of("P0001"), "tardio"), "mecanico03"))
                    .isInstanceOf(ReglaNegocioException.class)
                    .hasMessageContaining("entregada");
        }
    }

    @Nested
    @DisplayName("cotizacion")
    class CotizacionDeLaOrden {

        @Test
        @DisplayName("no se emite una cotizacion sin items")
        void cotizacionVacia() {
            OrdenTrabajo orden = OrdenTrabajoMother.enDiagnostico();

            assertThatThrownBy(() -> orden.emitirCotizacion("COT-1", 48, "recepcion01"))
                    .isInstanceOf(ReglaNegocioException.class)
                    .hasMessageContaining("al menos un servicio");
        }

        @Test
        @DisplayName("al aprobar registra el canal y la fecha de respuesta")
        void apruebaRegistrandoCanal() {
            OrdenTrabajo orden = OrdenTrabajoMother.cotizada();
            orden.aprobarCotizacion(CanalAprobacion.WHATSAPP, "sistema");

            assertThat(orden.getCotizacion().estaAprobada()).isTrue();
            assertThat(orden.getCotizacion().getCanalAprobacion()).isEqualTo(CanalAprobacion.WHATSAPP);
            assertThat(orden.getCotizacion().getFechaRespuesta()).isNotNull();
        }

        @Test
        @DisplayName("una cotizacion vencida se detecta por su vigencia")
        void vencimiento() {
            Cotizacion cot = Cotizacion.emitir("COT-1", new BigDecimal("100000"), 48);

            assertThat(cot.estaVencida(cot.getFecha().plusSeconds(47 * 3600))).isFalse();
            assertThat(cot.estaVencida(cot.getFecha().plusSeconds(49 * 3600))).isTrue();
        }
    }

    @Nested
    @DisplayName("repuestos y total")
    class RepuestosYTotal {

        @Test
        @DisplayName("solo se registran repuestos durante la reparacion")
        void consumoFueraDeReparacion() {
            OrdenTrabajo orden = OrdenTrabajoMother.cotizada();

            assertThatThrownBy(() -> orden.registrarRepuesto(OrdenTrabajoMother.pastillas()))
                    .isInstanceOf(ReglaNegocioException.class)
                    .hasMessageContaining("EN_REPARACION");
        }

        @Test
        @DisplayName("el total suma servicios y repuestos")
        void totalDerivado() {
            OrdenTrabajo orden = OrdenTrabajoMother.enReparacion();
            orden.registrarRepuesto(OrdenTrabajoMother.pastillas());

            assertThat(orden.getTotal()).isEqualByComparingTo(new BigDecimal("300000.00"));
        }

        @Test
        @DisplayName("la cantidad de un repuesto debe ser positiva")
        void cantidadInvalida() {
            assertThatThrownBy(() -> new LineaRepuesto("PAST-001", "Pastillas", 0, new BigDecimal("90000")))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("las colecciones expuestas son de solo lectura")
        void coleccionesInmutables() {
            OrdenTrabajo orden = OrdenTrabajoMother.enReparacion();

            assertThatThrownBy(() -> orden.getServicios().add(OrdenTrabajoMother.servicioFrenos()))
                    .isInstanceOf(UnsupportedOperationException.class);
            assertThatThrownBy(() -> orden.getRepuestos().add(OrdenTrabajoMother.pastillas()))
                    .isInstanceOf(UnsupportedOperationException.class);
        }
    }

    @Nested
    @DisplayName("eventos de dominio")
    class Eventos {

        @Test
        @DisplayName("recibir una orden emite OrdenCreada")
        void emiteOrdenCreada() {
            OrdenTrabajo orden = OrdenTrabajo.recibir(
                    OrdenTrabajoMother.cliente(), OrdenTrabajoMother.automovil(), 48500, null, "recepcion01");

            assertThat(orden.eventosPendientes()).hasSize(1);
            assertThat(orden.eventosPendientes().get(0)).isInstanceOf(OrdenCreada.class);
        }

        @Test
        @DisplayName("el flujo completo emite los eventos esperados en orden")
        void secuenciaDeEventos() {
            OrdenTrabajo orden = OrdenTrabajoMother.cotizada();
            orden.aprobarCotizacion(CanalAprobacion.WHATSAPP, "sistema");
            orden.asignarMecanico(OrdenTrabajoMother.mecanico(), "recepcion01");
            orden.registrarRepuesto(OrdenTrabajoMother.pastillas());
            orden.cerrar("mecanico03");
            orden.marcarFacturada("fac-1", "recepcion01");
            orden.entregar("recepcion01");

            assertThat(orden.eventosPendientes())
                    .extracting(EventoDominio::getTipo)
                    .containsExactly("CotizacionAprobada", "RepuestoConsumido", "OrdenCerrada", "VehiculoEntregado");
        }

        @Test
        @DisplayName("limpiarEventos deja la orden lista para el siguiente ciclo")
        void limpiezaDeEventos() {
            OrdenTrabajo orden = OrdenTrabajoMother.cotizada();
            orden.aprobarCotizacion(CanalAprobacion.WHATSAPP, "sistema");
            assertThat(orden.eventosPendientes()).isNotEmpty();

            orden.limpiarEventos();
            assertThat(orden.eventosPendientes()).isEmpty();
        }

        @Test
        @DisplayName("la lista de eventos no se modifica desde fuera")
        void eventosInmutables() {
            OrdenTrabajo orden = OrdenTrabajoMother.recibida();

            assertThatThrownBy(() -> orden.eventosPendientes().clear())
                    .isInstanceOf(UnsupportedOperationException.class);
        }

        @Test
        @DisplayName("el evento de entrega lleva el tipo de vehiculo para calcular el mantenimiento")
        void entregaLlevaTipoVehiculo() {
            OrdenTrabajo orden = OrdenTrabajoMother.facturada();
            orden.entregar("recepcion01");

            VehiculoEntregado evento = (VehiculoEntregado) orden.eventosPendientes().stream()
                    .filter(VehiculoEntregado.class::isInstance).findFirst().orElseThrow();

            assertThat(evento.getTipoVehiculo()).isEqualTo(TipoVehiculo.AUTOMOVIL);
            assertThat(evento.getPlaca()).isEqualTo("ABC123");
        }

        @Test
        @DisplayName("la cotizacion emitida viaja con el total y el telefono del cliente")
        void cotizacionEmitidaLlevaDatos() {
            OrdenTrabajo orden = OrdenTrabajoMother.enDiagnostico();
            orden.agregarServicio(OrdenTrabajoMother.servicioFrenos());
            orden.emitirCotizacion("COT-0100", 48, "recepcion01");

            CotizacionEmitida evento = (CotizacionEmitida) orden.eventosPendientes().stream()
                    .filter(CotizacionEmitida.class::isInstance).findFirst().orElseThrow();

            assertThat(evento.getTotal()).isEqualByComparingTo(new BigDecimal("120000.00"));
            assertThat(evento.getTelefonoCliente()).isEqualTo("+573001112233");
        }

        @Test
        @DisplayName("cerrar emite OrdenCerrada con el total recalculado")
        void cierreLlevaTotal() {
            OrdenTrabajo orden = OrdenTrabajoMother.enReparacion();
            orden.registrarRepuesto(OrdenTrabajoMother.pastillas());
            orden.cerrar("mecanico03");

            OrdenCerrada evento = (OrdenCerrada) orden.eventosPendientes().stream()
                    .filter(OrdenCerrada.class::isInstance).findFirst().orElseThrow();

            assertThat(evento.getTotal()).isEqualByComparingTo(new BigDecimal("300000.00"));
        }

        @Test
        @DisplayName("consumir un repuesto emite el evento con codigo y cantidad")
        void consumoEmiteEvento() {
            OrdenTrabajo orden = OrdenTrabajoMother.enReparacion();
            orden.registrarRepuesto(OrdenTrabajoMother.pastillas());

            RepuestoConsumido evento = (RepuestoConsumido) orden.eventosPendientes().get(0);
            assertThat(evento.getCodigoRepuesto()).isEqualTo("PAST-001");
            assertThat(evento.getCantidad()).isEqualTo(2);
        }

        @Test
        @DisplayName("aprobar emite el canal usado")
        void aprobacionLlevaCanal() {
            OrdenTrabajo orden = OrdenTrabajoMother.cotizada();
            orden.aprobarCotizacion(CanalAprobacion.PORTAL_WEB, "cliente");

            CotizacionAprobada evento = (CotizacionAprobada) orden.eventosPendientes().get(0);
            assertThat(evento.getCanal()).isEqualTo(CanalAprobacion.PORTAL_WEB);
        }
    }
}
