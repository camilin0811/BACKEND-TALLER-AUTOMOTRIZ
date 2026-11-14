package com.taller.infraestructura.persistencia.documento;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

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
