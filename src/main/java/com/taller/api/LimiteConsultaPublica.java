package com.taller.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.taller.api.dto.ErrorRespuesta;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Limite de peticiones de la consulta publica, por IP de origen.
 *
 * <p>La consulta exige placa y documento, pero sin limite alguien podria
 * probar documentos en bucle hasta acertar. Con una ventana fija de un
 * minuto, adivinar un documento de 8 a 10 digitos deja de ser viable y un
 * cliente real, que consulta una o dos veces, nunca nota el limite.
 *
 * <p>Vive en memoria: con una sola instancia del backend es suficiente. Si
 * algun dia hay varias, el contador debe pasar a un almacen compartido.
 */
@Component
public class LimiteConsultaPublica extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(LimiteConsultaPublica.class);
    private static final String RUTA = "/api/v1/publico/";
    private static final long VENTANA_MS = 60_000;
    /** Tope de IPs recordadas: acota la memoria ante un barrido con muchas IPs. */
    private static final int MAXIMO_CLIENTES = 10_000;

    private final int limitePorMinuto;
    private final ObjectMapper json;
    private final Map<String, Ventana> ventanas = new ConcurrentHashMap<>();

    public LimiteConsultaPublica(@Value("${taller.publico.limite-por-minuto:10}") int limitePorMinuto,
                                 ObjectMapper json) {
        this.limitePorMinuto = limitePorMinuto;
        this.json = json;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest req) {
        // La verificacion previa de CORS no consulta nada: no debe gastar cupo.
        return !req.getRequestURI().startsWith(RUTA) || "OPTIONS".equalsIgnoreCase(req.getMethod());
    }

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain cadena)
            throws ServletException, IOException {
        long ahora = System.currentTimeMillis();
        if (ventanas.size() > MAXIMO_CLIENTES) {
            ventanas.values().removeIf(v -> ahora - v.inicio >= VENTANA_MS);
        }

        Ventana ventana = ventanas.compute(ipDe(req), (ip, actual) ->
                actual == null || ahora - actual.inicio >= VENTANA_MS ? new Ventana(ahora) : actual);

        if (ventana.peticiones.incrementAndGet() > limitePorMinuto) {
            rechazar(req, res, Math.max(1, (ventana.inicio + VENTANA_MS - ahora + 999) / 1000));
            return;
        }
        cadena.doFilter(req, res);
    }

    private void rechazar(HttpServletRequest req, HttpServletResponse res, long segundos) throws IOException {
        String correlacion = UUID.randomUUID().toString().substring(0, 8);
        log.warn("[{}] Limite de consulta publica superado desde {}", correlacion, ipDe(req));
        res.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
        res.setHeader("Retry-After", String.valueOf(segundos));
        res.setContentType(MediaType.APPLICATION_JSON_VALUE);
        res.setCharacterEncoding("UTF-8");
        json.writeValue(res.getOutputStream(), new ErrorRespuesta(Instant.now(),
                HttpStatus.TOO_MANY_REQUESTS.value(), HttpStatus.TOO_MANY_REQUESTS.getReasonPhrase(),
                "DEMASIADAS_SOLICITUDES",
                "Hizo demasiadas consultas seguidas. Espere %d segundos e intente de nuevo.".formatted(segundos),
                req.getRequestURI(), correlacion, Map.<String, Object>of("reintentarEnSegundos", segundos)));
    }

    /**
     * IP real del visitante. Render publica el servicio detras de Cloudflare,
     * que entrega la IP original en CF-Connecting-IP y no deja que el cliente
     * la falsifique. X-Forwarded-For queda como respaldo fuera de Cloudflare.
     */
    private static String ipDe(HttpServletRequest req) {
        String cloudflare = req.getHeader("CF-Connecting-IP");
        if (cloudflare != null && !cloudflare.isBlank()) return cloudflare.trim();
        String reenviada = req.getHeader("X-Forwarded-For");
        if (reenviada != null && !reenviada.isBlank()) return reenviada.split(",")[0].trim();
        return req.getRemoteAddr();
    }

    private static final class Ventana {
        final long inicio;
        final AtomicInteger peticiones = new AtomicInteger();

        Ventana(long inicio) {
            this.inicio = inicio;
        }
    }
}
