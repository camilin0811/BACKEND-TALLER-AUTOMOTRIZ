package com.taller.infraestructura.configuracion;

import org.springframework.boot.web.client.ClientHttpRequestFactorySettings;
import org.springframework.boot.web.client.RestClientCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.boot.web.client.ClientHttpRequestFactories;

import java.time.Duration;

/**
 * Tiempos de espera para las integraciones.
 *
 * <p>Sin limite explicito, una dependencia lenta deja hilos ocupados hasta
 * agotar el servidor. Prefiero fallar rapido y reintentar.
 */
@Configuration
public class ConfiguracionHttp {

    @Bean
    public RestClientCustomizer tiemposDeEspera() {
        return builder -> builder.requestFactory(ClientHttpRequestFactories.get(
                ClientHttpRequestFactorySettings.DEFAULTS
                        .withConnectTimeout(Duration.ofSeconds(5))
                        .withReadTimeout(Duration.ofSeconds(15))));
    }
}
