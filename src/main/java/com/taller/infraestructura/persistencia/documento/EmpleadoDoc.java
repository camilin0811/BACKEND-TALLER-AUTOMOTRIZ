package com.taller.infraestructura.persistencia.documento;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDate;

/** Mecanicos y recepcionistas en una coleccion, discriminados por cargo. */
@Document("empleados")
public class EmpleadoDoc {

    @Id
    public String id;
    public String cargo;
    @Indexed(unique = true)
    public String documento;
    public String codigo;
    public String nombre;
    public String telefono;
    public String email;
    public BigDecimal salario;
    public LocalDate fechaIngreso;

    public String especialidad;
    public Boolean disponible;
    public String turno;
}
