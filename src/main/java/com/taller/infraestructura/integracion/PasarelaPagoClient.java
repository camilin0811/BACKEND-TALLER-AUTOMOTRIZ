package com.taller.infraestructura.integracion;

import com.taller.dominio.puerto.PasarelaPagos;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;
import java.util.Map;

@Component
public class PasarelaPagoClient implements PasarelaPagos {

    private static final Logger log = LoggerFactory.getLogger(PasarelaPagoClient.class);

    private final RestClient cliente;
    private final String apiKey;

    public PasarelaPagoClient(RestClient.Builder builder,
                              @Value("${taller.pagos.url:http://localhost:9092/pagos}") String url,
                              @Value("${taller.pagos.api-key:}") String apiKey) {
        this.cliente = builder.baseUrl(url).build();
        this.apiKey = apiKey;
    }

    @Override
    public Transaccion cobrar(BigDecimal monto, String descripcion) {
        try {
            Map<?, ?> respuesta = cliente.post()
                    .uri("/cobros")
                    .header("X-Api-Key", apiKey)
                    .body(Map.of("monto", monto, "descripcion", descripcion))
                    .retrieve()
                    .body(Map.class);
            if (respuesta != null && Boolean.TRUE.equals(respuesta.get("aprobada"))) {
                return new Transaccion(true, String.valueOf(respuesta.get("referencia")), null);
            }
            return new Transaccion(false, null,
                    respuesta == null ? "Respuesta vacia" : String.valueOf(respuesta.get("motivo")));
        } catch (RuntimeException e) {
            log.warn("La pasarela de pagos no respondio: {}", e.getMessage());
            return new Transaccion(false, null, "La pasarela no esta disponible");
        }
    }
}
