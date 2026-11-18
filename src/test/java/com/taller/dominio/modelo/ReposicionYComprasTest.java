package com.taller.dominio.modelo;

import com.taller.dominio.excepcion.ReglaNegocioException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Reposición y flujo de órdenes de compra")
class ReposicionYComprasTest {

    private static Repuesto filtro(int stock, int minimo) {
        return Repuesto.nuevo("FILT-009", "Filtro de aceite", new BigDecimal("28000"), stock, minimo);
    }

    private static OrdenCompra borrador(int cantidad) {
        return OrdenCompra.borradorPorReposicion("prov-1", "FILT-009", cantidad, new BigDecimal("28000"));
    }

    @Test
    @DisplayName("la reposición pide lo necesario para llegar al doble del mínimo")
    void reglaDobleMinimo() {
        // El caso que reportó QA: stock 3, mínimo 6 -> 9, no 99.
        assertThat(filtro(3, 6).cantidadReposicion()).isEqualTo(9);
        assertThat(filtro(0, 5).cantidadReposicion()).isEqualTo(10);
    }

    @Test
    @DisplayName("la reposición nunca pide cero ni cantidades negativas")
    void siemprePositiva() {
        assertThat(filtro(40, 5).cantidadReposicion()).isEqualTo(1);
    }

    @Test
    @DisplayName("un borrador permite corregir la cantidad antes de enviarlo")
    void editarBorrador() {
        OrdenCompra oc = borrador(9);
        oc.ajustarCantidad("FILT-009", 15);

        assertThat(oc.getItems().get(0).getCantidad()).isEqualTo(15);
    }

    @Test
    @DisplayName("una orden ya enviada no se edita")
    void enviadaNoSeEdita() {
        OrdenCompra oc = borrador(9);
        oc.enviar();

        assertThatThrownBy(() -> oc.ajustarCantidad("FILT-009", 15))
                .isInstanceOf(ReglaNegocioException.class);
    }

    @Test
    @DisplayName("la cantidad editada debe ser positiva")
    void cantidadPositiva() {
        assertThatThrownBy(() -> borrador(9).ajustarCantidad("FILT-009", 0))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("no se recibe mercancía de una orden que nunca se envió")
    void borradorNoSeRecibe() {
        assertThatThrownBy(() -> borrador(9).registrarRecepcion("FILT-009", 9))
                .isInstanceOf(ReglaNegocioException.class)
                .hasMessageContaining("enviada");
    }

    @Test
    @DisplayName("recepción parcial y luego total: Borrador → Enviada → Parcial → Recibida")
    void flujoCompleto() {
        OrdenCompra oc = borrador(9);
        assertThat(oc.estaAbierta()).isTrue();

        oc.enviar();
        oc.registrarRecepcion("FILT-009", 4);
        assertThat(oc.getEstado()).isEqualTo(EstadoOrdenCompra.RECIBIDA_PARCIAL);
        assertThat(oc.estaAbierta()).isTrue();

        oc.registrarRecepcion("FILT-009", 5);
        assertThat(oc.getEstado()).isEqualTo(EstadoOrdenCompra.RECIBIDA);
        assertThat(oc.estaAbierta()).isFalse();
    }

    @Test
    @DisplayName("solo la recepción y la administración pueden recibir compras")
    void permisoRecibir() {
        assertThat(Rol.RECEPCIONISTA.puede(Rol.Permiso.RECIBIR_COMPRAS)).isTrue();
        assertThat(Rol.ADMINISTRADOR.puede(Rol.Permiso.RECIBIR_COMPRAS)).isTrue();
        assertThat(Rol.MECANICO.puede(Rol.Permiso.RECIBIR_COMPRAS)).isFalse();
        assertThat(Rol.RECEPCIONISTA.puede(Rol.Permiso.ESCRIBIR_INVENTARIO)).isFalse();
    }
}
