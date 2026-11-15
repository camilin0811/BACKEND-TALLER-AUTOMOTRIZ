package com.taller.infraestructura.persistencia.documento;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

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
