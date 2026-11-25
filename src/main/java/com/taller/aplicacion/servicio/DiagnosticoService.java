package com.taller.aplicacion.servicio;

import com.taller.dominio.excepcion.IntegracionNoDisponibleException;
import com.taller.dominio.modelo.Diagnostico;
import com.taller.dominio.puerto.EscanerDiagnostico;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Para qué sirve: Arma el diagnóstico de una orden: leyendo el escáner OBD-II o con lo que escribe
 * el mecánico.
 * <p>PATRÓN Service Layer (capa de servicio, fachada de casos de uso): orquesta el caso de uso: carga
 *     del repositorio, delega las reglas en el dominio y guarda. Los controladores solo hablan con esta
 *     fachada.
 * <p>PATRÓN Inyección de dependencias / Inversión de control: recibe los puertos (interfaces del
 *     dominio) por constructor; no conoce MongoDB ni las integraciones concretas.
 */
@Service
public class DiagnosticoService {

    private static final Logger log = LoggerFactory.getLogger(DiagnosticoService.class);

    private final EscanerDiagnostico escaner;

    public DiagnosticoService(EscanerDiagnostico escaner) {
        this.escaner = escaner;
    }

    /**
     * Intenta leer el escaner. Si el equipo no responde devuelve null para que
     * el llamador ofrezca el registro manual: una falla del hardware no puede
     * detener la atencion del vehiculo.
     */
    public Diagnostico intentarEscaneo(String placa) {
        try {
            return escaner.escanear(placa);
        } catch (IntegracionNoDisponibleException e) {
            log.warn("El escaner OBD-II no respondio para la placa {}: {}", placa, e.getMessage());
            return null;
        }
    }

    public Diagnostico registroManual(List<String> codigos, String observaciones) {
        return Diagnostico.manual(codigos, observaciones);
    }

    public String interpretar(List<String> codigosFalla) {
        if (codigosFalla == null || codigosFalla.isEmpty()) {
            return "Sin códigos de falla almacenados";
        }
        StringBuilder resumen = new StringBuilder();
        for (String codigo : codigosFalla) {
            resumen.append(codigo).append(": ").append(sistemaDe(codigo)).append(". ");
        }
        return resumen.toString().trim();
    }

    /** La primera letra del codigo OBD-II indica el sistema afectado. */
    private String sistemaDe(String codigo) {
        if (codigo == null || codigo.isBlank()) {
            return "código desconocido";
        }
        return switch (Character.toUpperCase(codigo.charAt(0))) {
            case 'P' -> "motor y transmision";
            case 'C' -> "chasis";
            case 'B' -> "carroceria";
            case 'U' -> "red de comunicaciones";
            default -> "sistema no identificado";
        };
    }
}
