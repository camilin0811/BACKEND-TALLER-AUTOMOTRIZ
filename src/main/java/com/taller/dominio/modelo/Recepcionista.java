package com.taller.dominio.modelo;

import java.math.BigDecimal;
import java.time.LocalDate;

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
