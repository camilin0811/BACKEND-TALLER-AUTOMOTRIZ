package com.taller.api.dto;

import java.util.List;

public record RegistrarDiagnosticoDTO(boolean usarEscaner,
                                      List<String> codigosFalla,
                                      String observaciones) {
}
