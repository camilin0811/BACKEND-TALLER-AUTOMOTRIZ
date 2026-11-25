package com.taller.infraestructura.integracion;

import com.taller.dominio.excepcion.IntegracionNoDisponibleException;
import com.taller.dominio.modelo.Diagnostico;
import com.taller.dominio.puerto.EscanerDiagnostico;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;

/**
 * Lectura del escaner OBD-II a traves del gateway IoT del taller (ADR-008).
 *
 * <p>El escaner habla Bluetooth y no tiene pila de red: el gateway hace de
 * puente. Cambiar de modelo de escaner no toca el backend.
 *
 * <p>Para qué sirve: Lee el escáner OBD-II a través del gateway IoT del taller.
 * <p>PATRÓN Adapter: adapta la API del gateway IoT al puerto EscanerDiagnostico.
 */
@Component
public class ObdScannerAdapter implements EscanerDiagnostico {

    private final RestClient cliente;

    public ObdScannerAdapter(RestClient.Builder builder,
                             @Value("${taller.obd.gateway-url:http://localhost:9093/obd}") String url) {
        this.cliente = builder.baseUrl(url).build();
    }

    @Override
    @SuppressWarnings("unchecked")
    public Diagnostico escanear(String placa) {
        try {
            Map<String, Object> respuesta = (Map<String, Object>) cliente.get()
                    .uri("/escaneos/{placa}", placa)
                    .retrieve()
                    .body(Map.class);
            if (respuesta == null) {
                throw new IntegracionNoDisponibleException("escáner OBD-II", "respuesta vacía");
            }
            List<String> codigos = (List<String>) respuesta.getOrDefault("codigosFalla", List.of());
            Map<String, Object> lecturas = (Map<String, Object>) respuesta.getOrDefault("lecturas", Map.of());
            return Diagnostico.desdeEscaner(codigos, lecturas,
                    String.valueOf(respuesta.getOrDefault("observaciones", "")));
        } catch (IntegracionNoDisponibleException e) {
            throw e;
        } catch (RuntimeException e) {
            throw new IntegracionNoDisponibleException("escáner OBD-II", e.getMessage());
        }
    }
}
