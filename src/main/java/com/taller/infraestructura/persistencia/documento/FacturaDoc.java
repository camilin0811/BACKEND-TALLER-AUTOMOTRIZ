package com.taller.infraestructura.persistencia.documento;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Document("facturas")
public class FacturaDoc {

    @Id
    public String id;
    @Indexed(unique = true, sparse = true)
    public String numero;
    @Indexed
    public String idOrden;
    public Instant fecha;
    public OrdenTrabajoDoc.Referencia cliente;
    public BigDecimal subtotal;
    public BigDecimal iva;
    public BigDecimal total;
    public String cufe;
    @Indexed
    public String estadoDian;
    public Instant fechaValidacion;
    public String motivoRechazo;
    public int intentosEnvio;
    public List<PagoDoc> pagos = new ArrayList<>();

    public static class PagoDoc {
        public BigDecimal monto;
        public String metodo;
        public String referencia;
        public Instant fecha;
    }
}
