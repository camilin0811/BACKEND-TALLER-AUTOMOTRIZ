package com.taller.infraestructura.seguridad;

import com.taller.dominio.modelo.Usuario;
import com.taller.dominio.puerto.EmisorTokens;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.Optional;

/**
 * Emision y verificacion de tokens JWT firmados con HMAC-SHA256.
 *
 * <p>El secreto viene de configuracion y en produccion debe inyectarse por
 * variable de entorno. Si no se define, la aplicacion no arranca: un secreto
 * por defecto en un sistema productivo equivale a no tener firma.
 */
@Component
public class ProveedorJwt implements EmisorTokens {

    private static final Logger log = LoggerFactory.getLogger(ProveedorJwt.class);

    private final SecretKey clave;
    private final long duracionSegundos;
    private final String emisor;

    public ProveedorJwt(@Value("${taller.seguridad.jwt.secreto}") String secreto,
                        @Value("${taller.seguridad.jwt.duracion-segundos:28800}") long duracionSegundos,
                        @Value("${taller.seguridad.jwt.emisor:taller-backend}") String emisor) {
        if (secreto == null || secreto.getBytes(StandardCharsets.UTF_8).length < 32) {
            throw new IllegalStateException(
                    "taller.seguridad.jwt.secreto debe tener al menos 32 caracteres");
        }
        this.clave = Keys.hmacShaKeyFor(secreto.getBytes(StandardCharsets.UTF_8));
        this.duracionSegundos = duracionSegundos;
        this.emisor = emisor;
    }

    @Override
    public Credencial emitir(Usuario usuario) {
        Instant ahora = Instant.now();
        Instant expira = ahora.plusSeconds(duracionSegundos);
        String token = Jwts.builder()
                .issuer(emisor)
                .subject(usuario.getUsuario())
                .claim("uid", usuario.getId())
                .claim("rol", usuario.getRol().name())
                .claim("nombre", usuario.getNombre())
                .issuedAt(Date.from(ahora))
                .expiration(Date.from(expira))
                .signWith(clave)
                .compact();
        return new Credencial(token, duracionSegundos);
    }

    @Override
    public Optional<Identidad> verificar(String token) {
        try {
            Claims c = Jwts.parser()
                    .verifyWith(clave)
                    .requireIssuer(emisor)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            return Optional.of(new Identidad(
                    c.get("uid", String.class), c.getSubject(), c.get("rol", String.class)));
        } catch (JwtException | IllegalArgumentException e) {
            // Token expirado, firma invalida o manipulado: todos son lo mismo
            // desde fuera. No se detalla el motivo al cliente.
            log.debug("Token rechazado: {}", e.getMessage());
            return Optional.empty();
        }
    }
}
