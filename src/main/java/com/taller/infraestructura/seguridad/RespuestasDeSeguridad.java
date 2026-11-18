package com.taller.infraestructura.seguridad;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 401 y 403 con la misma forma que el resto de errores de la API.
 *
 * <p>Sin esto, Spring Security devuelve una pagina HTML o un cuerpo vacio y el
 * frontend no puede distinguir "no inicio sesión" de "no tiene permiso".
 */
@Component
public class RespuestasDeSeguridad implements AuthenticationEntryPoint, AccessDeniedHandler {

    private final ObjectMapper json = new ObjectMapper();

    @Override
    public void commence(HttpServletRequest peticion, HttpServletResponse respuesta,
                         AuthenticationException excepcion) throws IOException {
        escribir(peticion, respuesta, 401, "NO_AUTENTICADO",
                "Debe iniciar sesión para usar este recurso");
    }

    @Override
    public void handle(HttpServletRequest peticion, HttpServletResponse respuesta,
                       AccessDeniedException excepcion) throws IOException {
        escribir(peticion, respuesta, 403, "SIN_PERMISO",
                "Su rol no tiene permiso para esta operación");
    }

    private void escribir(HttpServletRequest peticion, HttpServletResponse respuesta,
                          int estado, String codigo, String mensaje) throws IOException {
        Map<String, Object> cuerpo = new LinkedHashMap<>();
        cuerpo.put("timestamp", Instant.now().toString());
        cuerpo.put("status", estado);
        cuerpo.put("error", estado == 401 ? "Unauthorized" : "Forbidden");
        cuerpo.put("codigo", codigo);
        cuerpo.put("mensaje", mensaje);
        cuerpo.put("ruta", peticion.getRequestURI());

        respuesta.setStatus(estado);
        respuesta.setContentType("application/json;charset=UTF-8");
        respuesta.getWriter().write(json.writeValueAsString(cuerpo));
    }
}
