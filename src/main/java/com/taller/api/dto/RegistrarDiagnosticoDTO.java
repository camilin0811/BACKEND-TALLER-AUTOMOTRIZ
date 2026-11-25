package com.taller.api.dto;

import java.util.List;

/**
 * Para qué sirve: Cuerpo para registrar un diagnóstico, manual o leído del escáner OBD-II.
 * <p>PATRÓN DTO (Data Transfer Object): record inmutable que define el contrato JSON de la API (con
 *     validaciones de Bean Validation) y separa la API del modelo de dominio.
 */
public record RegistrarDiagnosticoDTO(boolean usarEscaner,
                                      List<String> codigosFalla,
                                      String observaciones) {
}
