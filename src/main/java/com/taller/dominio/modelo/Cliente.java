package com.taller.dominio.modelo;

import java.time.LocalDate;

public class Cliente extends Persona {

    private String direccion;
    private final LocalDate fechaRegistro;

    public Cliente(String id, String nombre, String documento, String telefono, String email,
                   String direccion, LocalDate fechaRegistro) {
        super(id, nombre, documento, telefono, email);
        this.direccion = direccion;
        this.fechaRegistro = fechaRegistro == null ? LocalDate.now() : fechaRegistro;
    }

    public static Cliente nuevo(String nombre, String documento, String telefono, String email, String direccion) {
        return new Cliente(null, nombre, documento, telefono, email, direccion, LocalDate.now());
    }

    public void cambiarDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getDireccion() {
        return direccion;
    }

    public LocalDate getFechaRegistro() {
        return fechaRegistro;
    }
}
