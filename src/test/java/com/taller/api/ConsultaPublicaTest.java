package com.taller.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.taller.api.dto.ConsultaPublicaDTO;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Para qué sirve: Pruebas unitarias de la consulta pública del portal.
 * <p>Patrones: ninguno de diseño específico; es una pieza sencilla de su capa.
 */
@DisplayName("Consulta pública del cliente")
class ConsultaPublicaTest {

    @Nested
    @DisplayName("Validación de los datos")
    class Validacion {

        private final Validator validador = Validation.buildDefaultValidatorFactory().getValidator();

        private int errores(String placa, String documento) {
            return validador.validate(new ConsultaPublicaDTO(placa, documento)).size();
        }

        @Test
        @DisplayName("acepta una placa alfanumérica y un documento numérico")
        void datosValidos() {
            assertThat(errores("ABC123", "10000001")).isZero();
            assertThat(errores("xyz12d", "1234567890")).isZero();
        }

        @Test
        @DisplayName("rechaza placa y documento vacíos o ausentes")
        void vacios() {
            assertThat(errores("", "")).isPositive();
            assertThat(errores(null, null)).isPositive();
            assertThat(errores("   ", "10000001")).isPositive();
        }

        @Test
        @DisplayName("rechaza caracteres especiales en la placa")
        void inyeccion() {
            assertThat(errores("' OR 1=1--", "10000001")).isPositive();
            assertThat(errores("ABC-123", "10000001")).isPositive();
            assertThat(errores("ABCDEFGHIJ", "10000001")).isPositive();
        }

        @Test
        @DisplayName("rechaza documentos con letras o fuera de longitud")
        void documento() {
            assertThat(errores("ABC123", "10a00001")).isPositive();
            assertThat(errores("ABC123", "123")).isPositive();
        }
    }

    @Nested
    @DisplayName("Límite de peticiones por IP")
    class Limite {

        private final LimiteConsultaPublica filtro =
                new LimiteConsultaPublica(3, new ObjectMapper().registerModule(new JavaTimeModule()));

        private MockHttpServletResponse consultar(String ip, String metodo, String ruta) throws Exception {
            MockHttpServletRequest req = new MockHttpServletRequest(metodo, ruta);
            req.addHeader("CF-Connecting-IP", ip);
            MockHttpServletResponse res = new MockHttpServletResponse();
            filtro.doFilter(req, res, new MockFilterChain());
            return res;
        }

        @Test
        @DisplayName("deja pasar hasta el límite y responde 429 al superarlo")
        void superaElLimite() throws Exception {
            for (int i = 0; i < 3; i++) {
                assertThat(consultar("1.1.1.1", "POST", "/api/v1/publico/orden").getStatus()).isEqualTo(200);
            }
            MockHttpServletResponse rechazada = consultar("1.1.1.1", "POST", "/api/v1/publico/orden");

            assertThat(rechazada.getStatus()).isEqualTo(429);
            assertThat(rechazada.getHeader("Retry-After")).isNotBlank();
            assertThat(rechazada.getContentAsString()).contains("DEMASIADAS_SOLICITUDES");
        }

        @Test
        @DisplayName("cada IP tiene su propio cupo")
        void cupoPorIp() throws Exception {
            for (int i = 0; i < 4; i++) consultar("2.2.2.2", "POST", "/api/v1/publico/orden");

            assertThat(consultar("3.3.3.3", "POST", "/api/v1/publico/orden").getStatus()).isEqualTo(200);
        }

        @Test
        @DisplayName("no limita las rutas internas ni la verificación previa de CORS")
        void fueraDeAlcance() throws Exception {
            for (int i = 0; i < 10; i++) {
                assertThat(consultar("4.4.4.4", "GET", "/api/v1/ordenes").getStatus()).isEqualTo(200);
                assertThat(consultar("4.4.4.4", "OPTIONS", "/api/v1/publico/orden").getStatus()).isEqualTo(200);
            }
        }
    }
}
