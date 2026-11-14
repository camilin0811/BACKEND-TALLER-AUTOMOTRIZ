package com.taller.infraestructura.persistencia.documento;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Un solo documento para automoviles y motocicletas, con el campo {@code tipo}
 * como discriminador (ADR-004). La consulta dominante es por placa y no sabe
 * de antemano el tipo, asi que separarlos obligaria a consultar dos veces.
 */
@Document("vehiculos")
public class VehiculoDoc {

    @Id
    public String id;
    public String tipo;
    @Indexed(unique = true)
    public String placa;
    public String marca;
    public String linea;
    public int anio;
    public int kilometraje;
    public String vin;
    public Integer cilindrada;
    @Indexed
    public String idCliente;

    // Propios del automovil
    public Integer numeroPuertas;
    public String tipoCombustible;
    public Integer numeroPasajeros;
    public String tipoTransmision;

    // Propios de la motocicleta
    public String tipoMotor;
    public Integer numeroTiempos;
    public String categoria;
    public Boolean llevaParrilla;
}
