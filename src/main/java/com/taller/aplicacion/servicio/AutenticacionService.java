package com.taller.aplicacion.servicio;

import com.taller.dominio.excepcion.CredencialesInvalidasException;
import com.taller.dominio.excepcion.RecursoDuplicadoException;
import com.taller.dominio.excepcion.RecursoNoEncontradoException;
import com.taller.dominio.excepcion.ReglaNegocioException;
import com.taller.dominio.modelo.Rol;
import com.taller.dominio.modelo.Usuario;
import com.taller.dominio.puerto.CodificadorClaves;
import com.taller.dominio.puerto.EmisorTokens;
import com.taller.dominio.puerto.UsuarioRepositorio;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Para qué sirve: Valida usuario y clave, emite el token de sesión y cambia la clave.
 * <p>PATRÓN Service Layer (capa de servicio, fachada de casos de uso): orquesta el caso de uso: carga
 *     del repositorio, delega las reglas en el dominio y guarda. Los controladores solo hablan con esta
 *     fachada.
 * <p>PATRÓN Inyección de dependencias / Inversión de control: recibe los puertos (interfaces del
 *     dominio) por constructor; no conoce MongoDB ni las integraciones concretas.
 */
@Service
public class AutenticacionService {

    /**
     * Resultado de un inicio de sesion.
     *
     * <p>Vive aqui y no en el puerto para que el controlador no tenga que
     * importar nada del dominio: la API habla con la capa de aplicacion.
     */
    public record Sesion(String token, String tipo, long expiraEnSegundos,
                         String usuario, String rol, String nombre) {
    }

    private static final Logger log = LoggerFactory.getLogger(AutenticacionService.class);

    private final UsuarioRepositorio usuarios;
    private final CodificadorClaves codificador;
    private final EmisorTokens emisor;

    public AutenticacionService(UsuarioRepositorio usuarios, CodificadorClaves codificador,
                                EmisorTokens emisor) {
        this.usuarios = usuarios;
        this.codificador = codificador;
        this.emisor = emisor;
    }

    /**
     * Autentica y emite el token.
     *
     * <p>El mensaje de error es el mismo si el usuario no existe o si la clave
     * es incorrecta: distinguirlos le dice a un atacante que usuarios son
     * validos.
     */
    public Sesion autenticar(String nombreUsuario, String claveEnClaro) {
        Usuario usuario = usuarios.buscarPorUsuario(nombreUsuario == null ? "" : nombreUsuario.trim().toLowerCase())
                .orElse(null);

        if (usuario == null || !codificador.coincide(claveEnClaro, usuario.getHashClave())) {
            log.warn("Intento de acceso fallido para el usuario '{}'", nombreUsuario);
            throw new CredencialesInvalidasException();
        }
        usuario.exigirActivo();

        usuario.registrarAcceso();
        usuarios.guardar(usuario);
        log.info("Acceso concedido a '{}' con rol {}", usuario.getUsuario(), usuario.getRol());

        EmisorTokens.Credencial credencial = emisor.emitir(usuario);
        return new Sesion(credencial.token(), "Bearer", credencial.expiraEnSegundos(),
                usuario.getUsuario(), usuario.getRol().name(), usuario.getNombre());
    }

    public Usuario obtenerPorNombre(String nombreUsuario) {
        return usuarios.buscarPorUsuario(nombreUsuario)
                .orElseThrow(() -> new RecursoNoEncontradoException("usuario", nombreUsuario));
    }

    /** Perfil del usuario autenticado, listo para la API. */
    public java.util.Map<String, Object> perfilDe(String nombreUsuario) {
        Usuario usuario = obtenerPorNombre(nombreUsuario);
        return java.util.Map.of(
                "usuario", usuario.getUsuario(),
                "nombre", String.valueOf(usuario.getNombre()),
                "rol", usuario.getRol().name(),
                "permisos", usuario.getRol().permisos().stream().map(Enum::name).sorted().toList());
    }

    public Usuario registrar(String nombreUsuario, String claveEnClaro, Rol rol,
                             String nombre, String idPersona) {
        usuarios.buscarPorUsuario(nombreUsuario.trim().toLowerCase()).ifPresent(existente -> {
            throw new RecursoDuplicadoException("Ya existe el usuario %s".formatted(nombreUsuario));
        });
        exigirClaveRazonable(claveEnClaro);
        return usuarios.guardar(
                Usuario.nuevo(nombreUsuario, codificador.codificar(claveEnClaro), rol, nombre, idPersona));
    }

    public void cambiarClave(String nombreUsuario, String claveActual, String claveNueva) {
        Usuario usuario = obtenerPorNombre(nombreUsuario);
        if (!codificador.coincide(claveActual, usuario.getHashClave())) {
            throw new CredencialesInvalidasException();
        }
        exigirClaveRazonable(claveNueva);
        usuario.cambiarClave(codificador.codificar(claveNueva));
        usuarios.guardar(usuario);
    }

    private void exigirClaveRazonable(String clave) {
        if (clave == null || clave.length() < 8) {
            throw new ReglaNegocioException("CLAVE_DEBIL",
                    "La clave debe tener al menos 8 caracteres");
        }
    }
}
