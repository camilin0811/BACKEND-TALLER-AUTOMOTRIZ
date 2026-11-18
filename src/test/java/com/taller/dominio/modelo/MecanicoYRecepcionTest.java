package com.taller.dominio.modelo;

import com.taller.dominio.excepcion.ReglaNegocioException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Asignación de mecánico y recepción del vehículo")
class MecanicoYRecepcionTest {

    private static final ReferenciaMecanico OTRO = new ReferenciaMecanico("mec-2", "Ana Pérez");

    @Test
    @DisplayName("se asigna mecánico durante el diagnóstico sin mover el estado")
    void asignarEnDiagnostico() {
        OrdenTrabajo orden = OrdenTrabajoMother.enDiagnostico();

        assertThat(orden.cambiarMecanico(OrdenTrabajoMother.mecanico(), "recepcion01")).isEmpty();
        assertThat(orden.getMecanico()).isEqualTo(OrdenTrabajoMother.mecanico());
        assertThat(orden.getEstado()).isEqualTo(EstadoOrden.EN_DIAGNOSTICO);
    }

    @Test
    @DisplayName("cambiar de mecánico devuelve el anterior para liberarlo")
    void cambiarDevuelveAnterior() {
        OrdenTrabajo orden = OrdenTrabajoMother.enReparacion();
        ReferenciaMecanico anterior = orden.getMecanico();

        assertThat(orden.cambiarMecanico(OTRO, "recepcion01")).contains(anterior);
        assertThat(orden.getMecanico()).isEqualTo(OTRO);
        assertThat(orden.getEstado()).isEqualTo(EstadoOrden.EN_REPARACION);
    }

    @Test
    @DisplayName("una orden cerrada ya no se reasigna")
    void cerradaNoSeReasigna() {
        assertThatThrownBy(() -> OrdenTrabajoMother.cerrada().cambiarMecanico(OTRO, "recepcion01"))
                .isInstanceOf(ReglaNegocioException.class)
                .hasMessageContaining("Cerrada");
    }

    @Test
    @DisplayName("comenzar la reparación usa el mecánico asignado antes")
    void comenzarConElAsignado() {
        OrdenTrabajo orden = OrdenTrabajoMother.aprobada();
        orden.cambiarMecanico(OTRO, "recepcion01");

        orden.asignarMecanico(null, "recepcion01");

        assertThat(orden.getEstado()).isEqualTo(EstadoOrden.EN_REPARACION);
        assertThat(orden.getMecanico()).isEqualTo(OTRO);
    }

    @Test
    @DisplayName("sin mecánico asignado no se puede comenzar la reparación")
    void sinMecanicoNoComienza() {
        assertThatThrownBy(() -> OrdenTrabajoMother.aprobada().asignarMecanico(null, "recepcion01"))
                .isInstanceOf(ReglaNegocioException.class)
                .hasMessageContaining("mecánico");
    }

    @Test
    @DisplayName("la orden guarda el motivo que reporta el cliente y quién la recibió")
    void motivoDeIngreso() {
        OrdenTrabajo orden = OrdenTrabajo.recibir(OrdenTrabajoMother.cliente(),
                OrdenTrabajoMother.automovil(), 48500, null, "  Ruido al frenar  ", "recepcion01");

        assertThat(orden.getEstado()).isEqualTo(EstadoOrden.RECIBIDA);
        assertThat(orden.getMotivoIngreso()).isEqualTo("Ruido al frenar");
        assertThat(orden.getHistorialEstados().get(0).usuario()).isEqualTo("recepcion01");
    }

    @Test
    @DisplayName("asignar y cambiar el mecánico queda en el historial con quién lo hizo")
    void historialDeMecanico() {
        OrdenTrabajo orden = OrdenTrabajoMother.enDiagnostico();
        orden.cambiarMecanico(OrdenTrabajoMother.mecanico(), "recepcion01");
        orden.cambiarMecanico(OTRO, "admin");
        orden.cambiarMecanico(OTRO, "admin");

        assertThat(orden.getAnotaciones()).hasSize(2);
        assertThat(orden.getAnotaciones().get(0).descripcion()).isEqualTo("Mecánico asignado: Luis Rojas");
        assertThat(orden.getAnotaciones().get(0).usuario()).isEqualTo("recepcion01");
        assertThat(orden.getAnotaciones().get(1).descripcion())
                .isEqualTo("Mecánico cambiado de Luis Rojas a Ana Pérez");
        assertThat(orden.getAnotaciones().get(1).tipo()).isEqualTo(AnotacionHistorial.MECANICO);
    }

    @Test
    @DisplayName("en trabajo: de Recibida a En espera de repuesto")
    void estadosEnTrabajo() {
        assertThat(EstadoOrden.RECIBIDA.enTrabajo()).isTrue();
        assertThat(EstadoOrden.EN_ESPERA_REPUESTO.enTrabajo()).isTrue();
        assertThat(EstadoOrden.CERRADA.enTrabajo()).isFalse();
        assertThat(EstadoOrden.RECHAZADA.enTrabajo()).isFalse();
    }
}
