package com.taller.dominio.modelo;

import com.taller.dominio.excepcion.ReglaNegocioException;
import java.time.LocalDateTime;

/**
 * Para qué sirve: Cita agendada de un cliente para su vehículo; pasa de pendiente a confirmada o
 * atendida.
 * <p>PATRÓN Entidad (DDD): tiene identidad propia (id) y su estado cambia a lo largo del tiempo.
 * <p>PATRÓN Método fábrica estático: solicitar() crea el objeto en un estado inicial válido y le da
 *     nombre a esa forma de crearlo.
 */
public class Cita {

    private String id;
    private String numero;
    private final String idCliente;
    private final String placa;
    private final LocalDateTime fechaHora;
    private final String motivo;
    private EstadoCita estado;
    private final String canal;

    public Cita(String id, String numero, String idCliente, String placa,
                LocalDateTime fechaHora, String motivo, EstadoCita estado, String canal) {
        this.id = id;
        this.numero = numero;
        this.idCliente = idCliente;
        this.placa = placa;
        this.fechaHora = fechaHora;
        this.motivo = motivo;
        this.estado = estado == null ? EstadoCita.PENDIENTE : estado;
        this.canal = canal;
    }

    public static Cita solicitar(String idCliente, String placa, LocalDateTime fechaHora,
                                 String motivo, String canal) {
        if (fechaHora == null || fechaHora.isBefore(LocalDateTime.now())) {
            throw new ReglaNegocioException("FECHA_CITA_INVALIDA",
                    "La fecha de la cita debe estar en el futuro");
        }
        return new Cita(null, null, idCliente, placa, fechaHora, motivo, EstadoCita.PENDIENTE, canal);
    }

    public void confirmar() {
        exigirEstado(EstadoCita.PENDIENTE, "confirmar");
        this.estado = EstadoCita.CONFIRMADA;
    }

    public void cancelar() {
        if (estado == EstadoCita.ATENDIDA) {
            throw new ReglaNegocioException("CITA_YA_ATENDIDA", "No se puede cancelar una cita ya atendida");
        }
        this.estado = EstadoCita.CANCELADA;
    }

    public void marcarAtendida() {
        if (estado == EstadoCita.CANCELADA) {
            throw new ReglaNegocioException("CITA_CANCELADA", "No se puede atender una cita cancelada");
        }
        this.estado = EstadoCita.ATENDIDA;
    }

    private void exigirEstado(EstadoCita esperado, String accion) {
        if (this.estado != esperado) {
            throw new ReglaNegocioException("ESTADO_CITA_INVALIDO",
                    "No se puede %s una cita en estado %s".formatted(accion, estado));
        }
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

    public String getIdCliente() {
        return idCliente;
    }

    public String getPlaca() {
        return placa;
    }

    public LocalDateTime getFechaHora() {
        return fechaHora;
    }

    public String getMotivo() {
        return motivo;
    }

    public EstadoCita getEstado() {
        return estado;
    }

    public String getCanal() {
        return canal;
    }
}
