package com.taller.infraestructura.persistencia.documento;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * Para qué sirve: Documento de MongoDB de la colección "ordenes_compra".
 * <p>PATRÓN Modelo de persistencia separado del dominio: refleja cómo se guarda el dato (con
 *     anotaciones de Spring Data); Mapeadores lo convierte al objeto del dominio.
 */
@Document("ordenes_compra")
public class OrdenCompraDoc {

    @Id
    public String id;
    public String numero;
    public String idProveedor;
    public Instant fecha;
    public String estado;
    public String generadaPor;
    public List<ItemDoc> items = new ArrayList<>();

    public static class ItemDoc {
        public String codigoRepuesto;
        public int cantidad;
        public int cantidadRecibida;
        public BigDecimal precioUnitario;
    }
}
