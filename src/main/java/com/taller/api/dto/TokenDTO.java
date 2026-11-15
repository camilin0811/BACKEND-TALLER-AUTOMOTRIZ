package com.taller.api.dto;

public record TokenDTO(String token, String tipo, long expiraEnSegundos,
                       String usuario, String rol, String nombre) {
}
