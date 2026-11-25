package com.taller.infraestructura.persistencia.documento;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;

/**
 * Para qué sirve: Documento de MongoDB de la colección "servicios".
 * <p>PATRÓN Modelo de persistencia separado del dominio: refleja cómo se guarda el dato (con
 *     anotaciones de Spring Data); Mapeadores lo convierte al objeto del dominio.
 */
@Document("servicios")
public class ServicioDoc {

    @Id
    public String id;
    @Indexed(unique = true)
    public String codigo;
    public String descripcion;
    public BigDecimal precio;
    public double horasEstimadas;
    public boolean activo;
}
