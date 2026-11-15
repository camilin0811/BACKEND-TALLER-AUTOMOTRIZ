package com.taller.api.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record CrearVehiculoDTO(
        @NotNull(message = "El tipo es obligatorio")
        @Pattern(regexp = "AUTOMOVIL|MOTOCICLETA", message = "El tipo debe ser AUTOMOVIL o MOTOCICLETA")
        String tipo,
        @NotBlank(message = "La placa es obligatoria") String placa,
        @NotBlank(message = "El cliente es obligatorio") String idCliente,
        String marca,
        String linea,
        @Min(1900) int anio,
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
