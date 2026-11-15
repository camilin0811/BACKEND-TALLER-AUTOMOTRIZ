package com.taller.dominio.evento;

import com.taller.dominio.excepcion.*;
import com.taller.dominio.modelo.CanalAprobacion;
import com.taller.dominio.modelo.EstadoOrden;
import com.taller.dominio.modelo.TipoVehiculo;
import com.taller.dominio.puerto.FacturacionElectronica;
import com.taller.dominio.puerto.PasarelaPagos;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Eventos de dominio, excepciones y contratos de puerto")
class EventosYExcepcionesTest {

    @Nested
    @DisplayName("Eventos")
    class Eventos {

        @Test
        @DisplayName("todo evento nace con identificador unico y marca de tiempo")
        void identidadYFecha() {
            Instant antes = Instant.now().minusSeconds(1);
            OrdenCreada uno = new OrdenCreada("ot-1", "OT-0100", "ABC123", "cli-1");
            OrdenCreada otro = new OrdenCreada("ot-2", "OT-0101", "XYZ12D", "cli-2");

            assertThat(uno.getId()).isNotBlank().isNotEqualTo(otro.getId());
            assertThat(uno.getFechaHora()).isAfter(antes);
        }

        @Test
        @DisplayName("el tipo es el nombre simple de la clase, que usa el publicador")
        void tipoPorNombreDeClase() {
            assertThat(new OrdenCreada("o", "n", "p", "c").getTipo()).isEqualTo("OrdenCreada");
            assertThat(new OrdenCerrada("o", BigDecimal.ONE, "c").getTipo()).isEqualTo("OrdenCerrada");
        }

        @Test
        @DisplayName("OrdenCreada transporta orden, placa y cliente")
        void ordenCreada() {
            OrdenCreada e = new OrdenCreada("ot-1", "OT-0100", "ABC123", "cli-1");

            assertThat(e.getIdOrden()).isEqualTo("ot-1");
            assertThat(e.getNumeroOrden()).isEqualTo("OT-0100");
            assertThat(e.getPlaca()).isEqualTo("ABC123");
            assertThat(e.getIdCliente()).isEqualTo("cli-1");
        }

        @Test
        @DisplayName("DiagnosticoRegistrado transporta los codigos de falla")
        void diagnosticoRegistrado() {
            DiagnosticoRegistrado e = new DiagnosticoRegistrado("ot-1", List.of("P0128", "C1234"));

            assertThat(e.getIdOrden()).isEqualTo("ot-1");
            assertThat(e.getCodigosFalla()).containsExactly("P0128", "C1234");
        }

        @Test
        @DisplayName("CotizacionEmitida lleva lo necesario para notificar al cliente")
        void cotizacionEmitida() {
            CotizacionEmitida e = new CotizacionEmitida("ot-1", "COT-0100",
                    new BigDecimal("300000"), "+573001112233");

            assertThat(e.getIdOrden()).isEqualTo("ot-1");
            assertThat(e.getNumeroCotizacion()).isEqualTo("COT-0100");
            assertThat(e.getTotal()).isEqualByComparingTo(new BigDecimal("300000"));
            assertThat(e.getTelefonoCliente()).isEqualTo("+573001112233");
        }

        @Test
        @DisplayName("la aprobacion y el rechazo registran canal y motivo")
        void respuestaDelCliente() {
            CotizacionAprobada aprobada = new CotizacionAprobada("ot-1", CanalAprobacion.WHATSAPP);
            CotizacionRechazada rechazada = new CotizacionRechazada("ot-2", "Muy costoso");

            assertThat(aprobada.getIdOrden()).isEqualTo("ot-1");
            assertThat(aprobada.getCanal()).isEqualTo(CanalAprobacion.WHATSAPP);
            assertThat(rechazada.getIdOrden()).isEqualTo("ot-2");
            assertThat(rechazada.getMotivo()).isEqualTo("Muy costoso");
        }

        @Test
        @DisplayName("los eventos de inventario llevan codigo, cantidad y umbral")
        void inventario() {
            RepuestoConsumido consumo = new RepuestoConsumido("ot-1", "PAST-001", 2);
            StockBajoDetectado bajo = new StockBajoDetectado("PAST-001", 5, 5);

            assertThat(consumo.getIdOrden()).isEqualTo("ot-1");
            assertThat(consumo.getCodigoRepuesto()).isEqualTo("PAST-001");
            assertThat(consumo.getCantidad()).isEqualTo(2);

            assertThat(bajo.getCodigoRepuesto()).isEqualTo("PAST-001");
            assertThat(bajo.getStockActual()).isEqualTo(5);
            assertThat(bajo.getStockMinimo()).isEqualTo(5);
        }

        @Test
        @DisplayName("el cierre y la validacion de la factura cierran el ciclo")
        void cierreYFactura() {
            OrdenCerrada cierre = new OrdenCerrada("ot-1", new BigDecimal("300000"), "cli-1");
            FacturaValidada validada = new FacturaValidada("fac-1", "ot-1", "a1b2c3");

            assertThat(cierre.getIdOrden()).isEqualTo("ot-1");
            assertThat(cierre.getTotal()).isEqualByComparingTo(new BigDecimal("300000"));
            assertThat(cierre.getIdCliente()).isEqualTo("cli-1");

            assertThat(validada.getIdFactura()).isEqualTo("fac-1");
            assertThat(validada.getIdOrden()).isEqualTo("ot-1");
            assertThat(validada.getCufe()).isEqualTo("a1b2c3");
        }

        @Test
        @DisplayName("la entrega lleva el tipo de vehiculo para calcular el mantenimiento")
        void entrega() {
            VehiculoEntregado e = new VehiculoEntregado("ot-1", "XYZ12D", 18200, TipoVehiculo.MOTOCICLETA);

            assertThat(e.getIdOrden()).isEqualTo("ot-1");
            assertThat(e.getPlaca()).isEqualTo("XYZ12D");
            assertThat(e.getKilometraje()).isEqualTo(18200);
            assertThat(e.getTipoVehiculo()).isEqualTo(TipoVehiculo.MOTOCICLETA);
        }
    }

    @Nested
    @DisplayName("Excepciones")
    class Excepciones {

        @Test
        @DisplayName("cada excepcion expone un codigo estable")
        void codigosEstables() {
            assertThat(new RecursoNoEncontradoException("cliente", "cli-9").getCodigo())
                    .isEqualTo("RECURSO_NO_ENCONTRADO");
            assertThat(new RecursoDuplicadoException("placa repetida").getCodigo())
                    .isEqualTo("RECURSO_DUPLICADO");
            assertThat(new ReglaNegocioException("mensaje").getCodigo()).isEqualTo("REGLA_NEGOCIO");
            assertThat(new ReglaNegocioException("CODIGO_PROPIO", "mensaje").getCodigo())
                    .isEqualTo("CODIGO_PROPIO");
            assertThat(new IntegracionNoDisponibleException("DIAN", "timeout").getCodigo())
                    .isEqualTo("INTEGRACION_NO_DISPONIBLE");
        }

        @Test
        @DisplayName("el mensaje de recurso no encontrado nombra el recurso y el identificador")
        void mensajeDeNoEncontrado() {
            assertThat(new RecursoNoEncontradoException("orden de trabajo", "ot-9").getMessage())
                    .contains("orden de trabajo")
                    .contains("ot-9");
        }

        @Test
        @DisplayName("el mensaje de integracion nombra el servicio y el detalle")
        void mensajeDeIntegracion() {
            assertThat(new IntegracionNoDisponibleException("escaner OBD-II", "sin respuesta").getMessage())
                    .contains("escaner OBD-II")
                    .contains("sin respuesta");
        }

        @Test
        @DisplayName("la transicion invalida enumera las salidas validas del estado actual")
        void transicionConAlternativas() {
            TransicionInvalidaException e =
                    new TransicionInvalidaException(EstadoOrden.COTIZADA, EstadoOrden.CERRADA);

            assertThat(e.getMessage())
                    .contains("COTIZADA")
                    .contains("CERRADA")
                    .contains("APROBADA")
                    .contains("RECHAZADA");
        }

        @Test
        @DisplayName("desde un estado terminal el mensaje lo dice explicitamente")
        void transicionDesdeTerminal() {
            TransicionInvalidaException e =
                    new TransicionInvalidaException(EstadoOrden.ENTREGADA, EstadoOrden.RECIBIDA);

            assertThat(e.getMessage()).contains("terminal");
        }

        @Test
        @DisplayName("todas heredan de TallerException para que la API las traduzca igual")
        void raizComun() {
            assertThat(new RecursoNoEncontradoException("x", "y")).isInstanceOf(TallerException.class);
            assertThat(new StockInsuficienteException("PAST-001", 1, 3)).isInstanceOf(TallerException.class);
        }
    }

    @Nested
    @DisplayName("Contratos de puerto")
    class Puertos {

        @Test
        @DisplayName("el resultado de la DIAN distingue validacion de rechazo")
        void resultadoDian() {
            FacturacionElectronica.Resultado ok = FacturacionElectronica.Resultado.validada("a1b2c3");
            FacturacionElectronica.Resultado no = FacturacionElectronica.Resultado.rechazada("NIT invalido");

            assertThat(ok.validada()).isTrue();
            assertThat(ok.cufe()).isEqualTo("a1b2c3");
            assertThat(ok.motivoRechazo()).isNull();

            assertThat(no.validada()).isFalse();
            assertThat(no.cufe()).isNull();
            assertThat(no.motivoRechazo()).isEqualTo("NIT invalido");
        }

        @Test
        @DisplayName("la transaccion de la pasarela transporta referencia o motivo")
        void transaccionPasarela() {
            PasarelaPagos.Transaccion aprobada = new PasarelaPagos.Transaccion(true, "APR-1", null);
            PasarelaPagos.Transaccion negada = new PasarelaPagos.Transaccion(false, null, "Fondos");

            assertThat(aprobada.aprobada()).isTrue();
            assertThat(aprobada.referencia()).isEqualTo("APR-1");
            assertThat(negada.aprobada()).isFalse();
            assertThat(negada.motivoRechazo()).isEqualTo("Fondos");
        }

        @Test
        @DisplayName("publicarTodos recorre la lista completa")
        void publicarTodos() {
            java.util.List<EventoDominio> recibidos = new java.util.ArrayList<>();
            com.taller.dominio.puerto.PublicadorEventos publicador = recibidos::add;

            publicador.publicarTodos(List.of(
                    new OrdenCreada("o1", "n1", "p1", "c1"),
                    new OrdenCerrada("o1", BigDecimal.TEN, "c1")));

            assertThat(recibidos).hasSize(2);
        }
    }
}
