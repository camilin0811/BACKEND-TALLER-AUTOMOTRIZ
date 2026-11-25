package com.taller.infraestructura.persistencia.documento;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

/**
 * Para qué sirve: Documento de MongoDB de la colección "usuarios".
 * <p>PATRÓN Modelo de persistencia separado del dominio: refleja cómo se guarda el dato (con
 *     anotaciones de Spring Data); Mapeadores lo convierte al objeto del dominio.
 */
@Document("usuarios")
public class UsuarioDoc {

    @Id
    public String id;
    @Indexed(unique = true)
    public String usuario;
    /** Hash BCrypt. Nunca la clave en claro. */
    public String hashClave;
    public String rol;
    public String nombre;
    public String idPersona;
    public boolean activo;
    public Instant ultimoAcceso;
}
