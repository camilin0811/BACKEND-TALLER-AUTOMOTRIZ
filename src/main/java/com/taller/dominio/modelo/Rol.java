package com.taller.dominio.modelo;

import java.util.Set;

/**
 * Roles del sistema y lo que cada uno puede hacer.
 *
 * <p>Los permisos viven en el dominio, no en la configuracion de Spring, para
 * que se puedan probar sin levantar el contexto y para que la matriz
 * documentada en la wiki tenga un unico lugar donde comprobarse.
 */
public enum Rol {

    CLIENTE(Set.of(
            Permiso.VER_ORDENES_PROPIAS,
            Permiso.APROBAR_COTIZACION,
            Permiso.VER_FACTURAS_PROPIAS)),

    MECANICO(Set.of(
            Permiso.VER_ORDENES_ASIGNADAS,
            Permiso.REGISTRAR_DIAGNOSTICO,
            Permiso.CONSUMIR_INVENTARIO,
            Permiso.CERRAR_ORDEN,
            Permiso.LEER_CATALOGO)),

    RECEPCIONISTA(Set.of(
            Permiso.VER_TODAS_LAS_ORDENES,
            Permiso.ESCRIBIR_ORDENES,
            Permiso.TRANSICIONAR_ORDEN,
            Permiso.REGISTRAR_DIAGNOSTICO,
            Permiso.CERRAR_ORDEN,
            Permiso.LEER_INVENTARIO,
            Permiso.RECIBIR_COMPRAS,
            Permiso.LEER_FACTURAS,
            Permiso.ESCRIBIR_FACTURAS,
            Permiso.LEER_CATALOGO)),

    ADMINISTRADOR(Set.of(
            Permiso.VER_TODAS_LAS_ORDENES,
            Permiso.ESCRIBIR_ORDENES,
            Permiso.TRANSICIONAR_ORDEN,
            Permiso.REGISTRAR_DIAGNOSTICO,
            Permiso.CERRAR_ORDEN,
            Permiso.LEER_INVENTARIO,
            Permiso.ESCRIBIR_INVENTARIO,
            Permiso.RECIBIR_COMPRAS,
            Permiso.CONSUMIR_INVENTARIO,
            Permiso.LEER_FACTURAS,
            Permiso.LEER_CATALOGO,
            Permiso.ESCRIBIR_CATALOGO,
            Permiso.VER_REPORTES));

    public enum Permiso {
        VER_ORDENES_PROPIAS,
        VER_ORDENES_ASIGNADAS,
        VER_TODAS_LAS_ORDENES,
        ESCRIBIR_ORDENES,
        TRANSICIONAR_ORDEN,
        APROBAR_COTIZACION,
        REGISTRAR_DIAGNOSTICO,
        CERRAR_ORDEN,
        LEER_INVENTARIO,
        /** Revisar, editar y enviar ordenes de compra al proveedor. */
        ESCRIBIR_INVENTARIO,
        /** Registrar la mercancia que llega de una orden de compra enviada. */
        RECIBIR_COMPRAS,
        CONSUMIR_INVENTARIO,
        LEER_FACTURAS,
        VER_FACTURAS_PROPIAS,
        ESCRIBIR_FACTURAS,
        LEER_CATALOGO,
        ESCRIBIR_CATALOGO,
        VER_REPORTES
    }

    private final Set<Permiso> permisos;

    Rol(Set<Permiso> permisos) {
        this.permisos = permisos;
    }

    public boolean puede(Permiso permiso) {
        return permisos.contains(permiso);
    }

    public Set<Permiso> permisos() {
        return permisos;
    }

    /** Spring Security espera los roles con el prefijo ROLE_. */
    public String autoridad() {
        return "ROLE_" + name();
    }
}
