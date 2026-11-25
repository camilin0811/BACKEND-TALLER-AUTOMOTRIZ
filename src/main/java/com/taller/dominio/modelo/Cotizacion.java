package com.taller.dominio.modelo;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;

/**
 * Para qué sirve: Presupuesto que se envía al cliente: número, total, vigencia y si fue aprobado o
 * rechazado.
 * <p>PATRÓN Agregado y raíz de agregado (DDD): forma parte del agregado OrdenTrabajo: solo se modifica
 *     a través de la orden.
 * <p>PATRÓN Método fábrica estático: emitir() crea el objeto en un estado inicial válido y le da
 *     nombre a esa forma de crearlo.
 */
public class Cotizacion {

    private final String numero;
    private final Instant fecha;
    private EstadoCotizacion estado;
    private CanalAprobacion canalAprobacion;
    private Instant fechaRespuesta;
    private final BigDecimal total;
    private final int vigenciaHoras;
    private String motivoRechazo;

    public Cotizacion(String numero, Instant fecha, EstadoCotizacion estado, CanalAprobacion canalAprobacion,
                      Instant fechaRespuesta, BigDecimal total, int vigenciaHoras, String motivoRechazo) {
        this.numero = numero;
        this.fecha = fecha == null ? Instant.now() : fecha;
        this.estado = estado == null ? EstadoCotizacion.EMITIDA : estado;
        this.canalAprobacion = canalAprobacion;
        this.fechaRespuesta = fechaRespuesta;
        this.total = Montos.normalizar(total);
        this.vigenciaHoras = vigenciaHoras <= 0 ? 48 : vigenciaHoras;
        this.motivoRechazo = motivoRechazo;
    }

    public static Cotizacion emitir(String numero, BigDecimal total, int vigenciaHoras) {
        return new Cotizacion(numero, Instant.now(), EstadoCotizacion.EMITIDA, null, null,
                total, vigenciaHoras, null);
    }

    public void aprobar(CanalAprobacion canal) {
        this.estado = EstadoCotizacion.APROBADA;
        this.canalAprobacion = canal;
        this.fechaRespuesta = Instant.now();
    }

    public void rechazar(String motivo) {
        this.estado = EstadoCotizacion.RECHAZADA;
        this.fechaRespuesta = Instant.now();
        this.motivoRechazo = motivo;
    }

    public boolean estaVencida(Instant ahora) {
        return estado == EstadoCotizacion.EMITIDA
                && fecha.plus(Duration.ofHours(vigenciaHoras)).isBefore(ahora);
    }

    public boolean estaAprobada() {
        return estado == EstadoCotizacion.APROBADA;
    }

    public String getNumero() {
        return numero;
    }

    public Instant getFecha() {
        return fecha;
    }

    public EstadoCotizacion getEstado() {
        return estado;
    }

    public CanalAprobacion getCanalAprobacion() {
        return canalAprobacion;
    }

    public Instant getFechaRespuesta() {
        return fechaRespuesta;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public int getVigenciaHoras() {
        return vigenciaHoras;
    }

    public String getMotivoRechazo() {
        return motivoRechazo;
    }
}
