package com.taller.dominio.modelo;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Para qué sirve: Mecánico del taller: código, especialidad y disponibilidad.
 * <p>PATRÓN Entidad (DDD): tiene identidad propia (id) y su estado cambia a lo largo del tiempo.
 * <p>PATRÓN Método fábrica estático: nuevo() crea el objeto en un estado inicial válido y le da nombre
 *     a esa forma de crearlo.
 * <p>PATRÓN Herencia y polimorfismo: especializa Empleado.
 */
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
