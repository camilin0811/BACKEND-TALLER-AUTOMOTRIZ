package com.taller.infraestructura.configuracion;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

/**
 * Permite que el frontend consuma la API desde otro origen.
 *
 * <p>Los origenes se declaran por configuracion, no en el codigo: el de
 * desarrollo no es el de produccion y no tiene sentido recompilar para
 * cambiarlo. No se usa comodin porque abrir la API a cualquier origen es
 * gratuito de escribir y caro de arreglar.
 */
@Configuration
public class ConfiguracionCors implements WebMvcConfigurer {

    private final List<String> origenesPermitidos;

    public ConfiguracionCors(
            @Value("${taller.cors.origenes:http://localhost:5173,http://localhost:4173}")
            List<String> origenesPermitidos) {
        this.origenesPermitidos = origenesPermitidos;
    }

    @Override
    public void addCorsMappings(CorsRegistry registro) {
        registro.addMapping("/api/**")
                .allowedOrigins(origenesPermitidos.toArray(String[]::new))
                .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .exposedHeaders("Location")
                .allowCredentials(true)
                .maxAge(3600);
    }
}
