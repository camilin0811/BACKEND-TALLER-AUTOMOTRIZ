package com.taller.dominio.modelo;

import com.taller.dominio.excepcion.ReglaNegocioException;

import java.time.Instant;
import java.util.Objects;

/**
 * Credenciales de acceso.
 *
 * <p>La clave **nunca** se guarda en claro: la entidad solo conoce el hash y
 * no tiene forma de devolver el original. Quien la construye debe pasar ya el
 * valor codificado.
 *
 * <p>Para qué sirve: Credenciales de acceso: usuario, hash de la clave y rol.
 * <p>PATRÓN Entidad (DDD): tiene identidad propia (id) y su estado cambia a lo largo del tiempo.
 * <p>PATRÓN Método fábrica estático: nuevo() crea el objeto en un estado inicial válido y le da nombre
 *     a esa forma de crearlo.
 */
public class Usuario {

    private String id;
    private final String usuario;
    private String hashClave;
    private final Rol rol;
    private final String nombre;
    /** Enlaza con el empleado o cliente correspondiente, si aplica. */
    private final String idPersona;
    private boolean activo;
    private Instant ultimoAcceso;

    public Usuario(String id, String usuario, String hashClave, Rol rol, String nombre,
                   String idPersona, boolean activo, Instant ultimoAcceso) {
        this.id = id;
        this.usuario = normalizar(usuario);
        this.hashClave = exigir(hashClave, "hash de la clave");
        this.rol = Objects.requireNonNull(rol, "El rol es obligatorio");
        this.nombre = nombre;
        this.idPersona = idPersona;
        this.activo = activo;
        this.ultimoAcceso = ultimoAcceso;
    }

    public static Usuario nuevo(String usuario, String hashClave, Rol rol, String nombre, String idPersona) {
        return new Usuario(null, usuario, hashClave, rol, nombre, idPersona, true, null);
    }

    private static String normalizar(String valor) {
        return exigir(valor, "usuario").trim().toLowerCase();
    }

    private static String exigir(String valor, String campo) {
        if (valor == null || valor.isBlank()) {
            throw new IllegalArgumentException("El campo %s es obligatorio".formatted(campo));
        }
        return valor;
    }

    /** Un usuario desactivado no puede entrar aunque acierte la clave. */
    public void exigirActivo() {
        if (!activo) {
            throw new ReglaNegocioException("USUARIO_INACTIVO",
                    "El usuario %s esta desactivado".formatted(usuario));
        }
    }

    public void registrarAcceso() {
        this.ultimoAcceso = Instant.now();
    }

    public void cambiarClave(String nuevoHash) {
        this.hashClave = exigir(nuevoHash, "hash de la clave");
    }

    public void desactivar() {
        this.activo = false;
    }

    public boolean puede(Rol.Permiso permiso) {
        return activo && rol.puede(permiso);
    }

    public String getId() {
        return id;
    }

    public void asignarId(String id) {
        this.id = id;
    }

    public String getUsuario() {
        return usuario;
    }

    public String getHashClave() {
        return hashClave;
    }

    public Rol getRol() {
        return rol;
    }

    public String getNombre() {
        return nombre;
    }

    public String getIdPersona() {
        return idPersona;
    }

    public boolean isActivo() {
        return activo;
    }

    public Instant getUltimoAcceso() {
        return ultimoAcceso;
    }

    /** Nunca exponer el hash en registros ni en respuestas. */
    @Override
    public String toString() {
        return "Usuario{%s, rol=%s}".formatted(usuario, rol);
    }
}
