package com.taller.infraestructura.integracion;

import com.taller.dominio.modelo.Factura;
import com.taller.dominio.puerto.FacturacionElectronica;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.Map;

/**
 * Integracion con el proveedor tecnologico autorizado por la DIAN (ADR-007).
 *
 * <p>Solo se activa con {@code taller.facturacion.simulada=false}. Mientras
 * no haya proveedor contratado, el bean activo es {@link FacturacionSimulada}.
 *
 * <p>Reintenta con espera creciente. Si agota los intentos devuelve un rechazo
 * tecnico y la factura queda PENDIENTE_ENVIO para reintentar despues: una
 * caida del proveedor no puede perder la factura.
 *
 * <p>Para qué sirve: Envía las facturas al proveedor tecnológico autorizado por la DIAN (se activa con
 * taller.facturacion.simulada=false).
 * <p>PATRÓN Adapter: adapta la API HTTP del proveedor al puerto FacturacionElectronica.
 * <p>PATRÓN Strategy: es una de las dos estrategias de facturación; la otra es FacturacionSimulada.
 * <p>PATRÓN Retry (reintento con espera creciente): si el proveedor falla, reintenta con espera
 *     creciente; al agotar los intentos la factura queda pendiente de envío.
 */
@Component
// PATRÓN Strategy: esta implementación se elige por configuración (taller.facturacion.simulada=false).
@ConditionalOnProperty(name = "taller.facturacion.simulada", havingValue = "false")
public class DianFacturacionClient implements FacturacionElectronica {

    private static final Logger log = LoggerFactory.getLogger(DianFacturacionClient.class);

    private final RestClient cliente;
    private final String apiKey;
    private final int reintentos;

    public DianFacturacionClient(RestClient.Builder builder,
                                 @Value("${taller.facturacion.proveedor-url}") String url,
                                 @Value("${taller.facturacion.api-key:}") String apiKey,
                                 @Value("${taller.facturacion.reintentos:3}") int reintentos) {
        this.cliente = builder.baseUrl(url).build();
        this.apiKey = apiKey;
        this.reintentos = reintentos;
    }

    @Override
    public Resultado enviar(Factura factura) {
        RuntimeException ultimoError = null;
        // PATRÓN Retry: se reintenta el envío con espera creciente antes de rendirse.
        for (int intento = 1; intento <= reintentos; intento++) {
            try {
                Map<?, ?> respuesta = cliente.post()
                        .uri("/facturas")
                        .header("X-Api-Key", apiKey)
                        .body(cuerpoDe(factura))
                        .retrieve()
                        .body(Map.class);
                return interpretar(respuesta);
            } catch (RuntimeException e) {
                ultimoError = e;
                log.warn("Intento {}/{} fallido enviando la factura {}: {}",
                        intento, reintentos, factura.getNumero(), e.getMessage());
                esperar(intento);
            }
        }
        return Resultado.rechazada("El proveedor no respondio tras %d intentos: %s"
                .formatted(reintentos, ultimoError == null ? "sin detalle" : ultimoError.getMessage()));
    }

    private Map<String, Object> cuerpoDe(Factura f) {
        return Map.of(
                "numero", String.valueOf(f.getNumero()),
                "fecha", f.getFecha().toString(),
                "adquiriente", Map.of(
                        "nombre", f.getCliente() == null ? "" : String.valueOf(f.getCliente().nombre())),
                "subtotal", f.getSubtotal(),
                "iva", f.getIva(),
                "total", f.getTotal());
    }

    private Resultado interpretar(Map<?, ?> respuesta) {
        if (respuesta == null) {
            return Resultado.rechazada("Respuesta vacia del proveedor");
        }
        Object cufe = respuesta.get("cufe");
        if (cufe != null && !String.valueOf(cufe).isBlank()) {
            return Resultado.validada(String.valueOf(cufe));
        }
        Object motivo = respuesta.get("motivo");
        return Resultado.rechazada(motivo == null ? "La DIAN no validó la factura" : String.valueOf(motivo));
    }

    private void esperar(int intento) {
        try {
            Thread.sleep(500L * intento);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
