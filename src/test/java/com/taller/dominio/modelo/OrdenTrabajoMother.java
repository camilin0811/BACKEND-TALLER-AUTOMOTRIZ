package com.taller.dominio.modelo;

import java.math.BigDecimal;
import java.util.List;

/**
 * Construye ordenes en estados conocidos.
 *
 * <p>Evita repetir constructores de doce parametros en cada prueba: cuando la
 * entidad cambia, se ajusta este archivo y no los cincuenta tests.
 *
 * <p>Para qué sirve: Construye órdenes de trabajo en estados conocidos para las pruebas.
 * <p>PATRÓN Object Mother (pruebas): centraliza la creación de objetos de prueba: si la entidad
 *     cambia, se ajusta solo este archivo.
 */
public final class OrdenTrabajoMother {

    public static final String USUARIO = "recepcion01";

    private OrdenTrabajoMother() {
    }

    public static ReferenciaCliente cliente() {
        return new ReferenciaCliente("cli-1", "Andres Munoz", "+573001112233");
    }

    public static ReferenciaVehiculo automovil() {
        return new ReferenciaVehiculo("veh-1", "ABC123", TipoVehiculo.AUTOMOVIL, "Mazda 3");
    }

    public static ReferenciaVehiculo motocicleta() {
        return new ReferenciaVehiculo("veh-2", "XYZ12D", TipoVehiculo.MOTOCICLETA, "Yamaha FZ");
    }

    public static ReferenciaMecanico mecanico() {
        return new ReferenciaMecanico("mec-1", "Luis Rojas");
    }

    public static LineaServicio servicioFrenos() {
        return new LineaServicio("SRV-012", "Cambio de pastillas delanteras", new BigDecimal("120000"), 1.5);
    }

    public static LineaRepuesto pastillas() {
        return new LineaRepuesto("PAST-001", "Pastillas delanteras", 2, new BigDecimal("90000"));
    }

    public static OrdenTrabajo recibida() {
        OrdenTrabajo orden = OrdenTrabajo.recibir(cliente(), automovil(), 48500, "cita-1", USUARIO);
        orden.asignarId("ot-1");
        orden.asignarNumero("OT-0100");
        orden.limpiarEventos();
        return orden;
    }

    public static OrdenTrabajo enDiagnostico() {
        OrdenTrabajo orden = recibida();
        orden.registrarDiagnostico(Diagnostico.manual(List.of("P0128"), "Pastillas al limite"), "mecanico03");
        orden.limpiarEventos();
        return orden;
    }

    public static OrdenTrabajo cotizada() {
        OrdenTrabajo orden = enDiagnostico();
        orden.agregarServicio(servicioFrenos());
        orden.emitirCotizacion("COT-0100", 48, USUARIO);
        orden.limpiarEventos();
        return orden;
    }

    public static OrdenTrabajo aprobada() {
        OrdenTrabajo orden = cotizada();
        orden.aprobarCotizacion(CanalAprobacion.WHATSAPP, "sistema");
        orden.limpiarEventos();
        return orden;
    }

    public static OrdenTrabajo enReparacion() {
        OrdenTrabajo orden = aprobada();
        orden.asignarMecanico(mecanico(), USUARIO);
        orden.limpiarEventos();
        return orden;
    }

    public static OrdenTrabajo cerrada() {
        OrdenTrabajo orden = enReparacion();
        orden.registrarRepuesto(pastillas());
        orden.cerrar("mecanico03");
        orden.limpiarEventos();
        return orden;
    }

    public static OrdenTrabajo facturada() {
        OrdenTrabajo orden = cerrada();
        orden.marcarFacturada("fac-1", USUARIO);
        orden.limpiarEventos();
        return orden;
    }

    public static OrdenTrabajo entregada() {
        OrdenTrabajo orden = facturada();
        orden.entregar(USUARIO);
        orden.limpiarEventos();
        return orden;
    }
}
