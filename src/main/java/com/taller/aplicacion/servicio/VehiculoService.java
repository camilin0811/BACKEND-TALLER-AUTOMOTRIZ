package com.taller.aplicacion.servicio;

import com.taller.dominio.excepcion.RecursoDuplicadoException;
import com.taller.dominio.excepcion.RecursoNoEncontradoException;
import com.taller.dominio.modelo.Vehiculo;
import com.taller.dominio.puerto.VehiculoRepositorio;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Para qué sirve: Registra vehículos, los consulta por placa y actualiza el kilometraje.
 * <p>PATRÓN Service Layer (capa de servicio, fachada de casos de uso): orquesta el caso de uso: carga
 *     del repositorio, delega las reglas en el dominio y guarda. Los controladores solo hablan con esta
 *     fachada.
 * <p>PATRÓN Inyección de dependencias / Inversión de control: recibe los puertos (interfaces del
 *     dominio) por constructor; no conoce MongoDB ni las integraciones concretas.
 */
@Service
public class VehiculoService {

    private final VehiculoRepositorio vehiculos;
    private final ClienteService clientes;

    public VehiculoService(VehiculoRepositorio vehiculos, ClienteService clientes) {
        this.vehiculos = vehiculos;
        this.clientes = clientes;
    }

    public Vehiculo registrar(Vehiculo vehiculo) {
        if (vehiculos.existePlaca(vehiculo.getPlaca())) {
            throw new RecursoDuplicadoException("La placa %s ya está registrada".formatted(vehiculo.getPlaca()));
        }
        clientes.obtener(vehiculo.getIdCliente());
        return vehiculos.guardar(vehiculo);
    }

    public Vehiculo obtenerPorPlaca(String placa) {
        return vehiculos.buscarPorPlaca(placa)
                .orElseThrow(() -> new RecursoNoEncontradoException("vehículo con placa", placa));
    }

    public List<Vehiculo> listarPorCliente(String idCliente) {
        return vehiculos.listarPorCliente(idCliente);
    }

    public Vehiculo actualizarKilometraje(String placa, int kilometraje) {
        Vehiculo vehiculo = obtenerPorPlaca(placa);
        vehiculo.actualizarKilometraje(kilometraje);
        return vehiculos.guardar(vehiculo);
    }
}
