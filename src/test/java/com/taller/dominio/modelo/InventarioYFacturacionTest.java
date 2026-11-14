package com.taller.dominio.modelo;

import com.taller.dominio.excepcion.ReglaNegocioException;
import com.taller.dominio.excepcion.StockInsuficienteException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Inventario, compras y facturacion")
class InventarioYFacturacionTest {

    private static Repuesto pastillas(int stock, int minimo) {
        return Repuesto.nuevo("PAST-001", "Pastillas delanteras", new BigDecimal("90000"), stock, minimo);
    }

    @Nested
    @DisplayName("Repuesto")
    class RepuestoTest {

        @Test
        @DisplayName("descontar reduce el stock")
        void descuentoNormal() {
            Repuesto r = pastillas(10, 5);
            r.descontar(2);
            assertThat(r.getStock()).isEqualTo(8);
        }

        @Test
        @DisplayName("el stock nunca queda negativo")
        void stockNuncaNegativo() {
            Repuesto r = pastillas(1, 5);

            assertThatThrownBy(() -> r.descontar(3))
                    .isInstanceOf(StockInsuficienteException.class)
                    .hasMessageContaining("PAST-001")
                    .hasMessageContaining("1")
                    .hasMessageContaining("3");
            assertThat(r.getStock()).as("el stock no cambia si la operacion falla").isEqualTo(1);
        }

        @Test
        @DisplayName("la excepcion lleva los datos para que la API los exponga")
        void excepcionConDatos() {
            Repuesto r = pastillas(1, 5);
            StockInsuficienteException ex = org.junit.jupiter.api.Assertions.assertThrows(
                    StockInsuficienteException.class, () -> r.descontar(3));

            assertThat(ex.getCodigoRepuesto()).isEqualTo("PAST-001");
            assertThat(ex.getDisponible()).isEqualTo(1);
            assertThat(ex.getSolicitado()).isEqualTo(3);
            assertThat(ex.getCodigo()).isEqualTo("STOCK_INSUFICIENTE");
        }

        @Test
        @DisplayName("requiere reposicion al tocar el minimo, no al bajar de el")
        void reposicionAlTocarElMinimo() {
            Repuesto r = pastillas(6, 5);
            assertThat(r.requiereReposicion()).isFalse();

            r.descontar(1);
            assertThat(r.getStock()).isEqualTo(5);
            assertThat(r.requiereReposicion()).isTrue();
        }

        @Test
        @DisplayName("reponer suma al stock")
        void reposicion() {
            Repuesto r = pastillas(5, 5);
            r.reponer(20);
            assertThat(r.getStock()).isEqualTo(25);
            assertThat(r.requiereReposicion()).isFalse();
        }

        @Test
        @DisplayName("cantidades no positivas se rechazan")
        void cantidadesInvalidas() {
            Repuesto r = pastillas(10, 5);
            assertThatThrownBy(() -> r.descontar(0)).isInstanceOf(IllegalArgumentException.class);
            assertThatThrownBy(() -> r.reponer(-1)).isInstanceOf(IllegalArgumentException.class);
            assertThatThrownBy(() -> r.ajustarA(-1)).isInstanceOf(IllegalArgumentException.class);
        }
    }

    @Nested
    @DisplayName("MovimientoInventario")
    class Movimientos {

        @Test
        @DisplayName("la suma algebraica de los movimientos coincide con el stock")
        void sumaCuadraConStock() {
            List<MovimientoInventario> movimientos = List.of(
                    MovimientoInventario.entrada("PAST-001", 20, "Compra inicial", 20),
                    MovimientoInventario.salida("PAST-001", 2, "ot-1", 18),
                    MovimientoInventario.salida("PAST-001", 3, "ot-2", 15));

            int suma = movimientos.stream().mapToInt(MovimientoInventario::cantidadConSigno).sum();

            assertThat(suma).isEqualTo(15);
            assertThat(movimientos.get(movimientos.size() - 1).getStockResultante()).isEqualTo(suma);
        }
    }

    @Nested
    @DisplayName("OrdenCompra")
    class Compras {

        private OrdenCompra borrador() {
            return OrdenCompra.borradorPorReposicion("prov-1", "PAST-001", 20, new BigDecimal("70000"));
        }

        @Test
        @DisplayName("la reposicion automatica nace en BORRADOR, nunca enviada")
        void naceEnBorrador() {
            assertThat(borrador().getEstado()).isEqualTo(EstadoOrdenCompra.BORRADOR);
        }

        @Test
        @DisplayName("la recepcion completa cierra la orden")
        void recepcionCompleta() {
            OrdenCompra oc = borrador();
            oc.enviar();
            oc.registrarRecepcion("PAST-001", 20);

            assertThat(oc.getEstado()).isEqualTo(EstadoOrdenCompra.RECIBIDA);
        }

        @Test
        @DisplayName("la recepcion parcial deja la orden abierta")
        void recepcionParcial() {
            OrdenCompra oc = borrador();
            oc.enviar();
            oc.registrarRecepcion("PAST-001", 12);

            assertThat(oc.getEstado()).isEqualTo(EstadoOrdenCompra.RECIBIDA_PARCIAL);
            assertThat(oc.getItems().get(0).pendiente()).isEqualTo(8);
        }

        @Test
        @DisplayName("no se recibe un repuesto que no esta en la orden")
        void repuestoAjeno() {
            OrdenCompra oc = borrador();

            assertThatThrownBy(() -> oc.registrarRecepcion("FILT-009", 5))
                    .isInstanceOf(ReglaNegocioException.class)
                    .hasMessageContaining("FILT-009");
        }

        @Test
        @DisplayName("el total suma los subtotales de los items")
        void totalDeLaCompra() {
            assertThat(borrador().total()).isEqualByComparingTo(new BigDecimal("1400000.00"));
        }
    }

    @Nested
    @DisplayName("Factura")
    class Facturas {

        private Factura factura() {
            return Factura.emitirPara("ot-1", OrdenTrabajoMother.cliente(),
                    new BigDecimal("300000"), new BigDecimal("0.19"));
        }

        @Test
        @DisplayName("calcula IVA y total a partir del subtotal")
        void calculoDeTotales() {
            Factura f = factura();

            assertThat(f.getSubtotal()).isEqualByComparingTo(new BigDecimal("300000.00"));
            assertThat(f.getIva()).isEqualByComparingTo(new BigDecimal("57000.00"));
            assertThat(f.getTotal()).isEqualByComparingTo(new BigDecimal("357000.00"));
        }

        @Test
        @DisplayName("nace pendiente de envio y sin CUFE")
        void naceSinCufe() {
            Factura f = factura();

            assertThat(f.getEstadoDian()).isEqualTo(EstadoDian.PENDIENTE_ENVIO);
            assertThat(f.getCufe()).isNull();
            assertThat(f.estaValidada()).isFalse();
        }

        @Test
        @DisplayName("solo esta validada cuando la DIAN devuelve CUFE")
        void validacionConCufe() {
            Factura f = factura();
            f.marcarEnviada();
            f.marcarValidada("a1b2c3d4e5f6");

            assertThat(f.estaValidada()).isTrue();
            assertThat(f.getCufe()).isEqualTo("a1b2c3d4e5f6");
            assertThat(f.getFechaValidacion()).isNotNull();
            assertThat(f.getIntentosEnvio()).isEqualTo(1);
        }

        @Test
        @DisplayName("un CUFE vacio no valida la factura")
        void cufeVacio() {
            Factura f = factura();

            assertThatThrownBy(() -> f.marcarValidada("  "))
                    .isInstanceOf(ReglaNegocioException.class);
        }

        @Test
        @DisplayName("el rechazo guarda el motivo y no pone CUFE")
        void rechazoDian() {
            Factura f = factura();
            f.marcarRechazada("NIT del adquiriente invalido");

            assertThat(f.getEstadoDian()).isEqualTo(EstadoDian.RECHAZADA);
            assertThat(f.getMotivoRechazo()).contains("NIT");
            assertThat(f.estaValidada()).isFalse();
        }

        @Test
        @DisplayName("el pago total deja la factura saldada")
        void pagoTotal() {
            Factura f = factura();
            f.registrarPago(Pago.de(new BigDecimal("357000"), MetodoPago.EFECTIVO, null));

            assertThat(f.estaSaldada()).isTrue();
            assertThat(f.saldoPendiente()).isEqualByComparingTo(BigDecimal.ZERO);
        }

        @Test
        @DisplayName("un pago no puede exceder el saldo pendiente")
        void pagoExcedeSaldo() {
            Factura f = factura();
            f.registrarPago(Pago.de(new BigDecimal("200000"), MetodoPago.TARJETA, "APR-1"));

            assertThatThrownBy(() -> f.registrarPago(Pago.de(new BigDecimal("200000"), MetodoPago.EFECTIVO, null)))
                    .isInstanceOf(ReglaNegocioException.class)
                    .hasMessageContaining("157000.00");
        }

        @Test
        @DisplayName("acepta pagos parciales hasta saldar")
        void pagosParciales() {
            Factura f = factura();
            f.registrarPago(Pago.de(new BigDecimal("157000"), MetodoPago.TRANSFERENCIA, "TRF-1"));
            assertThat(f.estaSaldada()).isFalse();

            f.registrarPago(Pago.de(new BigDecimal("200000"), MetodoPago.EFECTIVO, null));
            assertThat(f.estaSaldada()).isTrue();
            assertThat(f.getPagos()).hasSize(2);
        }

        @Test
        @DisplayName("un pago de monto cero o negativo se rechaza")
        void pagoInvalido() {
            assertThatThrownBy(() -> Pago.de(BigDecimal.ZERO, MetodoPago.EFECTIVO, null))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }

    @Nested
    @DisplayName("MantenimientoPreventivo")
    class Mantenimientos {

        @Test
        @DisplayName("el automovil se programa a 5000 km y 6 meses")
        void intervaloAutomovil() {
            MantenimientoPreventivo m = MantenimientoPreventivo.programar(
                    "ABC123", TipoVehiculo.AUTOMOVIL, 48500, 5000, 6);

            assertThat(m.getKilometrajeObjetivo()).isEqualTo(53500);
            assertThat(m.getFechaSugerida()).isEqualTo(LocalDate.now().plusMonths(6));
            assertThat(m.getTipo()).contains("automovil");
        }

        @Test
        @DisplayName("la motocicleta se programa a 3000 km y 4 meses")
        void intervaloMotocicleta() {
            MantenimientoPreventivo m = MantenimientoPreventivo.programar(
                    "XYZ12D", TipoVehiculo.MOTOCICLETA, 18200, 3000, 4);

            assertThat(m.getKilometrajeObjetivo()).isEqualTo(21200);
            assertThat(m.getFechaSugerida()).isEqualTo(LocalDate.now().plusMonths(4));
            assertThat(m.getTipo()).contains("motocicleta");
        }

        @Test
        @DisplayName("vence por kilometraje aunque falte la fecha")
        void vencePorKilometraje() {
            MantenimientoPreventivo m = MantenimientoPreventivo.programar(
                    "ABC123", TipoVehiculo.AUTOMOVIL, 48500, 5000, 6);

            assertThat(m.estaVencido(LocalDate.now(), 53600)).isTrue();
            assertThat(m.estaVencido(LocalDate.now(), 50000)).isFalse();
        }

        @Test
        @DisplayName("una vez notificado deja de estar vencido")
        void noSeNotificaDosVeces() {
            MantenimientoPreventivo m = MantenimientoPreventivo.programar(
                    "ABC123", TipoVehiculo.AUTOMOVIL, 48500, 5000, 6);
            m.marcarNotificado();

            assertThat(m.estaVencido(LocalDate.now().plusYears(1), 99999)).isFalse();
        }
    }

    @Nested
    @DisplayName("Vehiculo")
    class Vehiculos {

        private Vehiculo automovil(int km) {
            return new Automovil(null, "abc-123", "Mazda", "3", 2019, km, "VIN", 2000,
                    "cli-1", 4, "GASOLINA", 5, "AUTOMATICA");
        }

        @Test
        @DisplayName("la placa se normaliza a mayusculas y sin guiones")
        void placaNormalizada() {
            assertThat(automovil(1000).getPlaca()).isEqualTo("ABC123");
        }

        @Test
        @DisplayName("el kilometraje nunca decrece")
        void kilometrajeNoDecrece() {
            Vehiculo v = automovil(48500);

            assertThatThrownBy(() -> v.actualizarKilometraje(40000))
                    .isInstanceOf(ReglaNegocioException.class)
                    .hasMessageContaining("48500");
            assertThat(v.getKilometraje()).isEqualTo(48500);
        }

        @Test
        @DisplayName("acepta el mismo kilometraje o uno mayor")
        void kilometrajeValido() {
            Vehiculo v = automovil(48500);
            v.actualizarKilometraje(48500);
            v.actualizarKilometraje(52000);

            assertThat(v.getKilometraje()).isEqualTo(52000);
        }

        @Test
        @DisplayName("cada subtipo reporta su propio tipo")
        void tipoPorSubclase() {
            Vehiculo moto = new Motocicleta(null, "XYZ12D", "Yamaha", "FZ", 2022, 18200, null, 150,
                    "cli-2", "MONOCILINDRICO", 4, "NAKED", true);

            assertThat(automovil(1).getTipo()).isEqualTo(TipoVehiculo.AUTOMOVIL);
            assertThat(moto.getTipo()).isEqualTo(TipoVehiculo.MOTOCICLETA);
        }

        @Test
        @DisplayName("dos vehiculos con la misma placa son el mismo")
        void identidadPorPlaca() {
            assertThat(automovil(1000)).isEqualTo(automovil(90000));
        }
    }
}
