package com.taller.infraestructura.persistencia.documento;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/** Contador atomico por tipo de documento de negocio. */
/**
 * Para qué sirve: Documento de MongoDB de la colección "consecutivos".
 * <p>PATRÓN Modelo de persistencia separado del dominio: refleja cómo se guarda el dato (con
 *     anotaciones de Spring Data); Mapeadores lo convierte al objeto del dominio.
 */
@Document("consecutivos")
public class ConsecutivoDoc {

    @Id
    public String id;
    public long valor;
}
