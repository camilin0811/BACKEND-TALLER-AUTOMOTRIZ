package com.taller.dominio.modelo;

import com.taller.dominio.excepcion.ReglaNegocioException;
import java.util.Objects;

/**
 * Base de los vehiculos atendidos por el taller.
 *
 * <p>La cilindrada vive aqui porque aplica tanto a automoviles como a
 * motocicletas. Las subclases solo anaden lo que de verdad las distingue.
 *
 * <p>Para qué sirve: Base de los vehículos: placa, marca, línea, año, kilometraje y cilindrada.
 * <p>PATRÓN Herencia y polimorfismo: clase abstracta con el método abstracto getTipo(): Automovil y
 *     Motocicleta la especializan.
 */
public abstract class Vehiculo {

    private String id;
    private final String placa;
    private final String marca;
    private final String linea;
    private final int anio;
    private int kilometraje;
    private final String vin;
    private final Integer cilindrada;
    private String idCliente;

    protected Vehiculo(String id, String placa, String marca, String linea, int anio,
                       int kilometraje, String vin, Integer cilindrada, String idCliente) {
        this.id = id;
        this.placa = normalizarPlaca(placa);
        this.marca = marca;
        this.linea = linea;
        this.anio = anio;
        this.kilometraje = exigirKilometrajeNoNegativo(kilometraje);
        this.vin = vin;
        this.cilindrada = cilindrada;
        this.idCliente = idCliente;
    }

    private static String normalizarPlaca(String placa) {
        if (placa == null || placa.isBlank()) {
            throw new IllegalArgumentException("La placa es obligatoria");
        }
        return placa.trim().toUpperCase().replace("-", "");
    }

    private static int exigirKilometrajeNoNegativo(int kilometraje) {
        if (kilometraje < 0) {
            throw new IllegalArgumentException("El kilometraje no puede ser negativo");
        }
        return kilometraje;
    }

    public abstract TipoVehiculo getTipo();

    /**
     * Invariante del dominio: el kilometraje nunca decrece. Un odometro que
     * retrocede es un error de digitacion o un fraude, nunca un dato valido.
     */
    public void actualizarKilometraje(int nuevoKilometraje) {
        if (nuevoKilometraje < this.kilometraje) {
            throw new ReglaNegocioException("KILOMETRAJE_INVALIDO",
                    "El kilometraje no puede ser menor al último registrado (%d)".formatted(this.kilometraje));
        }
        this.kilometraje = nuevoKilometraje;
    }

    public String descripcionCorta() {
        return "%s %s".formatted(marca == null ? "" : marca, linea == null ? "" : linea).trim();
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

    public String getMarca() {
        return marca;
    }

    public String getLinea() {
        return linea;
    }

    public int getAnio() {
        return anio;
    }

    public int getKilometraje() {
        return kilometraje;
    }

    public String getVin() {
        return vin;
    }

    public Integer getCilindrada() {
        return cilindrada;
    }

    public String getIdCliente() {
        return idCliente;
    }

    public void asignarCliente(String idCliente) {
        this.idCliente = idCliente;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Vehiculo otro)) {
            return false;
        }
        return Objects.equals(placa, otro.placa);
    }

    @Override
    public int hashCode() {
        return Objects.hash(placa);
    }
}
