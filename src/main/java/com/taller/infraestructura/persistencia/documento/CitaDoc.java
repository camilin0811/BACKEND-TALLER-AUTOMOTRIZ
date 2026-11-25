package com.taller.infraestructura.persistencia.documento;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

/**
 * Para qué sirve: Documento de MongoDB de la colección "citas".
 * <p>PATRÓN Modelo de persistencia separado del dominio: refleja cómo se guarda el dato (con
 *     anotaciones de Spring Data); Mapeadores lo convierte al objeto del dominio.
 */
@Document("citas")
public class CitaDoc {

    @Id
    public String id;
    public String numero;
    public String idCliente;
    public String placa;
    @Indexed
    public LocalDateTime fechaHora;
    public String motivo;
    public String estado;
    public String canal;
}
