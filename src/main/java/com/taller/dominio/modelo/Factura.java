package com.taller.dominio.modelo;

import com.taller.dominio.excepcion.ReglaNegocioException;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Documento fiscal.
 *
 * <p>Sin CUFE no tiene efecto legal, asi que el codigo solo se puebla cuando la
 * DIAN responde validando. Una factura validada no se modifica: se anula
 * emitiendo una nota de credito.
 */
public class Factura {

    private String id;
    private String numero;
    private final String idOrden;
    private final Instant fecha;
    private final ReferenciaCliente cliente;
    private final BigDecimal subtotal;
    private final BigDecimal iva;
    private final BigDecimal total;
    private String cufe;
    private EstadoDian estadoDian;
    private Instant fechaValidacion;
    private String motivoRechazo;
    private int intentosEnvio;
    private final List<Pago> pagos = new ArrayList<>();

    public Factura(String id, String numero, String idOrden, Instant fecha, ReferenciaCliente cliente,
                   BigDecimal subtotal, BigDecimal iva, BigDecimal total, String cufe,
                   EstadoDian estadoDian, Instant fechaValidacion, String motivoRechazo,
                   int intentosEnvio, List<Pago> pagos) {
        this.id = id;
        this.numero = numero;
        this.idOrden = idOrden;
        this.fecha = fecha == null ? Instant.now() : fecha;
        this.cliente = cliente;
        this.subtotal = Montos.normalizar(subtotal);
        this.iva = Montos.normalizar(iva);
        this.total = Montos.normalizar(total);
        this.cufe = cufe;
        this.estadoDian = estadoDian == null ? EstadoDian.PENDIENTE_ENVIO : estadoDian;
        this.fechaValidacion = fechaValidacion;
        this.motivoRechazo = motivoRechazo;
        this.intentosEnvio = intentosEnvio;
        if (pagos != null) {
            this.pagos.addAll(pagos);
        }
    }

    /** Construye la factura a partir del total de la orden y la tarifa de IVA. */
    public static Factura emitirPara(String idOrden, ReferenciaCliente cliente,
                                     BigDecimal totalOrden, BigDecimal tarifaIva) {
        BigDecimal base = Montos.normalizar(totalOrden);
        BigDecimal impuesto = Montos.normalizar(base.multiply(tarifaIva));
        BigDecimal granTotal = Montos.normalizar(base.add(impuesto));
        return new Factura(null, null, idOrden, Instant.now(), cliente, base, impuesto, granTotal,
                null, EstadoDian.PENDIENTE_ENVIO, null, null, 0, List.of());
    }

    public void marcarEnviada() {
        this.estadoDian = EstadoDian.ENVIADA;
        this.intentosEnvio++;
    }

    public void marcarValidada(String cufeRecibido) {
        if (cufeRecibido == null || cufeRecibido.isBlank()) {
            throw new ReglaNegocioException("CUFE_VACIO", "La DIAN no devolvió CUFE");
        }
        this.cufe = cufeRecibido;
        this.estadoDian = EstadoDian.VALIDADA;
        this.fechaValidacion = Instant.now();
        this.motivoRechazo = null;
    }

    public void marcarRechazada(String motivo) {
        this.estadoDian = EstadoDian.RECHAZADA;
        this.motivoRechazo = motivo;
    }

    public void marcarPendienteDeEnvio() {
        this.estadoDian = EstadoDian.PENDIENTE_ENVIO;
    }

    public boolean estaValidada() {
        return estadoDian == EstadoDian.VALIDADA && cufe != null;
    }

    /** Invariante: la suma de los pagos nunca excede el total de la factura. */
    public void registrarPago(Pago pago) {
        BigDecimal saldo = saldoPendiente();
        if (pago.monto().compareTo(saldo) > 0) {
            throw new ReglaNegocioException("PAGO_EXCEDE_SALDO",
                    "El pago excede el saldo pendiente (%s)".formatted(saldo.toPlainString()));
        }
        pagos.add(pago);
    }

    public BigDecimal totalPagado() {
        BigDecimal suma = Montos.cero();
        for (Pago pago : pagos) {
            suma = suma.add(pago.monto());
        }
        return Montos.normalizar(suma);
    }

    public BigDecimal saldoPendiente() {
        return Montos.normalizar(total.subtract(totalPagado()));
    }

    public boolean estaSaldada() {
        return saldoPendiente().compareTo(BigDecimal.ZERO) == 0;
    }

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

    public String getIdOrden() {
        return idOrden;
    }

    public Instant getFecha() {
        return fecha;
    }

    public ReferenciaCliente getCliente() {
        return cliente;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public BigDecimal getIva() {
        return iva;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public String getCufe() {
        return cufe;
    }

    public EstadoDian getEstadoDian() {
        return estadoDian;
    }

    public Instant getFechaValidacion() {
        return fechaValidacion;
    }

    public String getMotivoRechazo() {
        return motivoRechazo;
    }

    public int getIntentosEnvio() {
        return intentosEnvio;
    }

    public List<Pago> getPagos() {
        return Collections.unmodifiableList(pagos);
    }
}
