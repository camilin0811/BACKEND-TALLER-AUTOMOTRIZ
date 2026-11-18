package com.taller.api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/** Registro de un cliente. Mismas reglas que el formulario de nueva orden. */
public record CrearClienteDTO(
        @NotBlank(message = "El nombre es obligatorio")
        @Size(min = 3, max = 120, message = "El nombre debe tener entre 3 y 120 caracteres")
        String nombre,
        @NotBlank(message = "El documento es obligatorio")
        @Pattern(regexp = "\\d{5,15}", message = "El documento solo admite dígitos (entre 5 y 15)")
        String documento,
        @NotBlank(message = "El teléfono es obligatorio")
        @Pattern(regexp = "\\+?\\d{7,15}", message = "El teléfono debe tener entre 7 y 15 dígitos")
        String telefono,
        @Email(message = "El correo no tiene formato válido")
        @Size(max = 120, message = "El correo no puede pasar de 120 caracteres")
        String email,
        String direccion) {
}
