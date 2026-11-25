package com.taller.dominio.modelo;

import com.taller.dominio.excepcion.ReglaNegocioException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Para qué sirve: Pruebas unitarias de personas, vehículos, catálogo y montos.
 * <p>Patrones: ninguno de diseño específico; es una pieza sencilla de su capa.
 */
@DisplayName("Personas, catalogo y agenda")
class PersonasVehiculosYCatalogoTest {

    @Nested
    @DisplayName("Cliente y empleados")
    class Personas {

        @Test
        @DisplayName("un cliente nuevo nace con fecha de registro de hoy")
        void clienteNuevo() {
            Cliente c = Cliente.nuevo("Andres Munoz", "1085123456", "+573001112233",
                    "andres@example.com", "Calle 1 # 2-3");

            assertThat(c.getNombre()).isEqualTo("Andres Munoz");
            assertThat(c.getDocumento()).isEqualTo("1085123456");
            assertThat(c.getTelefono()).isEqualTo("+573001112233");
            assertThat(c.getEmail()).isEqualTo("andres@example.com");
            assertThat(c.getDireccion()).isEqualTo("Calle 1 # 2-3");
            assertThat(c.getFechaRegistro()).isEqualTo(LocalDate.now());
            assertThat(c.getId()).isNull();
        }

        @Test
        @DisplayName("el nombre y el documento son obligatorios")
        void camposObligatorios() {
            assertThatThrownBy(() -> Cliente.nuevo("  ", "1085", null, null, null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("nombre");
            assertThatThrownBy(() -> Cliente.nuevo("Andres", null, null, null, null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("documento");
        }

        @Test
        @DisplayName("el nombre se guarda sin espacios sobrantes")
        void nombreRecortado() {
            assertThat(Cliente.nuevo("  Andres  ", "1085", null, null, null).getNombre())
                    .isEqualTo("Andres");
        }

        @Test
        @DisplayName("dos personas con el mismo documento son la misma")
        void identidadPorDocumento() {
            Cliente uno = Cliente.nuevo("Andres Munoz", "1085123456", null, null, null);
            Cliente otro = Cliente.nuevo("A. Munoz", "1085123456", null, null, null);

            assertThat(uno).isEqualTo(otro).hasSameHashCodeAs(otro);
            assertThat(uno).isNotEqualTo(Cliente.nuevo("Otro", "999", null, null, null));
            assertThat(uno).isNotEqualTo("no es una persona");
        }

        @Test
        @DisplayName("se puede actualizar el contacto y la direccion")
        void actualizaciones() {
            Cliente c = Cliente.nuevo("Andres", "1085", null, null, null);
            c.actualizarContacto("+573009998877", "nuevo@example.com");
            c.cambiarDireccion("Carrera 5 # 6-7");
            c.asignarId("cli-1");

            assertThat(c.getTelefono()).isEqualTo("+573009998877");
            assertThat(c.getEmail()).isEqualTo("nuevo@example.com");
            assertThat(c.getDireccion()).isEqualTo("Carrera 5 # 6-7");
            assertThat(c.getId()).isEqualTo("cli-1");
        }

        @Test
        @DisplayName("el mecanico se ocupa al asignarle trabajo y se libera al cerrarlo")
        void disponibilidadDelMecanico() {
            Mecanico m = Mecanico.nuevo("Luis Rojas", "7001", "MEC-01", "frenos");
            assertThat(m.isDisponible()).isTrue();
            assertThat(m.getCargo()).isEqualTo("MECANICO");

            m.ocupar();
            assertThat(m.isDisponible()).isFalse();

            m.liberar();
            assertThat(m.isDisponible()).isTrue();
        }

        @Test
        @DisplayName("el mecanico expone sus datos laborales")
        void datosDelMecanico() {
            Mecanico m = new Mecanico("mec-1", "Luis Rojas", "7001", "+57300", "luis@example.com",
                    "MEC-01", new BigDecimal("2500000"), LocalDate.of(2024, 1, 15), "frenos", true);

            assertThat(m.getCodigo()).isEqualTo("MEC-01");
            assertThat(m.getSalario()).isEqualByComparingTo(new BigDecimal("2500000"));
            assertThat(m.getFechaIngreso()).isEqualTo(LocalDate.of(2024, 1, 15));
            assertThat(m.getEspecialidad()).isEqualTo("frenos");

            m.cambiarEspecialidad("suspension");
            assertThat(m.getEspecialidad()).isEqualTo("suspension");
        }

        @Test
        @DisplayName("el recepcionista tiene turno y cargo propio")
        void recepcionista() {
            Recepcionista r = Recepcionista.nuevo("Marta Rivas", "8002", "REC-01", "MANANA");

            assertThat(r.getCargo()).isEqualTo("RECEPCIONISTA");
            assertThat(r.getTurno()).isEqualTo("MANANA");

            r.cambiarTurno("TARDE");
            assertThat(r.getTurno()).isEqualTo("TARDE");
        }

        @Test
        @DisplayName("el codigo del empleado es obligatorio")
        void codigoObligatorio() {
            assertThatThrownBy(() -> Mecanico.nuevo("Luis", "7001", " ", "frenos"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("codigo");
        }
    }

    @Nested
    @DisplayName("Vehiculo")
    class Vehiculos {

        @Test
        @DisplayName("la descripcion corta junta marca y linea")
        void descripcion() {
            Vehiculo v = new Automovil(null, "ABC123", "Mazda", "3", 2019, 1000, "VIN", 2000,
                    "cli-1", 4, "GASOLINA", 5, "AUTOMATICA");

            assertThat(v.descripcionCorta()).isEqualTo("Mazda 3");
        }

        @Test
        @DisplayName("la descripcion tolera marca o linea ausentes")
        void descripcionIncompleta() {
            Vehiculo v = new Automovil(null, "ABC123", null, null, 2019, 1000, null, null,
                    "cli-1", 4, null, 5, null);

            assertThat(v.descripcionCorta()).isEmpty();
        }

        @Test
        @DisplayName("expone los datos propios de cada subtipo")
        void datosDeSubtipos() {
            Automovil a = new Automovil("v1", "ABC123", "Mazda", "3", 2019, 1000, "VIN1", 2000,
                    "cli-1", 4, "GASOLINA", 5, "AUTOMATICA");
            Motocicleta m = new Motocicleta("v2", "XYZ12D", "Yamaha", "FZ", 2022, 500, "VIN2", 150,
                    "cli-2", "MONOCILINDRICO", 4, "NAKED", true);

            assertThat(a.getNumeroPuertas()).isEqualTo(4);
            assertThat(a.getTipoCombustible()).isEqualTo("GASOLINA");
            assertThat(a.getNumeroPasajeros()).isEqualTo(5);
            assertThat(a.getTipoTransmision()).isEqualTo("AUTOMATICA");
            assertThat(a.getVin()).isEqualTo("VIN1");
            assertThat(a.getAnio()).isEqualTo(2019);
            assertThat(a.getCilindrada()).isEqualTo(2000);
            assertThat(a.getMarca()).isEqualTo("Mazda");
            assertThat(a.getLinea()).isEqualTo("3");
            assertThat(a.getId()).isEqualTo("v1");

            assertThat(m.getTipoMotor()).isEqualTo("MONOCILINDRICO");
            assertThat(m.getNumeroTiempos()).isEqualTo(4);
            assertThat(m.getCategoria()).isEqualTo("NAKED");
            assertThat(m.isLlevaParrilla()).isTrue();
        }

        @Test
        @DisplayName("la placa es obligatoria y el kilometraje inicial no puede ser negativo")
        void validacionesDeCreacion() {
            assertThatThrownBy(() -> new Automovil(null, " ", "Mazda", "3", 2019, 0, null, null,
                    "c", 4, null, 5, null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("placa");

            assertThatThrownBy(() -> new Automovil(null, "ABC123", "Mazda", "3", 2019, -1, null, null,
                    "c", 4, null, 5, null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("negativo");
        }

        @Test
        @DisplayName("se le puede asignar id y reasignar cliente")
        void asignaciones() {
            Vehiculo v = new Automovil(null, "ABC123", "Mazda", "3", 2019, 1000, null, null,
                    "cli-1", 4, null, 5, null);
            v.asignarId("veh-9");
            v.asignarCliente("cli-2");

            assertThat(v.getId()).isEqualTo("veh-9");
            assertThat(v.getIdCliente()).isEqualTo("cli-2");
        }
    }

    @Nested
    @DisplayName("Servicio del catalogo")
    class Servicios {

        private Servicio frenos() {
            return new Servicio(null, "SRV-012", "Cambio de pastillas", new BigDecimal("120000"), 1.5, true);
        }

        @Test
        @DisplayName("se convierte en linea de orden conservando precio y horas")
        void conversionALinea() {
            LineaServicio linea = frenos().aLinea();

            assertThat(linea.codigo()).isEqualTo("SRV-012");
            assertThat(linea.descripcion()).isEqualTo("Cambio de pastillas");
            assertThat(linea.precio()).isEqualByComparingTo(new BigDecimal("120000.00"));
            assertThat(linea.horasEstimadas()).isEqualTo(1.5);
        }

        @Test
        @DisplayName("el precio se puede actualizar pero nunca a cero o negativo")
        void cambioDePrecio() {
            Servicio s = frenos();
            s.cambiarPrecio(new BigDecimal("135000"));
            assertThat(s.getPrecio()).isEqualByComparingTo(new BigDecimal("135000.00"));

            assertThatThrownBy(() -> s.cambiarPrecio(BigDecimal.ZERO))
                    .isInstanceOf(IllegalArgumentException.class);
            assertThatThrownBy(() -> s.cambiarPrecio(new BigDecimal("-1")))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("un servicio retirado del catalogo queda inactivo")
        void desactivacion() {
            Servicio s = frenos();
            s.asignarId("srv-1");
            assertThat(s.isActivo()).isTrue();
            assertThat(s.getId()).isEqualTo("srv-1");
            assertThat(s.getDescripcion()).isEqualTo("Cambio de pastillas");
            assertThat(s.getHorasEstimadas()).isEqualTo(1.5);

            s.desactivar();
            assertThat(s.isActivo()).isFalse();
        }
    }

    @Nested
    @DisplayName("Proveedor")
    class Proveedores {

        @Test
        @DisplayName("expone sus datos de contacto")
        void datos() {
            Proveedor p = new Proveedor(null, "900123456-1", "Repuestos del Sur",
                    "+576021234", "ventas@sur.example");
            p.asignarId("prov-1");

            assertThat(p.getId()).isEqualTo("prov-1");
            assertThat(p.getNit()).isEqualTo("900123456-1");
            assertThat(p.getRazonSocial()).isEqualTo("Repuestos del Sur");
            assertThat(p.getTelefono()).isEqualTo("+576021234");
            assertThat(p.getEmail()).isEqualTo("ventas@sur.example");
        }
    }

    @Nested
    @DisplayName("Cita")
    class Citas {

        private Cita pendiente() {
            return Cita.solicitar("cli-1", "ABC123", LocalDateTime.now().plusDays(3),
                    "Revision de frenos", "PORTAL_WEB");
        }

        @Test
        @DisplayName("nace pendiente con los datos de la solicitud")
        void solicitud() {
            Cita c = pendiente();

            assertThat(c.getEstado()).isEqualTo(EstadoCita.PENDIENTE);
            assertThat(c.getIdCliente()).isEqualTo("cli-1");
            assertThat(c.getPlaca()).isEqualTo("ABC123");
            assertThat(c.getMotivo()).isEqualTo("Revision de frenos");
            assertThat(c.getCanal()).isEqualTo("PORTAL_WEB");
            assertThat(c.getFechaHora()).isAfter(LocalDateTime.now());
        }

        @Test
        @DisplayName("no se agenda una cita en el pasado")
        void fechaEnElPasado() {
            assertThatThrownBy(() -> Cita.solicitar("cli-1", "ABC123",
                    LocalDateTime.now().minusHours(1), "tarde", "PORTAL_WEB"))
                    .isInstanceOf(ReglaNegocioException.class)
                    .hasMessageContaining("futuro");

            assertThatThrownBy(() -> Cita.solicitar("cli-1", "ABC123", null, "sin fecha", null))
                    .isInstanceOf(ReglaNegocioException.class);
        }

        @Test
        @DisplayName("se confirma desde pendiente")
        void confirmacion() {
            Cita c = pendiente();
            c.confirmar();

            assertThat(c.getEstado()).isEqualTo(EstadoCita.CONFIRMADA);
        }

        @Test
        @DisplayName("no se confirma dos veces")
        void doblceConfirmacion() {
            Cita c = pendiente();
            c.confirmar();

            assertThatThrownBy(c::confirmar)
                    .isInstanceOf(ReglaNegocioException.class)
                    .hasMessageContaining("CONFIRMADA");
        }

        @Test
        @DisplayName("se puede cancelar mientras no este atendida")
        void cancelacion() {
            Cita c = pendiente();
            c.cancelar();

            assertThat(c.getEstado()).isEqualTo(EstadoCita.CANCELADA);
        }

        @Test
        @DisplayName("no se cancela una cita ya atendida")
        void cancelarAtendida() {
            Cita c = pendiente();
            c.confirmar();
            c.marcarAtendida();

            assertThatThrownBy(c::cancelar)
                    .isInstanceOf(ReglaNegocioException.class)
                    .hasMessageContaining("atendida");
        }

        @Test
        @DisplayName("no se atiende una cita cancelada")
        void atenderCancelada() {
            Cita c = pendiente();
            c.cancelar();

            assertThatThrownBy(c::marcarAtendida)
                    .isInstanceOf(ReglaNegocioException.class)
                    .hasMessageContaining("cancelada");
        }

        @Test
        @DisplayName("recibe numero e id al persistirse")
        void identificadores() {
            Cita c = pendiente();
            c.asignarId("cita-1");
            c.asignarNumero("CIT-0042");

            assertThat(c.getId()).isEqualTo("cita-1");
            assertThat(c.getNumero()).isEqualTo("CIT-0042");
        }

        @ParameterizedTest
        @ValueSource(strings = {"PENDIENTE", "CONFIRMADA", "ATENDIDA", "CANCELADA"})
        @DisplayName("los estados de cita se resuelven por nombre")
        void estadosPorNombre(String nombre) {
            assertThat(EstadoCita.valueOf(nombre)).isNotNull();
        }
    }

    @Nested
    @DisplayName("Montos")
    class MontosTest {

        @Test
        @DisplayName("normaliza a dos decimales con redondeo al alza")
        void normalizacion() {
            assertThat(Montos.normalizar(new BigDecimal("100.005")))
                    .isEqualByComparingTo(new BigDecimal("100.01"));
            assertThat(Montos.normalizar(null)).isEqualByComparingTo(BigDecimal.ZERO);
            assertThat(Montos.cero()).isEqualByComparingTo(BigDecimal.ZERO);
        }

        @Test
        @DisplayName("multiplica por cantidad conservando la escala")
        void multiplicacion() {
            assertThat(Montos.multiplicar(new BigDecimal("90000"), 2))
                    .isEqualByComparingTo(new BigDecimal("180000.00"));
        }

        @Test
        @DisplayName("reconoce montos positivos")
        void positividad() {
            assertThat(Montos.esPositivo(new BigDecimal("1"))).isTrue();
            assertThat(Montos.esPositivo(BigDecimal.ZERO)).isFalse();
            assertThat(Montos.esPositivo(null)).isFalse();
        }
    }
}
