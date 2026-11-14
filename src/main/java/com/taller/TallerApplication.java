package com.taller;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

/**
 * Punto de entrada del Sistema de Gestion del Taller Automotriz Automatizado.
 *
 * <p>Un solo artefacto desplegable, segun ADR-005 de la wiki: para el volumen
 * del taller (unas 40 ordenes diarias) separar en servicios anadiria operacion
 * sin resolver ningun problema de escala.
 */
@SpringBootApplication
@ConfigurationPropertiesScan
public class TallerApplication {

    public static void main(String[] args) {
        SpringApplication.run(TallerApplication.class, args);
    }
}
