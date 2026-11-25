package com.taller.api.controlador;

import com.taller.api.dto.CambiarClaveDTO;
import com.taller.api.dto.LoginDTO;
import com.taller.api.dto.TokenDTO;
import com.taller.aplicacion.servicio.AutenticacionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Para qué sirve: Inicio de sesión, datos del usuario autenticado y cambio de clave (/api/v1/auth).
 * <p>PATRÓN Controller (MVC): recibe la petición HTTP, valida la entrada y delega en la capa de
 *     servicio; no contiene reglas de negocio.
 * <p>PATRÓN DTO (Data Transfer Object): entra y sale por objetos de transferencia (LoginDTO, TokenDTO,
 *     CambiarClaveDTO), nunca expone las entidades del dominio.
 */
@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "Autenticacion")
public class AutenticacionControlador {

    private final AutenticacionService autenticacion;

    public AutenticacionControlador(AutenticacionService autenticacion) {
        this.autenticacion = autenticacion;
    }

    @PostMapping("/login")
    @Operation(summary = "Inicia sesión y devuelve el token")
    public TokenDTO entrar(@Valid @RequestBody LoginDTO dto) {
        AutenticacionService.Sesion sesion = autenticacion.autenticar(dto.usuario(), dto.clave());
        return new TokenDTO(sesion.token(), sesion.tipo(), sesion.expiraEnSegundos(),
                sesion.usuario(), sesion.rol(), sesion.nombre());
    }

    @GetMapping("/yo")
    @Operation(summary = "Datos del usuario autenticado")
    public Map<String, Object> yo(Authentication autenticado) {
        return autenticacion.perfilDe(autenticado.getName());
    }

    @PostMapping("/clave")
    @Operation(summary = "Cambia la clave del usuario autenticado")
    public ResponseEntity<Void> cambiarClave(Authentication autenticado,
                                             @Valid @RequestBody CambiarClaveDTO dto) {
        autenticacion.cambiarClave(autenticado.getName(), dto.claveActual(), dto.claveNueva());
        return ResponseEntity.noContent().build();
    }
}
