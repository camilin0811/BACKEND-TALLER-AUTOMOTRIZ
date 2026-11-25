package com.taller.dominio.modelo;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Para qué sirve: Recepcionista del taller, con su turno.
 * <p>PATRÓN Entidad (DDD): tiene identidad propia (id) y su estado cambia a lo largo del tiempo.
 * <p>PATRÓN Método fábrica estático: nuevo() crea el objeto en un estado inicial válido y le da nombre
 *     a esa forma de crearlo.
 * <p>PATRÓN Herencia y polimorfismo: especializa Empleado.
 */
public class Recepcionista extends Empleado {

    private String turno;

    public Recepcionista(String id, String nombre, String documento, String telefono, String email,
                         String codigo, BigDecimal salario, LocalDate fechaIngreso, String turno) {
        super(id, nombre, documento, telefono, email, codigo, "RECEPCIONISTA", salario, fechaIngreso);
        this.turno = turno;
    }

    public static Recepcionista nuevo(String nombre, String documento, String codigo, String turno) {
        return new Recepcionista(null, nombre, documento, null, null, codigo, null, LocalDate.now(), turno);
    }

    public String getTurno() {
        return turno;
    }

    public void cambiarTurno(String turno) {
        this.turno = turno;
    }
}
