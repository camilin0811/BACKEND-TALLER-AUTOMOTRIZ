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
 *
 * <p>Para qué sirve: Matriz de permisos HTTP: qué rutas son públicas, qué roles acceden a cada una y
 * dónde va el filtro JWT.
 * <p>PATRÓN Builder: HttpSecurity es un constructor fluido: se encadenan las reglas y build() arma la
 *     cadena de seguridad.
 * <p>PATRÓN Chain of Responsibility: registra FiltroJwt en la cadena de filtros por la que pasa cada
 *     petición.
 * <p>PATRÓN RBAC (control de acceso por roles): aplica por ruta los roles definidos en el dominio
 *     (Rol).
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
    // PATRÓN Builder + Chain of Responsibility: HttpSecurity arma, con llamadas encadenadas, la cadena de filtros.
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
                    // Compras: el administrador revisa, ajusta y envia al
                    // proveedor; la recepcion registra la mercancia que llega.
                    .requestMatchers(HttpMethod.POST, "/api/v1/ordenes-compra/*/recepcion").hasAnyRole(ADM, REC)
                    .requestMatchers(HttpMethod.PATCH, "/api/v1/ordenes-compra/**").hasRole(ADM)
                    .requestMatchers(HttpMethod.POST, "/api/v1/ordenes-compra/**").hasRole(ADM)
                    .requestMatchers("/api/v1/repuestos/**", "/api/v1/ordenes-compra/**")
                        .hasAnyRole(ADM, REC, MEC)

                    // --- consumo de repuestos: lo hace quien repara
                    .requestMatchers(HttpMethod.POST, "/api/v1/ordenes/*/repuestos")
                        .hasAnyRole(MEC, ADM, REC)

                    // --- ordenes: asignar o cambiar mecanico es de recepcion
                    .requestMatchers(HttpMethod.PATCH, "/api/v1/ordenes/*/mecanico").hasAnyRole(REC, ADM)
                    .requestMatchers("/api/v1/ordenes/**").hasAnyRole(REC, MEC, ADM)

                    // --- facturacion: el mecanico no factura
                    .requestMatchers("/api/v1/facturas/**").hasAnyRole(REC, ADM)

                    // --- maestros
                    .requestMatchers("/api/v1/clientes/**", "/api/v1/vehiculos/**",
                                     "/api/v1/citas/**", "/api/v1/mecanicos/**")
                        .hasAnyRole(REC, ADM)

                    .anyRequest().authenticated())
            // PATRÓN Chain of Responsibility: FiltroJwt se inserta en la cadena antes del filtro de usuario y clave.
            .addFilterBefore(filtroJwt, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
