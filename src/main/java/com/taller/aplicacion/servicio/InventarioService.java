package com.taller.aplicacion.servicio;

import com.taller.dominio.evento.StockBajoDetectado;
import com.taller.dominio.excepcion.RecursoNoEncontradoException;
import com.taller.dominio.puerto.GeneradorConsecutivos;
import com.taller.dominio.modelo.MovimientoInventario;
import com.taller.dominio.modelo.OrdenCompra;
import com.taller.dominio.modelo.Repuesto;
import com.taller.dominio.puerto.MovimientoInventarioRepositorio;
import com.taller.dominio.puerto.OrdenCompraRepositorio;
import com.taller.dominio.puerto.PublicadorEventos;
import com.taller.dominio.puerto.RepuestoRepositorio;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class InventarioService {

    private final RepuestoRepositorio repuestos;
    private final MovimientoInventarioRepositorio movimientos;
    private final OrdenCompraRepositorio ordenesCompra;
    private final PublicadorEventos publicador;
    private final GeneradorConsecutivos consecutivos;

    public InventarioService(RepuestoRepositorio repuestos,
                             MovimientoInventarioRepositorio movimientos,
                             OrdenCompraRepositorio ordenesCompra,
                             PublicadorEventos publicador,
                             GeneradorConsecutivos consecutivos) {
        this.repuestos = repuestos;
        this.movimientos = movimientos;
        this.ordenesCompra = ordenesCompra;
        this.publicador = publicador;
        this.consecutivos = consecutivos;
    }

    public Repuesto obtener(String codigo) {
        return repuestos.buscarPorCodigo(codigo)
                .orElseThrow(() -> new RecursoNoEncontradoException("repuesto", codigo));
    }

    public List<Repuesto> listar() {
        return repuestos.listar();
    }

    public List<Repuesto> listarBajoMinimo() {
        return repuestos.listarBajoMinimo();
    }

    public List<MovimientoInventario> movimientosDe(String codigo) {
        obtener(codigo);
        return movimientos.listarPorRepuesto(codigo);
    }

    public Repuesto crear(Repuesto repuesto) {
        return repuestos.guardar(repuesto);
    }

    /**
     * Descuenta stock y deja el movimiento. Las dos escrituras van juntas: un
     * stock sin su movimiento es una diferencia que nadie puede auditar.
     */
    @Transactional
    public Repuesto consumir(String codigo, int cantidad, String idOrden) {
        Repuesto repuesto = obtener(codigo);
        repuesto.descontar(cantidad);
        repuestos.guardar(repuesto);
        movimientos.guardar(MovimientoInventario.salida(codigo, cantidad, idOrden, repuesto.getStock()));
        if (repuesto.requiereReposicion()) {
            publicador.publicar(new StockBajoDetectado(codigo, repuesto.getStock(), repuesto.getStockMinimo()));
        }
        return repuesto;
    }

    @Transactional
    public Repuesto reponer(String codigo, int cantidad, String motivo) {
        Repuesto repuesto = obtener(codigo);
        repuesto.reponer(cantidad);
        repuestos.guardar(repuesto);
        movimientos.guardar(MovimientoInventario.entrada(codigo, cantidad, motivo, repuesto.getStock()));
        return repuesto;
    }

    /**
     * Reposicion automatica: crea una orden de compra en borrador.
     *
     * <p>Es el unico lugar que la genera, tanto si el stock baja del minimo
     * tras un consumo como si un consumo falla por falta de stock. Si ya hay
     * una orden abierta (borrador, enviada o recibida en parte) para ese
     * repuesto no crea otra: cada consumo bajo el minimo no debe sumar un
     * pedido nuevo al mismo proveedor.
     *
     * @return la orden creada, o vacio si ya habia una abierta
     */
    @Transactional
    public java.util.Optional<OrdenCompra> generarReposicion(String codigoRepuesto) {
        Repuesto repuesto = obtener(codigoRepuesto);
        boolean yaPedido = ordenesCompra.listar().stream()
                .anyMatch(oc -> oc.estaAbierta() && oc.incluye(codigoRepuesto));
        if (yaPedido) {
            return java.util.Optional.empty();
        }
        OrdenCompra compra = OrdenCompra.borradorPorReposicion(repuesto.proveedorHabitual(),
                repuesto.getCodigo(), repuesto.cantidadReposicion(), repuesto.getPrecio());
        compra.asignarNumero(consecutivos.siguienteOrdenCompra());
        return java.util.Optional.of(ordenesCompra.guardar(compra));
    }

    @Transactional
    public OrdenCompra ajustarCantidad(String idOrdenCompra, String codigoRepuesto, int cantidad) {
        OrdenCompra orden = obtenerOrdenCompra(idOrdenCompra);
        orden.ajustarCantidad(codigoRepuesto, cantidad);
        return ordenesCompra.guardar(orden);
    }

    @Transactional
    public OrdenCompra enviar(String idOrdenCompra) {
        OrdenCompra orden = obtenerOrdenCompra(idOrdenCompra);
        orden.enviar();
        return ordenesCompra.guardar(orden);
    }

    @Transactional
    public OrdenCompra registrarRecepcion(String idOrdenCompra, String codigoRepuesto, int cantidad) {
        OrdenCompra orden = obtenerOrdenCompra(idOrdenCompra);
        // El dominio valida primero (estado, repuesto, pendiente): si rechaza,
        // el stock no se toca.
        orden.registrarRecepcion(codigoRepuesto, cantidad);
        reponer(codigoRepuesto, cantidad, "Recepción de orden de compra %s".formatted(orden.getNumero()));
        return ordenesCompra.guardar(orden);
    }

    private OrdenCompra obtenerOrdenCompra(String id) {
        return ordenesCompra.buscarPorId(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("orden de compra", id));
    }

    public List<OrdenCompra> listarOrdenesCompra() {
        return ordenesCompra.listar();
    }
}
