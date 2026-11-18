package com.taller.dominio.modelo;

import com.taller.dominio.excepcion.ReglaNegocioException;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class OrdenCompra {

    private String id;
    private String numero;
    private final String idProveedor;
    private final Instant fecha;
    private EstadoOrdenCompra estado;
    private final List<ItemOrdenCompra> items = new ArrayList<>();
    /**
     * Quien la genero: un usuario, o null si la creo la reposicion
     * automatica. Las ordenes anteriores a este campo son automaticas.
     */
    private String generadaPor;

    public OrdenCompra(String id, String numero, String idProveedor, Instant fecha,
                       EstadoOrdenCompra estado, List<ItemOrdenCompra> items) {
        this.id = id;
        this.numero = numero;
        this.idProveedor = idProveedor;
        this.fecha = fecha == null ? Instant.now() : fecha;
        this.estado = estado == null ? EstadoOrdenCompra.BORRADOR : estado;
        if (items != null) {
            this.items.addAll(items);
        }
    }

    /**
     * La reposicion automatica crea la orden en BORRADOR, nunca ENVIADA:
     * un pedido a un proveedor siempre pasa por revision humana.
     */
    public static OrdenCompra borradorPorReposicion(String idProveedor, String codigoRepuesto,
                                                    int cantidad, BigDecimal precioUnitario) {
        OrdenCompra orden = new OrdenCompra(null, null, idProveedor, Instant.now(),
                EstadoOrdenCompra.BORRADOR, List.of());
        orden.items.add(new ItemOrdenCompra(codigoRepuesto, cantidad, 0, precioUnitario));
        return orden;
    }

    public void enviar() {
        if (estado != EstadoOrdenCompra.BORRADOR) {
            throw new ReglaNegocioException("ORDEN_COMPRA_NO_ENVIABLE",
                    "Solo se envía una orden de compra en borrador, y está en %s".formatted(
                            estado.name().toLowerCase().replace('_', ' ')));
        }
        if (items.isEmpty()) {
            throw new ReglaNegocioException("ORDEN_COMPRA_VACIA", "La orden de compra no tiene ítems");
        }
        this.estado = EstadoOrdenCompra.ENVIADA;
    }

    /** Mientras es borrador, quien la revisa puede corregir la cantidad sugerida. */
    public void ajustarCantidad(String codigoRepuesto, int cantidad) {
        if (estado != EstadoOrdenCompra.BORRADOR) {
            throw new ReglaNegocioException("ORDEN_COMPRA_NO_EDITABLE",
                    "Solo se edita una orden de compra en borrador; esta ya fue enviada");
        }
        item(codigoRepuesto).cambiarCantidad(cantidad);
    }

    public void registrarRecepcion(String codigoRepuesto, int cantidad) {
        // Solo llega lo que se pidio: una orden en borrador nunca salio al
        // proveedor y una ya recibida o anulada no espera nada.
        if (estado != EstadoOrdenCompra.ENVIADA && estado != EstadoOrdenCompra.RECIBIDA_PARCIAL) {
            throw new ReglaNegocioException("ORDEN_COMPRA_NO_RECIBIBLE",
                    "Solo se registra la recepción de una orden de compra enviada");
        }
        item(codigoRepuesto).recibir(cantidad);
        this.estado = items.stream().allMatch(ItemOrdenCompra::estaCompleto)
                ? EstadoOrdenCompra.RECIBIDA
                : EstadoOrdenCompra.RECIBIDA_PARCIAL;
    }

    /** True si la orden espera que se haga algo con ella (revisar o recibir). */
    public boolean estaAbierta() {
        return estado == EstadoOrdenCompra.BORRADOR || estado == EstadoOrdenCompra.ENVIADA
                || estado == EstadoOrdenCompra.RECIBIDA_PARCIAL;
    }

    public boolean incluye(String codigoRepuesto) {
        return items.stream().anyMatch(i -> i.getCodigoRepuesto().equals(codigoRepuesto));
    }

    private ItemOrdenCompra item(String codigoRepuesto) {
        return items.stream()
                .filter(i -> i.getCodigoRepuesto().equals(codigoRepuesto))
                .findFirst()
                .orElseThrow(() -> new ReglaNegocioException("ITEM_NO_EN_ORDEN",
                        "La orden de compra no incluye el repuesto %s".formatted(codigoRepuesto)));
    }

    public BigDecimal total() {
        BigDecimal suma = Montos.cero();
        for (ItemOrdenCompra item : items) {
            suma = suma.add(item.subtotal());
        }
        return Montos.normalizar(suma);
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

    public String getIdProveedor() {
        return idProveedor;
    }

    public Instant getFecha() {
        return fecha;
    }

    public EstadoOrdenCompra getEstado() {
        return estado;
    }

    public String getGeneradaPor() {
        return generadaPor;
    }

    public void registrarAutor(String usuario) {
        this.generadaPor = usuario;
    }

    public List<ItemOrdenCompra> getItems() {
        return Collections.unmodifiableList(items);
    }
}
