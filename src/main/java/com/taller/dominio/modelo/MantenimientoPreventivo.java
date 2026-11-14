package com.taller.dominio.modelo;

import java.time.LocalDate;

public class MantenimientoPreventivo {

    private String id;
    private final String placa;
    private final String tipo;
    private final int kilometrajeObjetivo;
    private final LocalDate fechaSugerida;
    private boolean notificado;

    public MantenimientoPreventivo(String id, String placa, String tipo, int kilometrajeObjetivo,
                                   LocalDate fechaSugerida, boolean notificado) {
        this.id = id;
        this.placa = placa;
        this.tipo = tipo;
        this.kilometrajeObjetivo = kilometrajeObjetivo;
        this.fechaSugerida = fechaSugerida;
        this.notificado = notificado;
    }

    /**
     * Programa el siguiente mantenimiento segun el tipo de vehiculo.
     * Las motocicletas requieren intervalos mas cortos que los automoviles.
     */
    public static MantenimientoPreventivo programar(String placa, TipoVehiculo tipoVehiculo,
                                                    int kilometrajeActual,
                                                    int kilometrosIntervalo, int mesesIntervalo) {
        String descripcion = tipoVehiculo == TipoVehiculo.MOTOCICLETA
                ? "Mantenimiento periodico de motocicleta"
                : "Mantenimiento periodico de automovil";
        return new MantenimientoPreventivo(null, placa, descripcion,
                kilometrajeActual + kilometrosIntervalo,
                LocalDate.now().plusMonths(mesesIntervalo), false);
    }

    public boolean estaVencido(LocalDate hoy, int kilometrajeActual) {
        return !notificado
                && (kilometrajeActual >= kilometrajeObjetivo || !hoy.isBefore(fechaSugerida));
    }

    public void marcarNotificado() {
        this.notificado = true;
    }

    public String getId() {
        return id;
    }

    public void asignarId(String id) {
        this.id = id;
    }

    public String getPlaca() {
        return placa;
    }

    public String getTipo() {
        return tipo;
    }

    public int getKilometrajeObjetivo() {
        return kilometrajeObjetivo;
    }

    public LocalDate getFechaSugerida() {
        return fechaSugerida;
    }

    public boolean isNotificado() {
        return notificado;
    }
}
