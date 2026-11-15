package com.taller.api;

import com.taller.api.dto.ErrorRespuesta;
import com.taller.dominio.excepcion.*;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Traduce las excepciones del dominio a respuestas HTTP.
 *
 * <p>Es el unico lugar donde el dominio se convierte en codigos de estado. Un
 * servicio que devuelve ResponseEntity ya esta acoplado al transporte.
 */
@RestControllerAdvice
public class ManejadorGlobalErrores {

    private static final Logger log = LoggerFactory.getLogger(ManejadorGlobalErrores.class);

    @ExceptionHandler(RecursoNoEncontradoException.class)
    public ResponseEntity<ErrorRespuesta> noEncontrado(RecursoNoEncontradoException e, HttpServletRequest req) {
        return construir(HttpStatus.NOT_FOUND, e.getCodigo(), e.getMessage(), req, null);
    }

    @ExceptionHandler(RecursoDuplicadoException.class)
    public ResponseEntity<ErrorRespuesta> duplicado(RecursoDuplicadoException e, HttpServletRequest req) {
        return construir(HttpStatus.CONFLICT, e.getCodigo(), e.getMessage(), req, null);
    }

    @ExceptionHandler(TransicionInvalidaException.class)
    public ResponseEntity<ErrorRespuesta> transicion(TransicionInvalidaException e, HttpServletRequest req) {
        return construir(HttpStatus.CONFLICT, e.getCodigo(), e.getMessage(), req, null);
    }

    @ExceptionHandler(StockInsuficienteException.class)
    public ResponseEntity<ErrorRespuesta> stock(StockInsuficienteException e, HttpServletRequest req) {
        Map<String, Object> detalle = new LinkedHashMap<>();
        detalle.put("codigoRepuesto", e.getCodigoRepuesto());
        detalle.put("disponible", e.getDisponible());
        detalle.put("solicitado", e.getSolicitado());
        return construir(HttpStatus.CONFLICT, e.getCodigo(), e.getMessage(), req, detalle);
    }

    @ExceptionHandler(ReglaNegocioException.class)
    public ResponseEntity<ErrorRespuesta> regla(ReglaNegocioException e, HttpServletRequest req) {
        return construir(HttpStatus.UNPROCESSABLE_ENTITY, e.getCodigo(), e.getMessage(), req, null);
    }

    @ExceptionHandler(IntegracionNoDisponibleException.class)
    public ResponseEntity<ErrorRespuesta> integracion(IntegracionNoDisponibleException e, HttpServletRequest req) {
        return construir(HttpStatus.SERVICE_UNAVAILABLE, e.getCodigo(), e.getMessage(), req, null);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorRespuesta> validacion(MethodArgumentNotValidException e, HttpServletRequest req) {
        Map<String, Object> campos = new LinkedHashMap<>();
        e.getBindingResult().getFieldErrors()
                .forEach(error -> campos.put(error.getField(), error.getDefaultMessage()));
        return construir(HttpStatus.BAD_REQUEST, "VALIDACION", "Hay campos invalidos en la solicitud",
                req, campos);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorRespuesta> argumento(IllegalArgumentException e, HttpServletRequest req) {
        return construir(HttpStatus.BAD_REQUEST, "ARGUMENTO_INVALIDO", e.getMessage(), req, null);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorRespuesta> inesperado(Exception e, HttpServletRequest req) {
        String correlacion = nuevaCorrelacion();
        log.error("Error inesperado [{}] en {}", correlacion, req.getRequestURI(), e);
        ErrorRespuesta cuerpo = new ErrorRespuesta(Instant.now(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(), "Internal Server Error", "ERROR_INTERNO",
                "Ocurrio un error inesperado. Reporte el identificador de correlacion.",
                req.getRequestURI(), correlacion, null);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(cuerpo);
    }

    private ResponseEntity<ErrorRespuesta> construir(HttpStatus estado, String codigo, String mensaje,
                                                     HttpServletRequest req, Map<String, Object> detalle) {
        String correlacion = nuevaCorrelacion();
        log.warn("[{}] {} {} -> {} {}", correlacion, req.getMethod(), req.getRequestURI(),
                estado.value(), codigo);
        return ResponseEntity.status(estado).body(new ErrorRespuesta(Instant.now(), estado.value(),
                estado.getReasonPhrase(), codigo, mensaje, req.getRequestURI(), correlacion, detalle));
    }

    private String nuevaCorrelacion() {
        return UUID.randomUUID().toString().substring(0, 8);
    }
}
