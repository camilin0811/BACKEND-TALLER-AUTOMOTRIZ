package com.taller.infraestructura.integracion;

import com.taller.dominio.puerto.NotificadorCliente;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.HexFormat;
import java.util.Map;

/**
 * Mensajeria hacia el cliente y verificacion de los webhooks entrantes.
 *
 * <p>Es el adaptador que sostiene la decision de negocio mas importante del
 * sistema: el cliente aprueba la cotizacion respondiendo un mensaje (ADR-006).
 */
@Component
public class WhatsAppGateway implements NotificadorCliente {

    private static final Logger log = LoggerFactory.getLogger(WhatsAppGateway.class);

    private final RestClient cliente;
    private final String token;
    private final String secretoWebhook;

    public WhatsAppGateway(RestClient.Builder builder,
                           @Value("${taller.mensajeria.whatsapp-url}") String url,
                           @Value("${taller.mensajeria.token:}") String token,
                           @Value("${taller.mensajeria.secreto-webhook:}") String secretoWebhook) {
        this.cliente = builder.baseUrl(url).build();
        this.token = token;
        this.secretoWebhook = secretoWebhook;
    }

    @Override
    public void enviarCotizacion(String telefono, String numeroCotizacion, BigDecimal total) {
        String texto = """
                Cotizacion %s
                Total: $%s
                Responda APRUEBO para iniciar el trabajo o NO ACEPTO para cancelar.
                Vigencia 48 horas."""
                .formatted(numeroCotizacion, total.toPlainString());
        enviarMensaje(telefono, texto);
    }

    @Override
    public void enviarMensaje(String telefono, String texto) {
        try {
            cliente.post()
                    .uri("/messages")
                    .header("Authorization", "Bearer " + token)
                    .body(Map.of("to", telefono, "type", "text", "text", Map.of("body", texto)))
                    .retrieve()
                    .toBodilessEntity();
            log.info("Mensaje enviado a {}", enmascarar(telefono));
        } catch (RuntimeException e) {
            // Una notificacion caida no puede tumbar la operacion del taller.
            log.warn("No se pudo enviar el mensaje a {}: {}", enmascarar(telefono), e.getMessage());
        }
    }

    @Override
    public void enviarCorreo(String email, String asunto, String cuerpo) {
        log.info("Correo pendiente de proveedor: {} / {}", enmascarar(email), asunto);
    }

    /**
     * Verifica la firma HMAC del webhook. Sin esto, cualquiera podria aprobar
     * cotizaciones ajenas con una peticion HTTP.
     */
    public boolean firmaValida(String cuerpoCrudo, String firmaRecibida) {
        if (secretoWebhook == null || secretoWebhook.isBlank()) {
            log.warn("No hay secreto de webhook configurado: se rechaza la peticion");
            return false;
        }
        if (firmaRecibida == null || firmaRecibida.isBlank()) {
            return false;
        }
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(secretoWebhook.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            String calculada = HexFormat.of().formatHex(mac.doFinal(cuerpoCrudo.getBytes(StandardCharsets.UTF_8)));
            return java.security.MessageDigest.isEqual(
                    calculada.getBytes(StandardCharsets.UTF_8),
                    firmaRecibida.replace("sha256=", "").getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            log.error("Error verificando la firma del webhook", e);
            return false;
        }
    }

    /** Nunca se registra un telefono o correo completo. */
    private String enmascarar(String contacto) {
        if (contacto == null || contacto.length() < 4) {
            return "***";
        }
        return "***" + contacto.substring(contacto.length() - 4);
    }
}
