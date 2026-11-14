package com.taller.aplicacion.servicio;

import com.taller.dominio.evento.StockBajoDetectado;
import com.taller.dominio.excepcion.RecursoNoEncontradoException;
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

    public InventarioService(RepuestoRepositorio repuestos,
                             MovimientoInventarioRepositorio movimientos,
                             OrdenCompraRepositorio ordenesCompra,
                             PublicadorEventos publicador) {
        this.repuestos = repuestos;
        this.movimientos = movimientos;
        this.ordenesCompra = ordenesCompra;
        this.publicador = publicador;
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

    @Transactional
    public OrdenCompra registrarRecepcion(String idOrdenCompra, String codigoRepuesto, int cantidad) {
        OrdenCompra orden = ordenesCompra.buscarPorId(idOrdenCompra)
                .orElseThrow(() -> new RecursoNoEncontradoException("orden de compra", idOrdenCompra));
        orden.registrarRecepcion(codigoRepuesto, cantidad);
        reponer(codigoRepuesto, cantidad, "Recepcion de orden de compra %s".formatted(orden.getNumero()));
        return ordenesCompra.guardar(orden);
    }

    public List<OrdenCompra> listarOrdenesCompra() {
        return ordenesCompra.listar();
    }
}
