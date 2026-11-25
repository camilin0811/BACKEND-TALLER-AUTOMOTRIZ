package com.taller.dominio.modelo;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.EnumSource;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Para qué sirve: Pruebas unitarias de la máquina de estados de la orden.
 * <p>Patrones: ninguno de diseño específico; es una pieza sencilla de su capa.
 */
@DisplayName("Maquina de estados de la orden de trabajo")
class EstadoOrdenTest {

    @ParameterizedTest(name = "{0} -> {1} es valida")
    @CsvSource({
            "RECIBIDA,           EN_DIAGNOSTICO",
            "EN_DIAGNOSTICO,     COTIZADA",
            "COTIZADA,           APROBADA",
            "COTIZADA,           RECHAZADA",
            "APROBADA,           EN_REPARACION",
            "RECHAZADA,          ENTREGADA",
            "EN_REPARACION,      EN_ESPERA_REPUESTO",
            "EN_REPARACION,      CERRADA",
            "EN_ESPERA_REPUESTO, EN_REPARACION",
            "CERRADA,            FACTURADA",
            "FACTURADA,          ENTREGADA"
    })
    void transicionesValidas(EstadoOrden desde, EstadoOrden hacia) {
        assertThat(desde.permiteTransicionA(hacia)).isTrue();
    }

    @ParameterizedTest(name = "{0} -> {1} es invalida")
    @CsvSource({
            "RECIBIDA,           FACTURADA",
            "RECIBIDA,           ENTREGADA",
            "RECIBIDA,           CERRADA",
            "EN_DIAGNOSTICO,     APROBADA",
            "COTIZADA,           EN_REPARACION",
            "COTIZADA,           CERRADA",
            "APROBADA,           CERRADA",
            "EN_ESPERA_REPUESTO, CERRADA",
            "CERRADA,            ENTREGADA",
            "ENTREGADA,          RECIBIDA",
            "ENTREGADA,          FACTURADA"
    })
    void transicionesInvalidas(EstadoOrden desde, EstadoOrden hacia) {
        assertThat(desde.permiteTransicionA(hacia)).isFalse();
    }

    @Test
    @DisplayName("ENTREGADA es el unico estado terminal")
    void unicoEstadoTerminal() {
        for (EstadoOrden estado : EstadoOrden.values()) {
            assertThat(estado.esTerminal())
                    .as("%s terminal", estado)
                    .isEqualTo(estado == EstadoOrden.ENTREGADA);
        }
    }

    @Test
    @DisplayName("una orden entregada queda en solo lectura")
    void entregadaNoAdmiteModificacion() {
        assertThat(EstadoOrden.ENTREGADA.admiteModificacion()).isFalse();
        assertThat(EstadoOrden.EN_REPARACION.admiteModificacion()).isTrue();
    }

    @Test
    @DisplayName("solo se consumen repuestos durante la reparacion")
    void consumoSoloEnReparacion() {
        for (EstadoOrden estado : EstadoOrden.values()) {
            assertThat(estado.permiteConsumoDeRepuestos())
                    .as("%s permite consumo", estado)
                    .isEqualTo(estado == EstadoOrden.EN_REPARACION);
        }
    }

    @ParameterizedTest
    @EnumSource(EstadoOrden.class)
    @DisplayName("ningun estado se declara como transicion de si mismo")
    void sinAutotransiciones(EstadoOrden estado) {
        assertThat(estado.permiteTransicionA(estado)).isFalse();
    }

    @ParameterizedTest
    @EnumSource(EstadoOrden.class)
    @DisplayName("todo estado tiene su conjunto de transiciones declarado")
    void todosLosEstadosDeclarados(EstadoOrden estado) {
        assertThat(estado.siguientesPosibles()).isNotNull();
    }

    @Test
    @DisplayName("el conjunto de transiciones no se puede modificar desde fuera")
    void conjuntoInmutable() {
        org.assertj.core.api.Assertions
                .assertThatThrownBy(() -> EstadoOrden.RECIBIDA.siguientesPosibles().add(EstadoOrden.ENTREGADA))
                .isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    @DisplayName("todo estado es alcanzable desde RECIBIDA")
    void todosAlcanzablesDesdeRecibida() {
        java.util.Set<EstadoOrden> vistos = new java.util.HashSet<>();
        java.util.Deque<EstadoOrden> porVisitar = new java.util.ArrayDeque<>();
        porVisitar.add(EstadoOrden.RECIBIDA);
        while (!porVisitar.isEmpty()) {
            EstadoOrden actual = porVisitar.pop();
            if (vistos.add(actual)) {
                porVisitar.addAll(actual.siguientesPosibles());
            }
        }
        assertThat(vistos).containsExactlyInAnyOrder(EstadoOrden.values());
    }
}
