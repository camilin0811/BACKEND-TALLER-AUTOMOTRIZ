package com.taller.dominio.modelo;

import com.taller.dominio.excepcion.ReglaNegocioException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Detalle de los agregados")
class DetalleDeAgregadosTest {

    @Nested
    @DisplayName("Repuesto")
    class Repuestos {

        private Repuesto conProveedores() {
            return new Repuesto("rep-1", "PAST-001", "Pastillas delanteras",
                    new BigDecimal("90000"), 10, 5, List.of("prov-1", "prov-2"));
        }

        @Test
        @DisplayName("el proveedor habitual es el primero de la lista")
        void proveedorHabitual() {
            assertThat(conProveedores().proveedorHabitual()).isEqualTo("prov-1");
            assertThat(Repuesto.nuevo("X", "X", BigDecimal.ONE, 1, 1).proveedorHabitual()).isNull();
        }

        @Test
        @DisplayName("no se repiten proveedores ni se agregan nulos")
        void agregarProveedor() {
            Repuesto r = conProveedores();
            r.agregarProveedor("prov-1");
            r.agregarProveedor(null);
            r.agregarProveedor("prov-3");

            assertThat(r.getIdsProveedores()).containsExactly("prov-1", "prov-2", "prov-3");
        }

        @Test
        @DisplayName("la lista de proveedores es de solo lectura")
        void proveedoresInmutables() {
            assertThatThrownBy(() -> conProveedores().getIdsProveedores().add("prov-9"))
                    .isInstanceOf(UnsupportedOperationException.class);
        }

        @Test
        @DisplayName("hayDisponibilidad responde al stock actual")
        void disponibilidad() {
            Repuesto r = conProveedores();

            assertThat(r.hayDisponibilidad(10)).isTrue();
            assertThat(r.hayDisponibilidad(11)).isFalse();
            assertThat(r.hayDisponibilidad(0)).isFalse();
        }

        @Test
        @DisplayName("se convierte en linea de orden con su precio vigente")
        void conversionALinea() {
            LineaRepuesto linea = conProveedores().aLinea(3);

            assertThat(linea.codigo()).isEqualTo("PAST-001");
            assertThat(linea.nombre()).isEqualTo("Pastillas delanteras");
            assertThat(linea.cantidad()).isEqualTo(3);
            assertThat(linea.subtotal()).isEqualByComparingTo(new BigDecimal("270000.00"));
        }

        @Test
        @DisplayName("el ajuste de inventario fija el stock contado")
        void ajusteDeInventario() {
            Repuesto r = conProveedores();
            r.ajustarA(7);

            assertThat(r.getStock()).isEqualTo(7);
        }

        @Test
        @DisplayName("expone id, nombre y permite cambiar el stock minimo")
        void datosYMinimo() {
            Repuesto r = conProveedores();
            r.asignarId("rep-9");
            r.cambiarStockMinimo(12);

            assertThat(r.getId()).isEqualTo("rep-9");
            assertThat(r.getNombre()).isEqualTo("Pastillas delanteras");
            assertThat(r.getStockMinimo()).isEqualTo(12);
            assertThat(r.requiereReposicion()).isTrue();
        }
    }

    @Nested
    @DisplayName("OrdenCompra e items")
    class Compras {

        private OrdenCompra borrador() {
            return OrdenCompra.borradorPorReposicion("prov-1", "PAST-001", 20, new BigDecimal("70000"));
        }

        @Test
        @DisplayName("no se envia una orden que ya fue enviada")
        void envioDoble() {
            OrdenCompra oc = borrador();
            oc.enviar();

            assertThatThrownBy(oc::enviar)
                    .isInstanceOf(ReglaNegocioException.class)
                    .hasMessageContaining("borrador");
        }

        @Test
        @DisplayName("no se envia una orden sin items")
        void envioVacio() {
            OrdenCompra vacia = new OrdenCompra(null, "OC-1", "prov-1", Instant.now(),
                    EstadoOrdenCompra.BORRADOR, List.of());

            assertThatThrownBy(vacia::enviar)
                    .isInstanceOf(ReglaNegocioException.class)
                    .hasMessageContaining("ítems");
        }

        @Test
        @DisplayName("expone id, numero, proveedor y fecha")
        void datos() {
            OrdenCompra oc = borrador();
            oc.asignarId("oc-1");
            oc.asignarNumero("OC-0012");

            assertThat(oc.getId()).isEqualTo("oc-1");
            assertThat(oc.getNumero()).isEqualTo("OC-0012");
            assertThat(oc.getIdProveedor()).isEqualTo("prov-1");
            assertThat(oc.getFecha()).isNotNull();
            assertThat(oc.getItems()).hasSize(1);
        }

        @Test
        @DisplayName("la recepcion nunca supera lo pedido: el excedente se rechaza")
        void recepcionTopada() {
            ItemOrdenCompra item = new ItemOrdenCompra("PAST-001", 20, 0, new BigDecimal("70000"));
            item.recibir(15);

            // Antes se topaba en silencio y el stock sumaba igual las 15.
            assertThatThrownBy(() -> item.recibir(15))
                    .isInstanceOf(com.taller.dominio.excepcion.ReglaNegocioException.class)
                    .hasMessageContaining("5");
            assertThat(item.getCantidadRecibida()).isEqualTo(15);
            item.recibir(5);
            assertThat(item.estaCompleto()).isTrue();
            assertThat(item.pendiente()).isZero();
        }

        @Test
        @DisplayName("una recepcion de cantidad no positiva se rechaza")
        void recepcionInvalida() {
            ItemOrdenCompra item = new ItemOrdenCompra("PAST-001", 20, 0, new BigDecimal("70000"));

            assertThatThrownBy(() -> item.recibir(0)).isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("el item expone cantidad y precio unitario")
        void datosDelItem() {
            ItemOrdenCompra item = new ItemOrdenCompra("PAST-001", 20, 5, new BigDecimal("70000"));

            assertThat(item.getCodigoRepuesto()).isEqualTo("PAST-001");
            assertThat(item.getCantidad()).isEqualTo(20);
            assertThat(item.getCantidadRecibida()).isEqualTo(5);
            assertThat(item.getPrecioUnitario()).isEqualByComparingTo(new BigDecimal("70000.00"));
            assertThat(item.pendiente()).isEqualTo(15);
        }
    }

    @Nested
    @DisplayName("MovimientoInventario")
    class Movimientos {

        @Test
        @DisplayName("la entrada suma y la salida resta")
        void signos() {
            assertThat(MovimientoInventario.entrada("PAST-001", 20, "compra", 20).cantidadConSigno())
                    .isEqualTo(20);
            assertThat(MovimientoInventario.salida("PAST-001", 2, "ot-1", 18).cantidadConSigno())
                    .isEqualTo(-2);
        }

        @Test
        @DisplayName("la salida referencia la orden que la causo")
        void trazaDeLaSalida() {
            MovimientoInventario m = MovimientoInventario.salida("PAST-001", 2, "ot-1", 18);
            m.asignarId("mov-1");

            assertThat(m.getId()).isEqualTo("mov-1");
            assertThat(m.getIdOrden()).isEqualTo("ot-1");
            assertThat(m.getTipo()).isEqualTo(TipoMovimiento.SALIDA);
            assertThat(m.getCantidad()).isEqualTo(2);
            assertThat(m.getStockResultante()).isEqualTo(18);
            assertThat(m.getCodigoRepuesto()).isEqualTo("PAST-001");
            assertThat(m.getMotivo()).contains("ot-1");
            assertThat(m.getFecha()).isNotNull();
        }

        @Test
        @DisplayName("un ajuste manual tambien queda registrado")
        void ajuste() {
            MovimientoInventario m = new MovimientoInventario(null, "PAST-001", TipoMovimiento.AJUSTE,
                    3, "Conteo fisico", null, 7, null);

            assertThat(m.cantidadConSigno()).isEqualTo(3);
            assertThat(m.getFecha()).isNotNull();
            assertThat(m.getIdOrden()).isNull();
        }
    }

    @Nested
    @DisplayName("Diagnostico")
    class Diagnosticos {

        @Test
        @DisplayName("el escaneo conserva codigos y lecturas de sensores")
        void desdeEscaner() {
            Diagnostico d = Diagnostico.desdeEscaner(List.of("P0128"),
                    Map.of("temperaturaMotor", 78), "Termostato");

            assertThat(d.origen()).isEqualTo(OrigenDiagnostico.OBD_II);
            assertThat(d.codigosFalla()).containsExactly("P0128");
            assertThat(d.lecturasSensores()).containsEntry("temperaturaMotor", 78);
            assertThat(d.observaciones()).isEqualTo("Termostato");
            assertThat(d.fecha()).isNotNull();
        }

        @Test
        @DisplayName("el registro manual no trae lecturas de sensores")
        void manual() {
            Diagnostico d = Diagnostico.manual(List.of("C1234"), "Revision visual");

            assertThat(d.origen()).isEqualTo(OrigenDiagnostico.MANUAL);
            assertThat(d.lecturasSensores()).isEmpty();
        }

        @Test
        @DisplayName("tolera listas nulas y las deja vacias e inmutables")
        void nulosTolerados() {
            Diagnostico d = new Diagnostico(null, null, null, null, null);

            assertThat(d.codigosFalla()).isEmpty();
            assertThat(d.lecturasSensores()).isEmpty();
            assertThat(d.fecha()).isNotNull();
            assertThatThrownBy(() -> d.codigosFalla().add("X"))
                    .isInstanceOf(UnsupportedOperationException.class);
        }
    }

    @Nested
    @DisplayName("Cotizacion")
    class Cotizaciones {

        @Test
        @DisplayName("el rechazo guarda el motivo y la fecha de respuesta")
        void rechazo() {
            Cotizacion c = Cotizacion.emitir("COT-1", new BigDecimal("300000"), 48);
            c.rechazar("Muy costoso");

            assertThat(c.getEstado()).isEqualTo(EstadoCotizacion.RECHAZADA);
            assertThat(c.getMotivoRechazo()).isEqualTo("Muy costoso");
            assertThat(c.getFechaRespuesta()).isNotNull();
            assertThat(c.estaAprobada()).isFalse();
        }

        @Test
        @DisplayName("una vigencia no positiva cae al valor por defecto de 48 horas")
        void vigenciaPorDefecto() {
            assertThat(Cotizacion.emitir("COT-1", BigDecimal.TEN, 0).getVigenciaHoras()).isEqualTo(48);
            assertThat(Cotizacion.emitir("COT-2", BigDecimal.TEN, -5).getVigenciaHoras()).isEqualTo(48);
            assertThat(Cotizacion.emitir("COT-3", BigDecimal.TEN, 24).getVigenciaHoras()).isEqualTo(24);
        }

        @Test
        @DisplayName("una cotizacion ya respondida no se considera vencida")
        void respondidaNoVence() {
            Cotizacion c = Cotizacion.emitir("COT-1", BigDecimal.TEN, 1);
            c.aprobar(CanalAprobacion.PRESENCIAL);

            assertThat(c.estaVencida(c.getFecha().plusSeconds(99999))).isFalse();
        }
    }

    @Nested
    @DisplayName("MantenimientoPreventivo")
    class Mantenimientos {

        @Test
        @DisplayName("expone sus datos y acepta id al persistirse")
        void datos() {
            MantenimientoPreventivo m = MantenimientoPreventivo.programar(
                    "ABC123", TipoVehiculo.AUTOMOVIL, 48500, 5000, 6);
            m.asignarId("man-1");

            assertThat(m.getId()).isEqualTo("man-1");
            assertThat(m.getPlaca()).isEqualTo("ABC123");
            assertThat(m.isNotificado()).isFalse();
        }

        @Test
        @DisplayName("vence por fecha aunque falte kilometraje")
        void vencePorFecha() {
            MantenimientoPreventivo m = MantenimientoPreventivo.programar(
                    "ABC123", TipoVehiculo.AUTOMOVIL, 48500, 5000, 6);

            assertThat(m.estaVencido(m.getFechaSugerida(), 0)).isTrue();
            assertThat(m.estaVencido(m.getFechaSugerida().minusDays(1), 0)).isFalse();
        }
    }

    @Nested
    @DisplayName("OrdenTrabajo: casos restantes")
    class OrdenesRestantes {

        @Test
        @DisplayName("no se aprueba ni se rechaza sin cotizacion emitida")
        void sinCotizacion() {
            OrdenTrabajo sinCot = OrdenTrabajoMother.enDiagnostico();

            assertThatThrownBy(() -> sinCot.aprobarCotizacion(CanalAprobacion.WHATSAPP, "u"))
                    .isInstanceOf(ReglaNegocioException.class)
                    .hasMessageContaining("cotización");

            assertThatThrownBy(() -> sinCot.rechazarCotizacion("motivo", "u"))
                    .isInstanceOf(ReglaNegocioException.class);
        }

        @Test
        @DisplayName("no se agregan servicios a una orden ya cerrada")
        void servicioTardio() {
            OrdenTrabajo cerrada = OrdenTrabajoMother.cerrada();

            assertThatThrownBy(() -> cerrada.agregarServicio(OrdenTrabajoMother.servicioFrenos()))
                    .isInstanceOf(ReglaNegocioException.class)
                    .hasMessageContaining("Cerrada");
        }

        @Test
        @DisplayName("una cotizacion solo con repuestos tambien es valida")
        void cotizacionSoloConRepuestos() {
            OrdenTrabajo orden = OrdenTrabajoMother.enDiagnostico();
            orden.agregarServicio(OrdenTrabajoMother.servicioFrenos());
            orden.emitirCotizacion("COT-1", 48, "u");

            assertThat(orden.getCotizacion().getTotal())
                    .isEqualByComparingTo(new BigDecimal("120000.00"));
        }

        @Test
        @DisplayName("la rehidratacion desde persistencia tolera colecciones nulas")
        void rehidratacionConNulos() {
            OrdenTrabajo orden = new OrdenTrabajo("ot-1", "OT-1", EstadoOrden.RECIBIDA, null, null,
                    1000, OrdenTrabajoMother.cliente(), OrdenTrabajoMother.automovil(), null, null,
                    null, null, 3L);
            orden.rehidratar(null, null, null, null, null);

            assertThat(orden.getDiagnosticos()).isEmpty();
            assertThat(orden.getServicios()).isEmpty();
            assertThat(orden.getRepuestos()).isEmpty();
            assertThat(orden.getHistorialEstados()).isEmpty();
            assertThat(orden.getCotizacion()).isNull();
            assertThat(orden.getFechaIngreso()).isNotNull();
            assertThat(orden.getTotal()).isEqualByComparingTo(BigDecimal.ZERO);
            assertThat(orden.getVersion()).isEqualTo(3L);
        }

        @Test
        @DisplayName("expone la cita de origen y el mecanico asignado")
        void referencias() {
            OrdenTrabajo orden = OrdenTrabajoMother.enReparacion();

            assertThat(orden.getIdCita()).isEqualTo("cita-1");
            assertThat(orden.getMecanico().nombre()).isEqualTo("Luis Rojas");
            assertThat(orden.getVehiculo().tipo()).isEqualTo(TipoVehiculo.AUTOMOVIL);
            assertThat(orden.getKilometrajeIngreso()).isEqualTo(48500);
        }

        @Test
        @DisplayName("el historial expuesto no se puede modificar desde fuera")
        void historialInmutable() {
            OrdenTrabajo orden = OrdenTrabajoMother.recibida();

            assertThatThrownBy(() -> orden.getHistorialEstados().clear())
                    .isInstanceOf(UnsupportedOperationException.class);
            assertThatThrownBy(() -> orden.getDiagnosticos().clear())
                    .isInstanceOf(UnsupportedOperationException.class);
        }
    }
}
