package com.taller.api;

import com.taller.api.dto.CrearRepuestoDTO;
import com.taller.api.dto.ErrorRespuesta;
import com.taller.aplicacion.servicio.InventarioService;
import com.taller.dominio.excepcion.RecursoDuplicadoException;
import com.taller.dominio.modelo.Repuesto;
import com.taller.dominio.puerto.GeneradorConsecutivos;
import com.taller.dominio.puerto.MovimientoInventarioRepositorio;
import com.taller.dominio.puerto.OrdenCompraRepositorio;
import com.taller.dominio.puerto.PublicadorEventos;
import com.taller.dominio.puerto.RepuestoRepositorio;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Para qué sirve: Pruebas unitarias de la creación de repuestos y de la respuesta a rutas inexistentes.
 * <p>Patrones: ninguno de diseño específico; es una pieza sencilla de su capa.
 */
@DisplayName("Creación de repuestos y rutas inexistentes")
class RepuestosYRutasTest {

    private final Validator validador = Validation.buildDefaultValidatorFactory().getValidator();

    private Set<String> errores(CrearRepuestoDTO dto) {
        return validador.validate(dto).stream().map(ConstraintViolation::getMessage).collect(Collectors.toSet());
    }

    @Test
    @DisplayName("una ruta que no existe responde 404, no 500")
    void rutaInexistenteDa404() throws Exception {
        MockHttpServletRequest req = new MockHttpServletRequest("GET", "/api/v1/no-existe");
        ResponseEntity<ErrorRespuesta> r = new ManejadorGlobalErrores()
                .rutaInexistente(new NoResourceFoundException(HttpMethod.GET, "api/v1/no-existe"), req);
        assertThat(r.getStatusCode().value()).isEqualTo(404);
        assertThat(r.getBody().codigo()).isEqualTo("RUTA_NO_ENCONTRADA");
        assertThat(r.getBody().mensaje()).contains("/api/v1/no-existe");
    }

    @Test
    @DisplayName("un código de repuesto repetido se rechaza con un mensaje claro y no se guarda")
    void codigoRepetido() {
        RepuestoRepositorio repuestos = mock(RepuestoRepositorio.class);
        Repuesto existente = Repuesto.nuevo("PAST-001", "Pastillas", new BigDecimal("90000"), 10, 5);
        when(repuestos.buscarPorCodigo("PAST-001")).thenReturn(Optional.of(existente));
        InventarioService servicio = new InventarioService(repuestos, mock(MovimientoInventarioRepositorio.class),
                mock(OrdenCompraRepositorio.class), mock(PublicadorEventos.class), mock(GeneradorConsecutivos.class));

        assertThatThrownBy(() -> servicio.crear(
                Repuesto.nuevo("PAST-001", "Otras pastillas", new BigDecimal("1000"), 1, 1)))
                .isInstanceOf(RecursoDuplicadoException.class)
                .hasMessageContaining("PAST-001");
        verify(repuestos, never()).guardar(any());
    }

    @Test
    @DisplayName("un código nuevo se guarda")
    void codigoNuevo() {
        RepuestoRepositorio repuestos = mock(RepuestoRepositorio.class);
        when(repuestos.buscarPorCodigo("AMOR-003")).thenReturn(Optional.empty());
        InventarioService servicio = new InventarioService(repuestos, mock(MovimientoInventarioRepositorio.class),
                mock(OrdenCompraRepositorio.class), mock(PublicadorEventos.class), mock(GeneradorConsecutivos.class));
        Repuesto nuevo = Repuesto.nuevo("AMOR-003", "Amortiguador", new BigDecimal("185000"), 6, 2);
        servicio.crear(nuevo);
        verify(repuestos).guardar(nuevo);
    }

    @Test
    @DisplayName("los datos inválidos del formulario devuelven mensajes en español")
    void validaciones() {
        assertThat(errores(new CrearRepuestoDTO("AMOR-003", "Amortiguador", new BigDecimal("185000"), 6, 2))).isEmpty();
        assertThat(errores(new CrearRepuestoDTO("a b", "Amortiguador", new BigDecimal("1"), 0, 0)))
                .containsExactly("El código solo admite letras, números y guiones (entre 3 y 20)");
        assertThat(errores(new CrearRepuestoDTO("AMOR-003", "x", BigDecimal.ZERO, -1, 0)))
                .contains("El nombre debe tener entre 3 y 120 caracteres", "El precio debe ser mayor que cero",
                        "El stock inicial no puede ser negativo");
    }
}
