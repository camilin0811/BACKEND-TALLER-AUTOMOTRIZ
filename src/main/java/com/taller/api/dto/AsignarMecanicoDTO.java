package com.taller.api.dto;

import jakarta.validation.constraints.NotBlank;

/** Mecanico que se asigna a una orden en trabajo. */
public record AsignarMecanicoDTO(@NotBlank(message = "Elija un mecánico") String idMecanico) {
}
