package com.taller.infraestructura.configuracion;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Para qué sirve: Describe la API para la documentación de Swagger UI.
 * <p>PATRÓN Factory (@Bean de Spring): el método @Bean fabrica la descripción OpenAPI.
 */
@Configuration
public class ConfiguracionOpenApi {

    @Bean
    public OpenAPI definicionDeLaApi() {
        return new OpenAPI().info(new Info()
                .title("Sistema de Gestion del Taller Automotriz Automatizado")
                .version("1.0.0")
                .description("""
                        API del backend del taller de automoviles y motocicletas.
                        El diseno completo esta en la wiki del repositorio.""")
                .contact(new Contact().name("Andres Camilo Munoz Bolanos"))
                .license(new License().name("Uso interno")));
    }
}
