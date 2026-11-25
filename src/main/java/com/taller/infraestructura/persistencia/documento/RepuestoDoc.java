package com.taller.infraestructura.persistencia.documento;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Para qué sirve: Documento de MongoDB de la colección "repuestos".
 * <p>PATRÓN Modelo de persistencia separado del dominio: refleja cómo se guarda el dato (con
 *     anotaciones de Spring Data); Mapeadores lo convierte al objeto del dominio.
 */
@Document("repuestos")
public class RepuestoDoc {

    @Id
    public String id;
    @Indexed(unique = true)
    public String codigo;
    public String nombre;
    public BigDecimal precio;
    public int stock;
    public int stockMinimo;
    public List<String> idsProveedores = new ArrayList<>();
}
