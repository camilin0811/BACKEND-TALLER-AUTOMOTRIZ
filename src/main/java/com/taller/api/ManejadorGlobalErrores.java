package com.taller.api;

import com.taller.api.dto.ErrorRespuesta;
import com.taller.dominio.excepcion.*;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
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
 *
 * <p>Para qué sirve: Convierte las excepciones en respuestas HTTP con la forma de ErrorRespuesta (404,
 * 409, 422, 401, 503...).
 * <p>PATRÓN Manejador centralizado de errores: @RestControllerAdvice: un único punto traduce cada tipo
 *     de excepción a su código HTTP y Spring lo aplica a todos los controladores.
 */
@RestControllerAdvice
public class ManejadorGlobalErrores {

    private static final Logger log = LoggerFactory.getLogger(ManejadorGlobalErrores.class);

    // PATRÓN Manejador centralizado de errores: cada @ExceptionHandler traduce un tipo de excepción a su código HTTP.
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

    @ExceptionHandler(CredencialesInvalidasException.class)
    public ResponseEntity<ErrorRespuesta> credenciales(CredencialesInvalidasException e,
                                                       HttpServletRequest req) {
        return construir(HttpStatus.UNAUTHORIZED, e.getCodigo(), e.getMessage(), req, null);
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
        return construir(HttpStatus.BAD_REQUEST, "VALIDACION", "Hay campos inválidos en la solicitud",
                req, campos);
    }

    /** Falta un parametro obligatorio: es culpa de la peticion, no del servidor. */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorRespuesta> parametroFaltante(MissingServletRequestParameterException e,
                                                            HttpServletRequest req) {
        return construir(HttpStatus.BAD_REQUEST, "PARAMETRO_FALTANTE",
                "Falta el parámetro obligatorio '%s'".formatted(e.getParameterName()), req,
                Map.of("parametro", e.getParameterName()));
    }

    /** Cuerpo ausente o que no es JSON valido: tambien es un error de la peticion. */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorRespuesta> cuerpoIlegible(HttpMessageNotReadableException e,
                                                         HttpServletRequest req) {
        return construir(HttpStatus.BAD_REQUEST, "CUERPO_INVALIDO",
                "El cuerpo de la solicitud falta o no es un JSON válido", req, null);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorRespuesta> metodo(HttpRequestMethodNotSupportedException e,
                                                 HttpServletRequest req) {
        return construir(HttpStatus.METHOD_NOT_ALLOWED, "METODO_NO_PERMITIDO",
                "Este recurso no admite el método %s".formatted(e.getMethod()), req, null);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorRespuesta> tipoInvalido(MethodArgumentTypeMismatchException e,
                                                       HttpServletRequest req) {
        return construir(HttpStatus.BAD_REQUEST, "PARAMETRO_INVALIDO",
                "El valor de '%s' no es válido".formatted(e.getName()), req,
                Map.of("parametro", String.valueOf(e.getName())));
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
                "Ocurrió un error inesperado. Reporte el identificador de correlación.",
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
