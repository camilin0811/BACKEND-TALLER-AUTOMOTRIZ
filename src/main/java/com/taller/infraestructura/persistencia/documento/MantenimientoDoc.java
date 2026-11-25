package com.taller.infraestructura.persistencia.documento;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;

/**
 * Para qué sirve: Documento de MongoDB de la colección "mantenimientos".
 * <p>PATRÓN Modelo de persistencia separado del dominio: refleja cómo se guarda el dato (con
 *     anotaciones de Spring Data); Mapeadores lo convierte al objeto del dominio.
 */
@Document("mantenimientos")
@CompoundIndex(name = "pendientes", def = "{'fechaSugerida': 1, 'notificado': 1}")
public class MantenimientoDoc {

    @Id
    public String id;
    public String placa;
    public String tipo;
    public int kilometrajeObjetivo;
    public LocalDate fechaSugerida;
    public boolean notificado;
}
