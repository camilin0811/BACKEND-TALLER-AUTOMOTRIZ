package com.taller.dominio.modelo;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Mecanico extends Empleado {

    private String especialidad;
    private boolean disponible;

    public Mecanico(String id, String nombre, String documento, String telefono, String email,
                    String codigo, BigDecimal salario, LocalDate fechaIngreso,
                    String especialidad, boolean disponible) {
        super(id, nombre, documento, telefono, email, codigo, "MECANICO", salario, fechaIngreso);
        this.especialidad = especialidad;
        this.disponible = disponible;
    }

    public static Mecanico nuevo(String nombre, String documento, String codigo, String especialidad) {
        return new Mecanico(null, nombre, documento, null, null, codigo, null, LocalDate.now(), especialidad, true);
    }

    public void ocupar() {
        this.disponible = false;
    }

    public void liberar() {
        this.disponible = true;
    }

    public String getEspecialidad() {
        return especialidad;
    }

    public void cambiarEspecialidad(String especialidad) {
        this.especialidad = especialidad;
    }

    public boolean isDisponible() {
        return disponible;
    }
}
