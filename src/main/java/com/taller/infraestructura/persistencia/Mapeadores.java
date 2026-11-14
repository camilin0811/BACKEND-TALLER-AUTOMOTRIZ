package com.taller.infraestructura.persistencia;

import com.taller.dominio.modelo.*;
import com.taller.infraestructura.persistencia.documento.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Traduccion entre documentos de MongoDB y objetos de dominio.
 *
 * <p>Existe para que el dominio no lleve una sola anotacion de Spring Data.
 * Es el precio de la regla de dependencias y se paga aqui, en un solo lugar.
 */
public final class Mapeadores {

    private Mapeadores() {
    }

    // ------------------------------------------------------------- cliente

    public static Cliente aCliente(ClienteDoc d) {
        return d == null ? null
                : new Cliente(d.id, d.nombre, d.documento, d.telefono, d.email, d.direccion, d.fechaRegistro);
    }

    public static ClienteDoc aDoc(Cliente c) {
        ClienteDoc d = new ClienteDoc();
        d.id = c.getId();
        d.nombre = c.getNombre();
        d.documento = c.getDocumento();
        d.telefono = c.getTelefono();
        d.email = c.getEmail();
        d.direccion = c.getDireccion();
        d.fechaRegistro = c.getFechaRegistro();
        return d;
    }

    // ------------------------------------------------------------ vehiculo

    public static Vehiculo aVehiculo(VehiculoDoc d) {
        if (d == null) {
            return null;
        }
        if (TipoVehiculo.MOTOCICLETA.name().equals(d.tipo)) {
            return new Motocicleta(d.id, d.placa, d.marca, d.linea, d.anio, d.kilometraje, d.vin,
                    d.cilindrada, d.idCliente, d.tipoMotor,
                    d.numeroTiempos == null ? 4 : d.numeroTiempos,
                    d.categoria, Boolean.TRUE.equals(d.llevaParrilla));
        }
        return new Automovil(d.id, d.placa, d.marca, d.linea, d.anio, d.kilometraje, d.vin,
                d.cilindrada, d.idCliente,
                d.numeroPuertas == null ? 0 : d.numeroPuertas, d.tipoCombustible,
                d.numeroPasajeros == null ? 0 : d.numeroPasajeros, d.tipoTransmision);
    }

    public static VehiculoDoc aDoc(Vehiculo v) {
        VehiculoDoc d = new VehiculoDoc();
        d.id = v.getId();
        d.tipo = v.getTipo().name();
        d.placa = v.getPlaca();
        d.marca = v.getMarca();
        d.linea = v.getLinea();
        d.anio = v.getAnio();
        d.kilometraje = v.getKilometraje();
        d.vin = v.getVin();
        d.cilindrada = v.getCilindrada();
        d.idCliente = v.getIdCliente();
        if (v instanceof Automovil a) {
            d.numeroPuertas = a.getNumeroPuertas();
            d.tipoCombustible = a.getTipoCombustible();
            d.numeroPasajeros = a.getNumeroPasajeros();
            d.tipoTransmision = a.getTipoTransmision();
        } else if (v instanceof Motocicleta m) {
            d.tipoMotor = m.getTipoMotor();
            d.numeroTiempos = m.getNumeroTiempos();
            d.categoria = m.getCategoria();
            d.llevaParrilla = m.isLlevaParrilla();
        }
        return d;
    }

    // ---------------------------------------------------------------- cita

    public static Cita aCita(CitaDoc d) {
        return d == null ? null : new Cita(d.id, d.numero, d.idCliente, d.placa, d.fechaHora, d.motivo,
                d.estado == null ? EstadoCita.PENDIENTE : EstadoCita.valueOf(d.estado), d.canal);
    }

    public static CitaDoc aDoc(Cita c) {
        CitaDoc d = new CitaDoc();
        d.id = c.getId();
        d.numero = c.getNumero();
        d.idCliente = c.getIdCliente();
        d.placa = c.getPlaca();
        d.fechaHora = c.getFechaHora();
        d.motivo = c.getMotivo();
        d.estado = c.getEstado().name();
        d.canal = c.getCanal();
        return d;
    }

    // -------------------------------------------------------------- orden

    public static OrdenTrabajo aOrden(OrdenTrabajoDoc d) {
        if (d == null) {
            return null;
        }
        OrdenTrabajo orden = new OrdenTrabajo(d.id, d.numero,
                d.estado == null ? EstadoOrden.RECIBIDA : EstadoOrden.valueOf(d.estado),
                d.fechaIngreso, d.fechaEntrega, d.kilometrajeIngreso,
                aReferenciaCliente(d.cliente), aReferenciaVehiculo(d.vehiculo),
                aReferenciaMecanico(d.mecanico), d.idCita, d.total, d.idFactura, d.version);

        List<Diagnostico> diags = new ArrayList<>();
        for (OrdenTrabajoDoc.DiagnosticoDoc dd : d.diagnosticos) {
            diags.add(new Diagnostico(dd.fecha,
                    dd.origen == null ? OrigenDiagnostico.MANUAL : OrigenDiagnostico.valueOf(dd.origen),
                    dd.codigosFalla, dd.lecturasSensores, dd.observaciones));
        }
        List<LineaServicio> servicios = new ArrayList<>();
        for (OrdenTrabajoDoc.LineaServicioDoc sd : d.servicios) {
            servicios.add(new LineaServicio(sd.codigo, sd.descripcion, sd.precio, sd.horasEstimadas));
        }
        List<LineaRepuesto> repuestos = new ArrayList<>();
        for (OrdenTrabajoDoc.LineaRepuestoDoc rd : d.repuestos) {
            repuestos.add(new LineaRepuesto(rd.codigo, rd.nombre, rd.cantidad, rd.precioUnitario));
        }
        List<CambioEstado> historial = new ArrayList<>();
        for (OrdenTrabajoDoc.CambioEstadoDoc cd : d.historialEstados) {
            historial.add(new CambioEstado(EstadoOrden.valueOf(cd.estado), cd.fecha, cd.usuario));
        }
        orden.rehidratar(diags, aCotizacion(d.cotizacion), servicios, repuestos, historial);
        return orden;
    }

    public static OrdenTrabajoDoc aDoc(OrdenTrabajo o) {
        OrdenTrabajoDoc d = new OrdenTrabajoDoc();
        d.id = o.getId();
        d.numero = o.getNumero();
        d.estado = o.getEstado().name();
        d.fechaIngreso = o.getFechaIngreso();
        d.fechaEntrega = o.getFechaEntrega();
        d.kilometrajeIngreso = o.getKilometrajeIngreso();
        d.cliente = aRefDoc(o.getCliente());
        d.vehiculo = aRefVehiculoDoc(o.getVehiculo());
        d.mecanico = aRefDoc(o.getMecanico());
        d.idCita = o.getIdCita();
        d.total = o.getTotal();
        d.idFactura = o.getIdFactura();
        d.version = o.getVersion();

        for (Diagnostico diag : o.getDiagnosticos()) {
            OrdenTrabajoDoc.DiagnosticoDoc dd = new OrdenTrabajoDoc.DiagnosticoDoc();
            dd.fecha = diag.fecha();
            dd.origen = diag.origen().name();
            dd.codigosFalla = new ArrayList<>(diag.codigosFalla());
            dd.lecturasSensores = diag.lecturasSensores();
            dd.observaciones = diag.observaciones();
            d.diagnosticos.add(dd);
        }
        for (LineaServicio s : o.getServicios()) {
            OrdenTrabajoDoc.LineaServicioDoc sd = new OrdenTrabajoDoc.LineaServicioDoc();
            sd.codigo = s.codigo();
            sd.descripcion = s.descripcion();
            sd.precio = s.precio();
            sd.horasEstimadas = s.horasEstimadas();
            d.servicios.add(sd);
        }
        for (LineaRepuesto r : o.getRepuestos()) {
            OrdenTrabajoDoc.LineaRepuestoDoc rd = new OrdenTrabajoDoc.LineaRepuestoDoc();
            rd.codigo = r.codigo();
            rd.nombre = r.nombre();
            rd.cantidad = r.cantidad();
            rd.precioUnitario = r.precioUnitario();
            d.repuestos.add(rd);
        }
        for (CambioEstado c : o.getHistorialEstados()) {
            OrdenTrabajoDoc.CambioEstadoDoc cd = new OrdenTrabajoDoc.CambioEstadoDoc();
            cd.estado = c.estado().name();
            cd.fecha = c.fecha();
            cd.usuario = c.usuario();
            d.historialEstados.add(cd);
        }
        d.cotizacion = aCotizacionDoc(o.getCotizacion());
        return d;
    }

    private static ReferenciaCliente aReferenciaCliente(OrdenTrabajoDoc.Referencia r) {
        return r == null ? null : new ReferenciaCliente(r.id, r.nombre, r.telefono);
    }

    private static ReferenciaMecanico aReferenciaMecanico(OrdenTrabajoDoc.Referencia r) {
        return r == null ? null : new ReferenciaMecanico(r.id, r.nombre);
    }

    private static ReferenciaVehiculo aReferenciaVehiculo(OrdenTrabajoDoc.ReferenciaVehiculoDoc r) {
        return r == null ? null
                : new ReferenciaVehiculo(r.id, r.placa, r.tipo == null ? null : TipoVehiculo.valueOf(r.tipo),
                        r.descripcion);
    }

    private static OrdenTrabajoDoc.Referencia aRefDoc(ReferenciaCliente r) {
        if (r == null) {
            return null;
        }
        OrdenTrabajoDoc.Referencia d = new OrdenTrabajoDoc.Referencia();
        d.id = r.id();
        d.nombre = r.nombre();
        d.telefono = r.telefono();
        return d;
    }

    private static OrdenTrabajoDoc.Referencia aRefDoc(ReferenciaMecanico r) {
        if (r == null) {
            return null;
        }
        OrdenTrabajoDoc.Referencia d = new OrdenTrabajoDoc.Referencia();
        d.id = r.id();
        d.nombre = r.nombre();
        return d;
    }

    private static OrdenTrabajoDoc.ReferenciaVehiculoDoc aRefVehiculoDoc(ReferenciaVehiculo r) {
        if (r == null) {
            return null;
        }
        OrdenTrabajoDoc.ReferenciaVehiculoDoc d = new OrdenTrabajoDoc.ReferenciaVehiculoDoc();
        d.id = r.id();
        d.placa = r.placa();
        d.tipo = r.tipo() == null ? null : r.tipo().name();
        d.descripcion = r.descripcion();
        return d;
    }

    private static Cotizacion aCotizacion(OrdenTrabajoDoc.CotizacionDoc c) {
        return c == null ? null : new Cotizacion(c.numero, c.fecha,
                c.estado == null ? null : EstadoCotizacion.valueOf(c.estado),
                c.canalAprobacion == null ? null : CanalAprobacion.valueOf(c.canalAprobacion),
                c.fechaRespuesta, c.total, c.vigenciaHoras, c.motivoRechazo);
    }

    private static OrdenTrabajoDoc.CotizacionDoc aCotizacionDoc(Cotizacion c) {
        if (c == null) {
            return null;
        }
        OrdenTrabajoDoc.CotizacionDoc d = new OrdenTrabajoDoc.CotizacionDoc();
        d.numero = c.getNumero();
        d.fecha = c.getFecha();
        d.estado = c.getEstado().name();
        d.canalAprobacion = c.getCanalAprobacion() == null ? null : c.getCanalAprobacion().name();
        d.fechaRespuesta = c.getFechaRespuesta();
        d.total = c.getTotal();
        d.vigenciaHoras = c.getVigenciaHoras();
        d.motivoRechazo = c.getMotivoRechazo();
        return d;
    }

    // ----------------------------------------------------------- repuesto

    public static Repuesto aRepuesto(RepuestoDoc d) {
        return d == null ? null
                : new Repuesto(d.id, d.codigo, d.nombre, d.precio, d.stock, d.stockMinimo, d.idsProveedores);
    }

    public static RepuestoDoc aDoc(Repuesto r) {
        RepuestoDoc d = new RepuestoDoc();
        d.id = r.getId();
        d.codigo = r.getCodigo();
        d.nombre = r.getNombre();
        d.precio = r.getPrecio();
        d.stock = r.getStock();
        d.stockMinimo = r.getStockMinimo();
        d.idsProveedores = new ArrayList<>(r.getIdsProveedores());
        return d;
    }

    public static MovimientoInventario aMovimiento(MovimientoInventarioDoc d) {
        return d == null ? null : new MovimientoInventario(d.id, d.codigoRepuesto,
                TipoMovimiento.valueOf(d.tipo), d.cantidad, d.motivo, d.idOrden, d.stockResultante, d.fecha);
    }

    public static MovimientoInventarioDoc aDoc(MovimientoInventario m) {
        MovimientoInventarioDoc d = new MovimientoInventarioDoc();
        d.id = m.getId();
        d.codigoRepuesto = m.getCodigoRepuesto();
        d.tipo = m.getTipo().name();
        d.cantidad = m.getCantidad();
        d.motivo = m.getMotivo();
        d.idOrden = m.getIdOrden();
        d.stockResultante = m.getStockResultante();
        d.fecha = m.getFecha();
        return d;
    }

    // ------------------------------------------------------------ factura

    public static Factura aFactura(FacturaDoc d) {
        if (d == null) {
            return null;
        }
        List<Pago> pagos = new ArrayList<>();
        for (FacturaDoc.PagoDoc p : d.pagos) {
            pagos.add(new Pago(p.monto, MetodoPago.valueOf(p.metodo), p.referencia, p.fecha));
        }
        return new Factura(d.id, d.numero, d.idOrden, d.fecha, aReferenciaCliente(d.cliente),
                d.subtotal, d.iva, d.total, d.cufe,
                d.estadoDian == null ? null : EstadoDian.valueOf(d.estadoDian),
                d.fechaValidacion, d.motivoRechazo, d.intentosEnvio, pagos);
    }

    public static FacturaDoc aDoc(Factura f) {
        FacturaDoc d = new FacturaDoc();
        d.id = f.getId();
        d.numero = f.getNumero();
        d.idOrden = f.getIdOrden();
        d.fecha = f.getFecha();
        d.cliente = aRefDoc(f.getCliente());
        d.subtotal = f.getSubtotal();
        d.iva = f.getIva();
        d.total = f.getTotal();
        d.cufe = f.getCufe();
        d.estadoDian = f.getEstadoDian().name();
        d.fechaValidacion = f.getFechaValidacion();
        d.motivoRechazo = f.getMotivoRechazo();
        d.intentosEnvio = f.getIntentosEnvio();
        for (Pago p : f.getPagos()) {
            FacturaDoc.PagoDoc pd = new FacturaDoc.PagoDoc();
            pd.monto = p.monto();
            pd.metodo = p.metodo().name();
            pd.referencia = p.referencia();
            pd.fecha = p.fecha();
            d.pagos.add(pd);
        }
        return d;
    }

    // ------------------------------------------------------- orden compra

    public static OrdenCompra aOrdenCompra(OrdenCompraDoc d) {
        if (d == null) {
            return null;
        }
        List<ItemOrdenCompra> items = new ArrayList<>();
        for (OrdenCompraDoc.ItemDoc i : d.items) {
            items.add(new ItemOrdenCompra(i.codigoRepuesto, i.cantidad, i.cantidadRecibida, i.precioUnitario));
        }
        return new OrdenCompra(d.id, d.numero, d.idProveedor, d.fecha,
                d.estado == null ? null : EstadoOrdenCompra.valueOf(d.estado), items);
    }

    public static OrdenCompraDoc aDoc(OrdenCompra o) {
        OrdenCompraDoc d = new OrdenCompraDoc();
        d.id = o.getId();
        d.numero = o.getNumero();
        d.idProveedor = o.getIdProveedor();
        d.fecha = o.getFecha();
        d.estado = o.getEstado().name();
        for (ItemOrdenCompra i : o.getItems()) {
            OrdenCompraDoc.ItemDoc id = new OrdenCompraDoc.ItemDoc();
            id.codigoRepuesto = i.getCodigoRepuesto();
            id.cantidad = i.getCantidad();
            id.cantidadRecibida = i.getCantidadRecibida();
            id.precioUnitario = i.getPrecioUnitario();
            d.items.add(id);
        }
        return d;
    }

    // ------------------------------------------- mantenimiento y catalogo

    public static MantenimientoPreventivo aMantenimiento(MantenimientoDoc d) {
        return d == null ? null : new MantenimientoPreventivo(d.id, d.placa, d.tipo,
                d.kilometrajeObjetivo, d.fechaSugerida, d.notificado);
    }

    public static MantenimientoDoc aDoc(MantenimientoPreventivo m) {
        MantenimientoDoc d = new MantenimientoDoc();
        d.id = m.getId();
        d.placa = m.getPlaca();
        d.tipo = m.getTipo();
        d.kilometrajeObjetivo = m.getKilometrajeObjetivo();
        d.fechaSugerida = m.getFechaSugerida();
        d.notificado = m.isNotificado();
        return d;
    }

    public static Servicio aServicio(ServicioDoc d) {
        return d == null ? null
                : new Servicio(d.id, d.codigo, d.descripcion, d.precio, d.horasEstimadas, d.activo);
    }

    public static ServicioDoc aDoc(Servicio s) {
        ServicioDoc d = new ServicioDoc();
        d.id = s.getId();
        d.codigo = s.getCodigo();
        d.descripcion = s.getDescripcion();
        d.precio = s.getPrecio();
        d.horasEstimadas = s.getHorasEstimadas();
        d.activo = s.isActivo();
        return d;
    }

    public static Mecanico aMecanico(EmpleadoDoc d) {
        return d == null ? null : new Mecanico(d.id, d.nombre, d.documento, d.telefono, d.email,
                d.codigo, d.salario, d.fechaIngreso, d.especialidad, Boolean.TRUE.equals(d.disponible));
    }

    public static EmpleadoDoc aDoc(Mecanico m) {
        EmpleadoDoc d = new EmpleadoDoc();
        d.id = m.getId();
        d.cargo = "MECANICO";
        d.documento = m.getDocumento();
        d.codigo = m.getCodigo();
        d.nombre = m.getNombre();
        d.telefono = m.getTelefono();
        d.email = m.getEmail();
        d.salario = m.getSalario();
        d.fechaIngreso = m.getFechaIngreso();
        d.especialidad = m.getEspecialidad();
        d.disponible = m.isDisponible();
        return d;
    }
}
