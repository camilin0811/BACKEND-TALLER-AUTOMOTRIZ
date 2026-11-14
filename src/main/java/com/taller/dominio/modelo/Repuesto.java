package com.taller.dominio.modelo;

import com.taller.dominio.excepcion.StockInsuficienteException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Item de almacen.
 *
 * <p>El stock se guarda como valor calculado aunque sea derivable de los
 * movimientos: la pregunta "hay stock?" ocurre en cada consumo y recorrer
 * todos los movimientos cada vez no escala.
 */
public class Repuesto {

    private String id;
    private final String codigo;
    private String nombre;
    private BigDecimal precio;
    private int stock;
    private int stockMinimo;
    private final List<String> idsProveedores = new ArrayList<>();

    public Repuesto(String id, String codigo, String nombre, BigDecimal precio,
                    int stock, int stockMinimo, List<String> idsProveedores) {
        this.id = id;
        this.codigo = codigo;
        this.nombre = nombre;
        this.precio = Montos.normalizar(precio);
        this.stock = stock;
        this.stockMinimo = stockMinimo;
        if (idsProveedores != null) {
            this.idsProveedores.addAll(idsProveedores);
        }
    }

    public static Repuesto nuevo(String codigo, String nombre, BigDecimal precio, int stock, int stockMinimo) {
        return new Repuesto(null, codigo, nombre, precio, stock, stockMinimo, List.of());
    }

    /**
     * Invariante: el stock nunca queda negativo. Si el consumo no cabe, la
     * operacion se rechaza entera y el llamador decide que hacer con la orden.
     */
    public void descontar(int cantidad) {
        exigirCantidadPositiva(cantidad);
        if (cantidad > stock) {
            throw new StockInsuficienteException(codigo, stock, cantidad);
        }
        this.stock -= cantidad;
    }

    public void reponer(int cantidad) {
        exigirCantidadPositiva(cantidad);
        this.stock += cantidad;
    }

    public void ajustarA(int nuevoStock) {
        if (nuevoStock < 0) {
            throw new IllegalArgumentException("El stock no puede ser negativo");
        }
        this.stock = nuevoStock;
    }

    public boolean requiereReposicion() {
        return stock <= stockMinimo;
    }

    public boolean hayDisponibilidad(int cantidad) {
        return cantidad > 0 && cantidad <= stock;
    }

    public LineaRepuesto aLinea(int cantidad) {
        return new LineaRepuesto(codigo, nombre, cantidad, precio);
    }

    private static void exigirCantidadPositiva(int cantidad) {
        if (cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor que cero");
        }
    }

    public void agregarProveedor(String idProveedor) {
        if (idProveedor != null && !idsProveedores.contains(idProveedor)) {
            idsProveedores.add(idProveedor);
        }
    }

    public String getId() {
        return id;
    }

    public void asignarId(String id) {
        this.id = id;
    }

    public String getCodigo() {
        return codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public BigDecimal getPrecio() {
        return precio;
    }

    public int getStock() {
        return stock;
    }

    public int getStockMinimo() {
        return stockMinimo;
    }

    public void cambiarStockMinimo(int stockMinimo) {
        this.stockMinimo = stockMinimo;
    }

    public List<String> getIdsProveedores() {
        return Collections.unmodifiableList(idsProveedores);
    }

    public String proveedorHabitual() {
        return idsProveedores.isEmpty() ? null : idsProveedores.get(0);
    }
}
