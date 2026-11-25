package com.taller.infraestructura.persistencia.documento;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Para qué sirve: Documento de MongoDB de la colección "proveedores".
 * <p>PATRÓN Modelo de persistencia separado del dominio: refleja cómo se guarda el dato (con
 *     anotaciones de Spring Data); Mapeadores lo convierte al objeto del dominio.
 */
@Document("proveedores")
public class ProveedorDoc {

    @Id
    public String id;
    @Indexed(unique = true)
    public String nit;
    public String razonSocial;
    public String telefono;
    public String email;
}
