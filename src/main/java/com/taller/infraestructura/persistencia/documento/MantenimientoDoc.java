package com.taller.infraestructura.persistencia.documento;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;

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
