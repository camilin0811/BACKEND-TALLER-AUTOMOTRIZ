package com.taller.aplicacion.manejador;

import com.taller.aplicacion.servicio.FacturacionService;
import com.taller.dominio.evento.EventoDominio;
import com.taller.dominio.evento.OrdenCerrada;
import com.taller.dominio.evento.VehiculoEntregado;
import com.taller.dominio.modelo.MantenimientoPreventivo;
import com.taller.dominio.modelo.TipoVehiculo;
import com.taller.dominio.puerto.MantenimientoRepositorio;
import com.taller.dominio.puerto.VehiculoRepositorio;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Set;

/** M4: facturacion al cierre y programacion del mantenimiento preventivo. */
@Component
public class ManejadorFacturacion extends ManejadorEventos {

    private static final Logger log = LoggerFactory.getLogger(ManejadorFacturacion.class);

    private static final Set<String> SOPORTADOS = Set.of("OrdenCerrada", "VehiculoEntregado");

    private final FacturacionService facturacion;
    private final MantenimientoRepositorio mantenimientos;
    private final VehiculoRepositorio vehiculos;
    private final int kmAutomovil;
    private final int mesesAutomovil;
    private final int kmMotocicleta;
    private final int mesesMotocicleta;

    public ManejadorFacturacion(FacturacionService facturacion,
                                MantenimientoRepositorio mantenimientos,
                                VehiculoRepositorio vehiculos,
                                @Value("${taller.mantenimiento.automovil.kilometros:5000}") int kmAutomovil,
                                @Value("${taller.mantenimiento.automovil.meses:6}") int mesesAutomovil,
                                @Value("${taller.mantenimiento.motocicleta.kilometros:3000}") int kmMotocicleta,
                                @Value("${taller.mantenimiento.motocicleta.meses:4}") int mesesMotocicleta) {
        this.facturacion = facturacion;
        this.mantenimientos = mantenimientos;
        this.vehiculos = vehiculos;
        this.kmAutomovil = kmAutomovil;
        this.mesesAutomovil = mesesAutomovil;
        this.kmMotocicleta = kmMotocicleta;
        this.mesesMotocicleta = mesesMotocicleta;
    }

    @Override
    public boolean soporta(String tipoEvento) {
        return SOPORTADOS.contains(tipoEvento);
    }

    @Override
    public void manejar(EventoDominio evento) {
        if (evento instanceof OrdenCerrada e) {
            facturacion.emitirPorCierre(e.getIdOrden());
        } else if (evento instanceof VehiculoEntregado e) {
            programarMantenimiento(e);
        }
    }

    private void programarMantenimiento(VehiculoEntregado e) {
        boolean esMoto = e.getTipoVehiculo() == TipoVehiculo.MOTOCICLETA;
        int kilometraje = vehiculos.buscarPorPlaca(e.getPlaca())
                .map(v -> v.getKilometraje())
                .orElse(e.getKilometraje());
        MantenimientoPreventivo mantenimiento = MantenimientoPreventivo.programar(
                e.getPlaca(), e.getTipoVehiculo(), kilometraje,
                esMoto ? kmMotocicleta : kmAutomovil,
                esMoto ? mesesMotocicleta : mesesAutomovil);
        mantenimientos.guardar(mantenimiento);
        log.info("Mantenimiento preventivo programado para {} a los {} km",
                e.getPlaca(), mantenimiento.getKilometrajeObjetivo());
    }
}
