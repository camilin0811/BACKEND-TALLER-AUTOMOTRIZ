package com.taller.infraestructura.configuracion;

import com.taller.infraestructura.seguridad.FiltroJwt;
import com.taller.infraestructura.seguridad.RespuestasDeSeguridad;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Matriz de permisos de la API.
 *
 * <p>Traduce a reglas HTTP la tabla documentada en la wiki. Las rutas publicas
 * se declaran una por una: dejar un comodin abierto es la forma mas comun de
 * exponer un endpoint sin darse cuenta.
 */
@Configuration
@EnableWebSecurity
public class ConfiguracionSeguridad {

    private static final String REC = "RECEPCIONISTA";
    private static final String MEC = "MECANICO";
    private static final String ADM = "ADMINISTRADOR";

    private final FiltroJwt filtroJwt;
    private final RespuestasDeSeguridad respuestas;

    public ConfiguracionSeguridad(FiltroJwt filtroJwt, RespuestasDeSeguridad respuestas) {
        this.filtroJwt = filtroJwt;
        this.respuestas = respuestas;
    }

    @Bean
    public SecurityFilterChain cadena(HttpSecurity http) throws Exception {
        http
            // Sin sesiones ni formularios: la API es sin estado y el token
            // viaja en cada peticion, asi que CSRF no aplica.
            .csrf(csrf -> csrf.disable())
            .cors(cors -> {})
            .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .exceptionHandling(e -> e
                    .authenticationEntryPoint(respuestas)
                    .accessDeniedHandler(respuestas))
            .authorizeHttpRequests(reglas -> reglas
                    // --- publico
                    .requestMatchers("/api/v1/auth/login").permitAll()
                    .requestMatchers("/api/v1/webhooks/**").permitAll()
                    .requestMatchers("/api/v1/publico/**").permitAll()
                    .requestMatchers("/actuator/health").permitAll()
                    .requestMatchers("/api-docs/**", "/v3/api-docs/**", "/swagger-ui/**").permitAll()
                    .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                    // --- inventario
                    .requestMatchers(HttpMethod.POST, "/api/v1/repuestos/**").hasRole(ADM)
                    .requestMatchers(HttpMethod.POST, "/api/v1/ordenes-compra/**").hasAnyRole(ADM, REC)
                    .requestMatchers("/api/v1/repuestos/**", "/api/v1/ordenes-compra/**")
                        .hasAnyRole(ADM, REC, MEC)

                    // --- consumo de repuestos: lo hace quien repara
                    .requestMatchers(HttpMethod.POST, "/api/v1/ordenes/*/repuestos")
                        .hasAnyRole(MEC, ADM, REC)

                    // --- ordenes
                    .requestMatchers("/api/v1/ordenes/**").hasAnyRole(REC, MEC, ADM)

                    // --- facturacion: el mecanico no factura
                    .requestMatchers("/api/v1/facturas/**").hasAnyRole(REC, ADM)

                    // --- maestros
                    .requestMatchers("/api/v1/clientes/**", "/api/v1/vehiculos/**",
                                     "/api/v1/citas/**", "/api/v1/mecanicos/**")
                        .hasAnyRole(REC, ADM)

                    .anyRequest().authenticated())
            .addFilterBefore(filtroJwt, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
