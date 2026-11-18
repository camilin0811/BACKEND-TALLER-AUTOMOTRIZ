package com.taller.infraestructura.persistencia.documento;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/** Agregado de la orden con servicios, repuestos, diagnosticos e historial incrustados. */
@Document("ordenes")
@CompoundIndex(name = "estado_fecha", def = "{'estado': 1, 'fechaIngreso': -1}")
@CompoundIndex(name = "mecanico_estado", def = "{'mecanico.id': 1, 'estado': 1}")
public class OrdenTrabajoDoc {

    @Id
    public String id;
    @Indexed(unique = true, sparse = true)
    public String numero;
    public String estado;
    public Instant fechaIngreso;
    public Instant fechaEntrega;
    public int kilometrajeIngreso;

    public Referencia cliente;
    public ReferenciaVehiculoDoc vehiculo;
    public Referencia mecanico;
    public String idCita;

    public List<DiagnosticoDoc> diagnosticos = new ArrayList<>();
    public CotizacionDoc cotizacion;
    public List<LineaServicioDoc> servicios = new ArrayList<>();
    public List<LineaRepuestoDoc> repuestos = new ArrayList<>();
    public List<CambioEstadoDoc> historialEstados = new ArrayList<>();
    public List<AnotacionDoc> anotaciones = new ArrayList<>();

    public BigDecimal total;
    public String idFactura;
    public String motivoIngreso;

    /** Bloqueo optimista: dos usuarios sobre la misma orden no se pisan. */
    @Version
    public Long version;

    public static class Referencia {
        public String id;
        public String nombre;
        public String telefono;
    }

    public static class ReferenciaVehiculoDoc {
        public String id;
        @Indexed
        public String placa;
        public String tipo;
        public String descripcion;
    }

    public static class DiagnosticoDoc {
        public Instant fecha;
        public String origen;
        public List<String> codigosFalla = new ArrayList<>();
        public Map<String, Object> lecturasSensores = Map.of();
        public String observaciones;
    }

    public static class CotizacionDoc {
        public String numero;
        public Instant fecha;
        public String estado;
        public String canalAprobacion;
        public Instant fechaRespuesta;
        public BigDecimal total;
        public int vigenciaHoras;
        public String motivoRechazo;
    }

    public static class LineaServicioDoc {
        public String codigo;
        public String descripcion;
        public BigDecimal precio;
        public double horasEstimadas;
    }

    public static class LineaRepuestoDoc {
        public String codigo;
        public String nombre;
        public int cantidad;
        public BigDecimal precioUnitario;
    }

    public static class AnotacionDoc {
        public Instant fecha;
        public String usuario;
        public String tipo;
        public String descripcion;
    }

    public static class CambioEstadoDoc {
        public String estado;
        public Instant fecha;
        public String usuario;
    }
}
