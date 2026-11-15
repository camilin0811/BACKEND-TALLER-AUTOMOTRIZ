package com.taller.api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CrearClienteDTO(
        @NotBlank(message = "El nombre es obligatorio") String nombre,
        @NotBlank(message = "El documento es obligatorio") @Size(max = 20) String documento,
        String telefono,
        @Email(message = "El correo no tiene formato valido") String email,
        String direccion) {
}
