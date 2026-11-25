package com.taller.infraestructura.persistencia.documento;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

/**
 * Para qué sirve: Documento de MongoDB de la colección "movimientos_inventario".
 * <p>PATRÓN Modelo de persistencia separado del dominio: refleja cómo se guarda el dato (con
 *     anotaciones de Spring Data); Mapeadores lo convierte al objeto del dominio.
 */
@Document("movimientos_inventario")
@CompoundIndex(name = "repuesto_fecha", def = "{'codigoRepuesto': 1, 'fecha': -1}")
public class MovimientoInventarioDoc {

    @Id
    public String id;
    public String codigoRepuesto;
    public String tipo;
    public int cantidad;
    public String motivo;
    public String idOrden;
    public int stockResultante;
    public Instant fecha;
}
