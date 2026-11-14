package com.taller.dominio.modelo;

import com.taller.dominio.evento.CotizacionAprobada;
import com.taller.dominio.evento.CotizacionEmitida;
import com.taller.dominio.evento.CotizacionRechazada;
import com.taller.dominio.evento.DiagnosticoRegistrado;
import com.taller.dominio.evento.EventoDominio;
import com.taller.dominio.evento.OrdenCerrada;
import com.taller.dominio.evento.OrdenCreada;
import com.taller.dominio.evento.RepuestoConsumido;
import com.taller.dominio.evento.VehiculoEntregado;
import com.taller.dominio.excepcion.ReglaNegocioException;
import com.taller.dominio.excepcion.TransicionInvalidaException;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Agregado central del sistema.
 *
 * <p>Concentra el ciclo de vida del trabajo sobre un vehiculo. Es la unica
 * entidad que cambia de estado, y lo hace exclusivamente a traves de
 * {@link #cambiarEstado}, que delega la validacion en {@link EstadoOrden}.
 *
 * <p>Los cambios relevantes para el negocio quedan registrados como eventos de
 * dominio en {@link #eventosPendientes()}. El servicio de aplicacion los publica
 * tras persistir y luego llama a {@link #limpiarEventos()}.
 */
public class OrdenTrabajo {

    private String id;
    private String numero;
    private EstadoOrden estado;
    private final Instant fechaIngreso;
    private Instant fechaEntrega;
    private final int kilometrajeIngreso;

    private final ReferenciaCliente cliente;
    private final ReferenciaVehiculo vehiculo;
    private ReferenciaMecanico mecanico;
    private final String idCita;

    private final List<Diagnostico> diagnosticos = new ArrayList<>();
    private Cotizacion cotizacion;
    private final List<LineaServicio> servicios = new ArrayList<>();
    private final List<LineaRepuesto> repuestos = new ArrayList<>();
    private final List<CambioEstado> historialEstados = new ArrayList<>();

    private BigDecimal total;
    private String idFactura;
    private Long version;

    private final transient List<EventoDominio> eventos = new ArrayList<>();

    public OrdenTrabajo(String id, String numero, EstadoOrden estado, Instant fechaIngreso, Instant fechaEntrega,
                        int kilometrajeIngreso, ReferenciaCliente cliente, ReferenciaVehiculo vehiculo,
                        ReferenciaMecanico mecanico, String idCita, BigDecimal total, String idFactura,
                        Long version) {
        this.id = id;
        this.numero = numero;
        this.estado = estado == null ? EstadoOrden.RECIBIDA : estado;
        this.fechaIngreso = fechaIngreso == null ? Instant.now() : fechaIngreso;
        this.fechaEntrega = fechaEntrega;
        this.kilometrajeIngreso = kilometrajeIngreso;
        this.cliente = cliente;
        this.vehiculo = vehiculo;
        this.mecanico = mecanico;
        this.idCita = idCita;
        this.total = Montos.normalizar(total);
        this.idFactura = idFactura;
        this.version = version;
    }

    /** Crea una orden recien recibida y registra el evento correspondiente. */
    public static OrdenTrabajo recibir(ReferenciaCliente cliente, ReferenciaVehiculo vehiculo,
                                       int kilometrajeIngreso, String idCita, String usuario) {
        OrdenTrabajo orden = new OrdenTrabajo(null, null, EstadoOrden.RECIBIDA, Instant.now(), null,
                kilometrajeIngreso, cliente, vehiculo, null, idCita, Montos.cero(), null, null);
        orden.historialEstados.add(new CambioEstado(EstadoOrden.RECIBIDA, Instant.now(), usuario));
        orden.eventos.add(new OrdenCreada(null, null, vehiculo.placa(), cliente.id()));
        return orden;
    }

    // ------------------------------------------------------------- transiciones

    public void iniciarDiagnostico(String usuario) {
        cambiarEstado(EstadoOrden.EN_DIAGNOSTICO, usuario);
    }

    public void registrarDiagnostico(Diagnostico diagnostico, String usuario) {
        exigirModificable();
        if (estado == EstadoOrden.RECIBIDA) {
            cambiarEstado(EstadoOrden.EN_DIAGNOSTICO, usuario);
        }
        diagnosticos.add(diagnostico);
        eventos.add(new DiagnosticoRegistrado(id, diagnostico.codigosFalla()));
    }

    public void agregarServicio(LineaServicio servicio) {
        exigirModificable();
        if (estado.ordinal() > EstadoOrden.EN_REPARACION.ordinal()
                && estado != EstadoOrden.EN_ESPERA_REPUESTO) {
            throw new ReglaNegocioException("ORDEN_NO_EDITABLE",
                    "No se pueden agregar servicios a una orden en estado %s".formatted(estado));
        }
        servicios.add(servicio);
        recalcularTotal();
    }

    public void emitirCotizacion(String numeroCotizacion, int vigenciaHoras, String usuario) {
        if (servicios.isEmpty() && repuestos.isEmpty()) {
            throw new ReglaNegocioException("COTIZACION_VACIA",
                    "La cotizacion debe incluir al menos un servicio");
        }
        recalcularTotal();
        this.cotizacion = Cotizacion.emitir(numeroCotizacion, total, vigenciaHoras);
        cambiarEstado(EstadoOrden.COTIZADA, usuario);
        eventos.add(new CotizacionEmitida(id, numeroCotizacion, total, cliente == null ? null : cliente.telefono()));
    }

    public void aprobarCotizacion(CanalAprobacion canal, String usuario) {
        exigirCotizacion();
        cambiarEstado(EstadoOrden.APROBADA, usuario);
        cotizacion.aprobar(canal);
        eventos.add(new CotizacionAprobada(id, canal));
    }

    public void rechazarCotizacion(String motivo, String usuario) {
        exigirCotizacion();
        cambiarEstado(EstadoOrden.RECHAZADA, usuario);
        cotizacion.rechazar(motivo);
        eventos.add(new CotizacionRechazada(id, motivo));
    }

    public void asignarMecanico(ReferenciaMecanico nuevoMecanico, String usuario) {
        if (estado != EstadoOrden.APROBADA) {
            throw new ReglaNegocioException("ORDEN_NO_APROBADA",
                    "La orden debe estar aprobada para asignar mecanico, y esta en %s".formatted(estado));
        }
        this.mecanico = nuevoMecanico;
        cambiarEstado(EstadoOrden.EN_REPARACION, usuario);
    }

    public void registrarRepuesto(LineaRepuesto linea) {
        if (!estado.permiteConsumoDeRepuestos()) {
            throw new ReglaNegocioException("CONSUMO_NO_PERMITIDO",
                    "Solo se registran repuestos con la orden en EN_REPARACION, y esta en %s".formatted(estado));
        }
        repuestos.add(linea);
        recalcularTotal();
        eventos.add(new RepuestoConsumido(id, linea.codigo(), linea.cantidad()));
    }

    public void marcarEsperaRepuesto(String usuario) {
        cambiarEstado(EstadoOrden.EN_ESPERA_REPUESTO, usuario);
    }

    public void reanudarReparacion(String usuario) {
        cambiarEstado(EstadoOrden.EN_REPARACION, usuario);
    }

    public void cerrar(String usuario) {
        recalcularTotal();
        cambiarEstado(EstadoOrden.CERRADA, usuario);
        eventos.add(new OrdenCerrada(id, total, cliente == null ? null : cliente.id()));
    }

    public void marcarFacturada(String idFacturaEmitida, String usuario) {
        if (cotizacion == null || !cotizacion.estaAprobada()) {
            throw new ReglaNegocioException("SIN_COTIZACION_APROBADA",
                    "No se puede facturar una orden sin cotizacion aprobada");
        }
        cambiarEstado(EstadoOrden.FACTURADA, usuario);
        this.idFactura = idFacturaEmitida;
    }

    public void entregar(String usuario) {
        cambiarEstado(EstadoOrden.ENTREGADA, usuario);
        this.fechaEntrega = Instant.now();
        eventos.add(new VehiculoEntregado(id, vehiculo.placa(), kilometrajeIngreso, vehiculo.tipo()));
    }

    private void cambiarEstado(EstadoOrden destino, String usuario) {
        if (!estado.permiteTransicionA(destino)) {
            throw new TransicionInvalidaException(estado, destino);
        }
        this.estado = destino;
        this.historialEstados.add(new CambioEstado(destino, Instant.now(), usuario));
    }

    private void exigirModificable() {
        if (!estado.admiteModificacion()) {
            throw new ReglaNegocioException("ORDEN_SOLO_LECTURA",
                    "La orden %s esta entregada y no admite cambios".formatted(numero));
        }
    }

    private void exigirCotizacion() {
        if (cotizacion == null) {
            throw new ReglaNegocioException("SIN_COTIZACION", "La orden no tiene cotizacion emitida");
        }
    }

    /** El total siempre se deriva de las lineas; nunca se escribe a mano. */
    public BigDecimal recalcularTotal() {
        BigDecimal suma = Montos.cero();
        for (LineaServicio s : servicios) {
            suma = suma.add(s.precio());
        }
        for (LineaRepuesto r : repuestos) {
            suma = suma.add(r.subtotal());
        }
        this.total = Montos.normalizar(suma);
        return this.total;
    }

    // ----------------------------------------------------------------- eventos

    public List<EventoDominio> eventosPendientes() {
        return Collections.unmodifiableList(eventos);
    }

    public void limpiarEventos() {
        eventos.clear();
    }

    // ----------------------------------------------------------------- lectura

    public String getId() {
        return id;
    }

    public void asignarId(String id) {
        this.id = id;
    }

    public String getNumero() {
        return numero;
    }

    public void asignarNumero(String numero) {
        this.numero = numero;
    }

    public EstadoOrden getEstado() {
        return estado;
    }

    public Instant getFechaIngreso() {
        return fechaIngreso;
    }

    public Instant getFechaEntrega() {
        return fechaEntrega;
    }

    public int getKilometrajeIngreso() {
        return kilometrajeIngreso;
    }

    public ReferenciaCliente getCliente() {
        return cliente;
    }

    public ReferenciaVehiculo getVehiculo() {
        return vehiculo;
    }

    public ReferenciaMecanico getMecanico() {
        return mecanico;
    }

    public String getIdCita() {
        return idCita;
    }

    public List<Diagnostico> getDiagnosticos() {
        return Collections.unmodifiableList(diagnosticos);
    }

    public Cotizacion getCotizacion() {
        return cotizacion;
    }

    public List<LineaServicio> getServicios() {
        return Collections.unmodifiableList(servicios);
    }

    public List<LineaRepuesto> getRepuestos() {
        return Collections.unmodifiableList(repuestos);
    }

    public List<CambioEstado> getHistorialEstados() {
        return Collections.unmodifiableList(historialEstados);
    }

    public BigDecimal getTotal() {
        return total;
    }

    public String getIdFactura() {
        return idFactura;
    }

    public Long getVersion() {
        return version;
    }

    /** Rehidratacion desde persistencia: repuebla las colecciones embebidas. */
    public void rehidratar(List<Diagnostico> diags, Cotizacion cot, List<LineaServicio> servs,
                           List<LineaRepuesto> reps, List<CambioEstado> historial) {
        if (diags != null) {
            diagnosticos.addAll(diags);
        }
        this.cotizacion = cot;
        if (servs != null) {
            servicios.addAll(servs);
        }
        if (reps != null) {
            repuestos.addAll(reps);
        }
        if (historial != null) {
            historialEstados.addAll(historial);
        }
    }
}
