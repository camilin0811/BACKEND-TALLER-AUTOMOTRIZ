package com.taller.api.mapeador;

import com.taller.api.dto.CrearVehiculoDTO;
import com.taller.api.dto.OrdenDetalleDTO;
import com.taller.api.dto.OrdenResumenDTO;
import com.taller.dominio.modelo.*;

import java.util.ArrayList;
import java.util.List;

/** Traduccion entre el dominio y los DTO expuestos por la API. */
public final class MapeadorApi {

    private MapeadorApi() {
    }

    public static Vehiculo aVehiculo(CrearVehiculoDTO d) {
        if (TipoVehiculo.MOTOCICLETA.name().equals(d.tipo())) {
            return new Motocicleta(null, d.placa(), d.marca(), d.linea(), d.anio(), d.kilometraje(),
                    d.vin(), d.cilindrada(), d.idCliente(), d.tipoMotor(),
                    d.numeroTiempos() == null ? 4 : d.numeroTiempos(),
                    d.categoria(), Boolean.TRUE.equals(d.llevaParrilla()));
        }
        return new Automovil(null, d.placa(), d.marca(), d.linea(), d.anio(), d.kilometraje(),
                d.vin(), d.cilindrada(), d.idCliente(),
                d.numeroPuertas() == null ? 0 : d.numeroPuertas(), d.tipoCombustible(),
                d.numeroPasajeros() == null ? 0 : d.numeroPasajeros(), d.tipoTransmision());
    }

    public static OrdenResumenDTO aResumen(OrdenTrabajo o) {
        return new OrdenResumenDTO(o.getId(), o.getNumero(), o.getEstado().name(),
                o.getVehiculo() == null ? null : o.getVehiculo().placa(),
                o.getVehiculo() == null || o.getVehiculo().tipo() == null ? null : o.getVehiculo().tipo().name(),
                o.getVehiculo() == null ? null : o.getVehiculo().descripcion(),
                o.getCliente() == null ? null : o.getCliente().nombre(),
                o.getMecanico() == null ? null : o.getMecanico().nombre(),
                o.getTotal(), o.getFechaIngreso());
    }

    public static OrdenDetalleDTO aDetalle(OrdenTrabajo o) {
        List<String> codigos = new ArrayList<>();
        o.getDiagnosticos().forEach(d -> codigos.addAll(d.codigosFalla()));

        List<OrdenDetalleDTO.ItemDTO> servicios = o.getServicios().stream()
                .map(s -> new OrdenDetalleDTO.ItemDTO(s.codigo(), s.descripcion(), 1, s.precio()))
                .toList();
        List<OrdenDetalleDTO.ItemDTO> repuestos = o.getRepuestos().stream()
                .map(r -> new OrdenDetalleDTO.ItemDTO(r.codigo(), r.nombre(), r.cantidad(), r.subtotal()))
                .toList();
        List<OrdenDetalleDTO.HitoDTO> historial = o.getHistorialEstados().stream()
                .map(h -> new OrdenDetalleDTO.HitoDTO(h.estado().name(), h.fecha(), h.usuario()))
                .toList();

        return new OrdenDetalleDTO(o.getId(), o.getNumero(), o.getEstado().name(),
                o.getFechaIngreso(), o.getFechaEntrega(), o.getKilometrajeIngreso(),
                o.getVehiculo() == null ? null : o.getVehiculo().placa(),
                o.getVehiculo() == null ? null : o.getVehiculo().descripcion(),
                o.getCliente() == null ? null : o.getCliente().nombre(),
                o.getMecanico() == null ? null : o.getMecanico().nombre(),
                codigos,
                o.getCotizacion() == null ? null : o.getCotizacion().getNumero(),
                o.getCotizacion() == null ? null : o.getCotizacion().getEstado().name(),
                servicios, repuestos, historial, o.getTotal(), o.getIdFactura());
    }
}
