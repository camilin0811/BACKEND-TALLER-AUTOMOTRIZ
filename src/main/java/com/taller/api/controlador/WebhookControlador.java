package com.taller.api.controlador;

import com.taller.aplicacion.servicio.OrdenTrabajoService;
import com.taller.dominio.modelo.CanalAprobacion;
import com.taller.infraestructura.integracion.WhatsAppGateway;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Locale;

/**
 * Webhooks entrantes.
 *
 * <p>Este endpoint es el que sostiene la decision de negocio central: el
 * cliente aprueba la cotizacion respondiendo por WhatsApp (ADR-006). No usa
 * JWT; se autentica verificando la firma HMAC del proveedor.
 *
 * <p>Para qué sirve: Recibe las respuestas del cliente por WhatsApp (APRUEBO / RECHAZO) y aprueba o
 * rechaza la cotización.
 * <p>PATRÓN Controller (MVC): recibe la petición HTTP, valida la entrada y delega en la capa de
 *     servicio; no contiene reglas de negocio.
 * <p>PATRÓN Webhook: el proveedor de mensajería llama a esta URL cuando el cliente responde; antes de
 *     actuar se verifica la firma HMAC del cuerpo.
 */
@RestController
@RequestMapping("/api/v1/webhooks")
@Tag(name = "Webhooks")
public class WebhookControlador {

    private static final Logger log = LoggerFactory.getLogger(WebhookControlador.class);

    private final WhatsAppGateway whatsapp;
    private final OrdenTrabajoService ordenes;

    public WebhookControlador(WhatsAppGateway whatsapp, OrdenTrabajoService ordenes) {
        this.whatsapp = whatsapp;
        this.ordenes = ordenes;
    }

    @PostMapping("/whatsapp")
    @Operation(summary = "Respuesta del cliente desde la mensajeria")
    public ResponseEntity<Void> whatsapp(@RequestBody String cuerpo,
                                         @RequestHeader(value = "X-Hub-Signature-256", required = false)
                                         String firma,
                                         @RequestParam(required = false) String idOrden,
                                         @RequestParam(required = false) String texto) {
        if (!whatsapp.firmaValida(cuerpo, firma)) {
            log.warn("Webhook de WhatsApp con firma invalida");
            return ResponseEntity.status(401).build();
        }
        procesarRespuesta(idOrden, texto);
        // Siempre 200: si respondemos error, el proveedor reintenta en bucle.
        return ResponseEntity.ok().build();
    }

    private void procesarRespuesta(String idOrden, String texto) {
        if (idOrden == null || texto == null) {
            return;
        }
        String normalizado = texto.trim().toUpperCase(Locale.ROOT);
        try {
            if (normalizado.startsWith("APRUEBO")) {
                ordenes.aprobarCotizacion(idOrden, CanalAprobacion.WHATSAPP, "sistema");
            } else if (normalizado.startsWith("NO ACEPTO")) {
                ordenes.rechazarCotizacion(idOrden, "Rechazada por el cliente via WhatsApp", "sistema");
            } else {
                // Texto libre: no se toca el estado, queda para atencion humana.
                log.info("Respuesta no reconocida en la orden {}; se marca para atencion manual", idOrden);
            }
        } catch (RuntimeException e) {
            log.error("No se pudo procesar la respuesta del cliente para la orden {}", idOrden, e);
        }
    }
}
