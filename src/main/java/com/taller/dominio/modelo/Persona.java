package com.taller.dominio.modelo;

import java.util.Objects;

/** Base de los actores humanos del sistema. */
/**
 * Para qué sirve: Base de las personas del sistema: nombre, documento y datos de contacto.
 * <p>PATRÓN Herencia y polimorfismo: clase abstracta: Cliente y Empleado la especializan.
 */
public abstract class Persona {

    private String id;
    private final String nombre;
    private final String documento;
    private String telefono;
    private String email;

    protected Persona(String id, String nombre, String documento, String telefono, String email) {
        this.id = id;
        this.nombre = exigirTexto(nombre, "nombre");
        this.documento = exigirTexto(documento, "documento");
        this.telefono = telefono;
        this.email = email;
    }

    protected static String exigirTexto(String valor, String campo) {
        if (valor == null || valor.isBlank()) {
            throw new IllegalArgumentException("El campo %s es obligatorio".formatted(campo));
        }
        return valor.trim();
    }

    public void actualizarContacto(String telefono, String email) {
        this.telefono = telefono;
        this.email = email;
    }

    public String getId() {
        return id;
    }

    public void asignarId(String id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getDocumento() {
        return documento;
    }

    public String getTelefono() {
        return telefono;
    }

    public String getEmail() {
        return email;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Persona otra)) {
            return false;
        }
        return Objects.equals(documento, otra.documento);
    }

    @Override
    public int hashCode() {
        return Objects.hash(documento);
    }
}
