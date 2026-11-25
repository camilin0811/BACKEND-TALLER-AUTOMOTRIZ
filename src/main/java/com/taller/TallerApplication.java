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
 *
 * <p>Para qué sirve: Arranca la aplicación Spring Boot: un solo artefacto desplegable con toda la API.
 * <p>PATRÓN Inyección de dependencias / Inversión de control: Spring crea y conecta todos los
 *     componentes (@Service, @Component, @Repository) por constructor; cada uno es una única instancia
 *     (singleton) administrada por el contenedor.
 * <p>PATRÓN Arquitectura hexagonal (puertos y adaptadores): el código se reparte en capas: api
 *     (entrada HTTP), aplicacion (casos de uso), dominio (reglas, sin Spring) e infraestructura
 *     (MongoDB e integraciones).
 */
@SpringBootApplication
@ConfigurationPropertiesScan
public class TallerApplication {

    public static void main(String[] args) {
        SpringApplication.run(TallerApplication.class, args);
    }
}
