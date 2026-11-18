package com.taller.aplicacion.servicio;

import com.taller.dominio.evento.EventoDominio;
import com.taller.dominio.excepcion.RecursoNoEncontradoException;
import com.taller.dominio.excepcion.StockInsuficienteException;
import com.taller.dominio.modelo.*;
import com.taller.dominio.puerto.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Casos de uso de la orden de trabajo.
 *
 * <p>El servicio orquesta; las reglas viven en el agregado. Tras cada
 * operacion persiste y publica los eventos que la orden haya acumulado.
 */
@Service
public class OrdenTrabajoService {

    private final OrdenTrabajoRepositorio ordenes;
    private final VehiculoRepositorio vehiculos;
    private final ClienteRepositorio clientes;
    private final MecanicoRepositorio mecanicos;
    private final ServicioRepositorio catalogoServicios;
    private final RepuestoRepositorio repuestos;
    private final InventarioService inventario;
    private final OrdenCompraRepositorio ordenesCompra;
    private final GeneradorConsecutivos consecutivos;
    private final PublicadorEventos publicador;

    public OrdenTrabajoService(OrdenTrabajoRepositorio ordenes, VehiculoRepositorio vehiculos,
                               ClienteRepositorio clientes, MecanicoRepositorio mecanicos,
                               ServicioRepositorio catalogoServicios, RepuestoRepositorio repuestos,
                               InventarioService inventario, OrdenCompraRepositorio ordenesCompra,
                               GeneradorConsecutivos consecutivos, PublicadorEventos publicador) {
        this.ordenes = ordenes;
        this.vehiculos = vehiculos;
        this.clientes = clientes;
        this.mecanicos = mecanicos;
        this.catalogoServicios = catalogoServicios;
        this.repuestos = repuestos;
        this.inventario = inventario;
        this.ordenesCompra = ordenesCompra;
        this.consecutivos = consecutivos;
        this.publicador = publicador;
    }

    @Transactional
    public OrdenTrabajo crear(String placa, int kilometraje, String idCita, String usuario) {
        Vehiculo vehiculo = vehiculos.buscarPorPlaca(placa)
                .orElseThrow(() -> new RecursoNoEncontradoException("vehículo con placa", placa));
        Cliente cliente = clientes.buscarPorId(vehiculo.getIdCliente())
                .orElseThrow(() -> new RecursoNoEncontradoException("cliente", vehiculo.getIdCliente()));

        vehiculo.actualizarKilometraje(kilometraje);
        vehiculos.guardar(vehiculo);

        OrdenTrabajo orden = OrdenTrabajo.recibir(
                new ReferenciaCliente(cliente.getId(), cliente.getNombre(), cliente.getTelefono()),
                new ReferenciaVehiculo(vehiculo.getId(), vehiculo.getPlaca(), vehiculo.getTipo(),
                        vehiculo.descripcionCorta()),
                kilometraje, idCita, usuario);
        orden.asignarNumero(consecutivos.siguienteOrdenTrabajo());
        return persistirYPublicar(orden);
    }

    @Transactional
    public OrdenTrabajo registrarDiagnostico(String idOrden, Diagnostico diagnostico, String usuario) {
        OrdenTrabajo orden = obtener(idOrden);
        orden.registrarDiagnostico(diagnostico, usuario);
        return persistirYPublicar(orden);
    }

    @Transactional
    public OrdenTrabajo agregarServicio(String idOrden, String codigoServicio) {
        OrdenTrabajo orden = obtener(idOrden);
        Servicio servicio = catalogoServicios.buscarPorCodigo(codigoServicio)
                .orElseThrow(() -> new RecursoNoEncontradoException("servicio", codigoServicio));
        orden.agregarServicio(servicio.aLinea());
        return persistirYPublicar(orden);
    }

    @Transactional
    public OrdenTrabajo emitirCotizacion(String idOrden, int vigenciaHoras, String usuario) {
        OrdenTrabajo orden = obtener(idOrden);
        orden.emitirCotizacion(consecutivos.siguienteCotizacion(), vigenciaHoras, usuario);
        return persistirYPublicar(orden);
    }

    @Transactional
    public OrdenTrabajo aprobarCotizacion(String idOrden, CanalAprobacion canal, String usuario) {
        OrdenTrabajo orden = obtener(idOrden);
        orden.aprobarCotizacion(canal, usuario);
        return persistirYPublicar(orden);
    }

    @Transactional
    public OrdenTrabajo rechazarCotizacion(String idOrden, String motivo, String usuario) {
        OrdenTrabajo orden = obtener(idOrden);
        orden.rechazarCotizacion(motivo, usuario);
        return persistirYPublicar(orden);
    }

    @Transactional
    public OrdenTrabajo asignarMecanico(String idOrden, String idMecanico, String usuario) {
        OrdenTrabajo orden = obtener(idOrden);
        Mecanico mecanico = mecanicos.buscarPorId(idMecanico)
                .orElseThrow(() -> new RecursoNoEncontradoException("mecanico", idMecanico));
        orden.asignarMecanico(new ReferenciaMecanico(mecanico.getId(), mecanico.getNombre()), usuario);
        mecanico.ocupar();
        mecanicos.guardar(mecanico);
        return persistirYPublicar(orden);
    }

    /**
     * Registra el consumo de un repuesto.
     *
     * <p>Si no hay stock, la orden pasa a EN_ESPERA_REPUESTO y se genera una
     * orden de compra en borrador antes de propagar el error. El mecanico ve el
     * bloqueo en el momento, no al final del dia.
     */
    @Transactional
    public OrdenTrabajo registrarRepuesto(String idOrden, String codigoRepuesto, int cantidad, String usuario) {
        OrdenTrabajo orden = obtener(idOrden);
        Repuesto repuesto = repuestos.buscarPorCodigo(codigoRepuesto)
                .orElseThrow(() -> new RecursoNoEncontradoException("repuesto", codigoRepuesto));

        if (!repuesto.hayDisponibilidad(cantidad)) {
            orden.marcarEsperaRepuesto(usuario);
            persistirYPublicar(orden);
            generarReposicion(repuesto, cantidad);
            throw new StockInsuficienteException(codigoRepuesto, repuesto.getStock(), cantidad);
        }

        inventario.consumir(codigoRepuesto, cantidad, idOrden);
        orden.registrarRepuesto(repuesto.aLinea(cantidad));
        return persistirYPublicar(orden);
    }

    private void generarReposicion(Repuesto repuesto, int cantidadFaltante) {
        int cantidadPedido = Math.max(cantidadFaltante, repuesto.getStockMinimo() * 2);
        OrdenCompra compra = OrdenCompra.borradorPorReposicion(
                repuesto.proveedorHabitual(), repuesto.getCodigo(), cantidadPedido, repuesto.getPrecio());
        compra.asignarNumero(consecutivos.siguienteOrdenCompra());
        ordenesCompra.guardar(compra);
    }

    @Transactional
    public OrdenTrabajo reanudar(String idOrden, String usuario) {
        OrdenTrabajo orden = obtener(idOrden);
        orden.reanudarReparacion(usuario);
        return persistirYPublicar(orden);
    }

    @Transactional
    public OrdenTrabajo cerrar(String idOrden, String usuario) {
        OrdenTrabajo orden = obtener(idOrden);
        orden.cerrar(usuario);
        liberarMecanico(orden);
        return persistirYPublicar(orden);
    }

    @Transactional
    public OrdenTrabajo entregar(String idOrden, String usuario) {
        OrdenTrabajo orden = obtener(idOrden);
        orden.entregar(usuario);
        return persistirYPublicar(orden);
    }

    private void liberarMecanico(OrdenTrabajo orden) {
        if (orden.getMecanico() != null) {
            mecanicos.buscarPorId(orden.getMecanico().id()).ifPresent(mecanico -> {
                mecanico.liberar();
                mecanicos.guardar(mecanico);
            });
        }
    }

    public OrdenTrabajo obtener(String idOrden) {
        return ordenes.buscarPorId(idOrden)
                .orElseThrow(() -> new RecursoNoEncontradoException("orden de trabajo", idOrden));
    }

    public List<OrdenTrabajo> listarPorEstado(EstadoOrden estado) {
        return ordenes.listarPorEstado(estado);
    }

    public List<OrdenTrabajo> listarPorPlaca(String placa) {
        return ordenes.listarPorPlaca(placa);
    }

    public List<CambioEstado> historial(String idOrden) {
        return obtener(idOrden).getHistorialEstados();
    }

    /** Persiste y publica: el evento solo sale si el estado quedo guardado. */
    private OrdenTrabajo persistirYPublicar(OrdenTrabajo orden) {
        OrdenTrabajo guardada = ordenes.guardar(orden);
        List<EventoDominio> pendientes = List.copyOf(orden.eventosPendientes());
        orden.limpiarEventos();
        publicador.publicarTodos(pendientes);
        return guardada;
    }
}
