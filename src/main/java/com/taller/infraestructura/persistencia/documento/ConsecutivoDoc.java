package com.taller.infraestructura.persistencia.documento;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/** Contador atomico por tipo de documento de negocio. */
@Document("consecutivos")
public class ConsecutivoDoc {

    @Id
    public String id;
    public long valor;
}
