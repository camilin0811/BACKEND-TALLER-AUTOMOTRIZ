package com.taller.infraestructura.persistencia.documento;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Document("ordenes_compra")
public class OrdenCompraDoc {

    @Id
    public String id;
    public String numero;
    public String idProveedor;
    public Instant fecha;
    public String estado;
    public List<ItemDoc> items = new ArrayList<>();

    public static class ItemDoc {
        public String codigoRepuesto;
        public int cantidad;
        public int cantidadRecibida;
        public BigDecimal precioUnitario;
    }
}
