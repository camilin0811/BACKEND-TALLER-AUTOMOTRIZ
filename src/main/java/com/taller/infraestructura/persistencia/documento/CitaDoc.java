package com.taller.infraestructura.persistencia.documento;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

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
