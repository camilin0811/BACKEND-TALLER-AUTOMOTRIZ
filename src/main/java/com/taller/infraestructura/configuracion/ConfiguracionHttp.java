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
 *
 * <p>Para qué sirve: Fija tiempos de espera a los clientes HTTP que usan las integraciones.
 * <p>PATRÓN Factory (@Bean de Spring): el método @Bean fabrica el objeto que Spring inyecta donde se
 *     necesite.
 * <p>PATRÓN Timeout: patrón de resiliencia: una integración lenta falla rápido en lugar de bloquear
 *     hilos del servidor.
 */
@Configuration
public class ConfiguracionHttp {

    @Bean
    // PATRÓN Factory (@Bean) + Timeout: fabrica la configuración de tiempos de espera que Spring aplica a los clientes HTTP.
    public RestClientCustomizer tiemposDeEspera() {
        return builder -> builder.requestFactory(ClientHttpRequestFactories.get(
                ClientHttpRequestFactorySettings.DEFAULTS
                        .withConnectTimeout(Duration.ofSeconds(5))
                        .withReadTimeout(Duration.ofSeconds(15))));
    }
}
