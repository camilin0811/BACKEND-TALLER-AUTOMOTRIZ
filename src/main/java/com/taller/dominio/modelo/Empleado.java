package com.taller.dominio.modelo;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Base de los empleados del taller.
 *
 * <p>Los roles se modelan como subclases (ADR-010). Es una simplificacion
 * valida para una plantilla estable, pero impide que un empleado cumpla dos
 * funciones; queda registrado como deuda de diseno en la wiki.
 */
public abstract class Empleado extends Persona {

    private final String codigo;
    private final String cargo;
    private BigDecimal salario;
    private final LocalDate fechaIngreso;

    protected Empleado(String id, String nombre, String documento, String telefono, String email,
                       String codigo, String cargo, BigDecimal salario, LocalDate fechaIngreso) {
        super(id, nombre, documento, telefono, email);
        this.codigo = exigirTexto(codigo, "codigo");
        this.cargo = cargo;
        this.salario = salario;
        this.fechaIngreso = fechaIngreso == null ? LocalDate.now() : fechaIngreso;
    }

    public String getCodigo() {
        return codigo;
    }

    public String getCargo() {
        return cargo;
    }

    public BigDecimal getSalario() {
        return salario;
    }

    public LocalDate getFechaIngreso() {
        return fechaIngreso;
    }
}
