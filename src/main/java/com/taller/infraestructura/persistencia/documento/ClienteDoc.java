package com.taller.infraestructura.persistencia.documento;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;

/**
 * Para qué sirve: Documento de MongoDB de la colección "clientes".
 * <p>PATRÓN Modelo de persistencia separado del dominio: refleja cómo se guarda el dato (con
 *     anotaciones de Spring Data); Mapeadores lo convierte al objeto del dominio.
 */
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
