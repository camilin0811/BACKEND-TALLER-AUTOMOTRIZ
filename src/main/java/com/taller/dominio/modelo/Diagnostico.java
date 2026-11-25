package com.taller.dominio.modelo;

import java.time.Instant;
import java.util.List;
import java.util.Map;

/**
 * Para qué sirve: Resultado de revisar el vehículo: códigos de falla, lecturas y observaciones, del
 * escáner o manual.
 * <p>PATRÓN Objeto de valor (DDD): record inmutable que se identifica por sus valores, no por un id.
 * <p>PATRÓN Método fábrica estático: desdeEscaner() y manual() crea el objeto en un estado inicial
 *     válido y le da nombre a esa forma de crearlo.
 */
public record Diagnostico(Instant fecha,
                          OrigenDiagnostico origen,
                          List<String> codigosFalla,
                          Map<String, Object> lecturasSensores,
                          String observaciones) {

    public Diagnostico {
        codigosFalla = codigosFalla == null ? List.of() : List.copyOf(codigosFalla);
        lecturasSensores = lecturasSensores == null ? Map.of() : Map.copyOf(lecturasSensores);
        fecha = fecha == null ? Instant.now() : fecha;
    }

    public static Diagnostico desdeEscaner(List<String> codigos, Map<String, Object> lecturas, String observaciones) {
        return new Diagnostico(Instant.now(), OrigenDiagnostico.OBD_II, codigos, lecturas, observaciones);
    }

    public static Diagnostico manual(List<String> codigos, String observaciones) {
        return new Diagnostico(Instant.now(), OrigenDiagnostico.MANUAL, codigos, Map.of(), observaciones);
    }
}
