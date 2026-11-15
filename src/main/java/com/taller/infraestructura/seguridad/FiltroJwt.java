package com.taller.infraestructura.seguridad;

import com.taller.dominio.puerto.EmisorTokens;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

/** Traduce el token del encabezado Authorization en una autenticacion de Spring. */
@Component
public class FiltroJwt extends OncePerRequestFilter {

    private static final String PREFIJO = "Bearer ";

    private final EmisorTokens emisor;

    public FiltroJwt(EmisorTokens emisor) {
        this.emisor = emisor;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest peticion,
                                    @NonNull HttpServletResponse respuesta,
                                    @NonNull FilterChain cadena) throws ServletException, IOException {
        String encabezado = peticion.getHeader("Authorization");

        if (encabezado != null && encabezado.startsWith(PREFIJO)) {
            emisor.verificar(encabezado.substring(PREFIJO.length())).ifPresent(identidad -> {
                var autoridad = new SimpleGrantedAuthority("ROLE_" + identidad.rol());
                var autenticacion = new UsernamePasswordAuthenticationToken(
                        identidad.usuario(), null, List.of(autoridad));
                autenticacion.setDetails(new WebAuthenticationDetailsSource().buildDetails(peticion));
                SecurityContextHolder.getContext().setAuthentication(autenticacion);
            });
        }

        cadena.doFilter(peticion, respuesta);
    }
}
