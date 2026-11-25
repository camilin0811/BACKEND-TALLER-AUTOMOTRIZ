package com.taller.dominio.modelo;

/**
 * Para qué sirve: Proveedor de repuestos: NIT, nombre y contacto.
 * <p>PATRÓN Entidad (DDD): tiene identidad propia (id) y su estado cambia a lo largo del tiempo.
 */
public class Proveedor {

    private String id;
    private final String nit;
    private final String razonSocial;
    private String telefono;
    private String email;

    public Proveedor(String id, String nit, String razonSocial, String telefono, String email) {
        this.id = id;
        this.nit = nit;
        this.razonSocial = razonSocial;
        this.telefono = telefono;
        this.email = email;
    }

    public String getId() {
        return id;
    }

    public void asignarId(String id) {
        this.id = id;
    }

    public String getNit() {
        return nit;
    }

    public String getRazonSocial() {
        return razonSocial;
    }

    public String getTelefono() {
        return telefono;
    }

    public String getEmail() {
        return email;
    }
}
