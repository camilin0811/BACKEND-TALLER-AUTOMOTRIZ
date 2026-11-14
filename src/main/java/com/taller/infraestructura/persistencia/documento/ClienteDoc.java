package com.taller.infraestructura.persistencia.documento;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;

@Document("clientes")
public class ClienteDoc {

    @Id
    public String id;
    @Indexed(unique = true)
    public String documento;
    public String nombre;
    public String telefono;
    public String email;
    public String direccion;
    public LocalDate fechaRegistro;
}
