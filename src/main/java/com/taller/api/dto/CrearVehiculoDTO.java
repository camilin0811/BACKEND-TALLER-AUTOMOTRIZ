package com.taller.api.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

/**
 * Para qué sirve: Cuerpo para registrar un automóvil o una motocicleta; los campos propios de cada
 * tipo son opcionales.
 * <p>PATRÓN DTO (Data Transfer Object): record inmutable que define el contrato JSON de la API (con
 *     validaciones de Bean Validation) y separa la API del modelo de dominio.
 */
public record CrearVehiculoDTO(
        @NotNull(message = "El tipo es obligatorio")
        @Pattern(regexp = "AUTOMOVIL|MOTOCICLETA", message = "El tipo debe ser AUTOMOVIL o MOTOCICLETA")
        String tipo,
        @NotBlank(message = "La placa es obligatoria")
        @jakarta.validation.constraints.Pattern(regexp = "[A-Za-z0-9]{3,8}",
                message = "La placa solo admite letras y números (entre 3 y 8)")
        String placa,
        @NotBlank(message = "El cliente es obligatorio") String idCliente,
        @NotBlank(message = "La marca es obligatoria")
        @jakarta.validation.constraints.Size(max = 40, message = "La marca no puede pasar de 40 caracteres")
        String marca,
        @NotBlank(message = "El modelo es obligatorio")
        @jakarta.validation.constraints.Size(max = 60, message = "El modelo no puede pasar de 60 caracteres")
        String linea,
        @Min(value = 1950, message = "El año debe ser 1950 o posterior")
        @jakarta.validation.constraints.Max(value = 2100, message = "El año no es válido")
        int anio,
        @Min(0) int kilometraje,
        String vin,
        Integer cilindrada,
        Integer numeroPuertas,
        String tipoCombustible,
        Integer numeroPasajeros,
        String tipoTransmision,
        String tipoMotor,
        Integer numeroTiempos,
        String categoria,
        Boolean llevaParrilla) {
}
