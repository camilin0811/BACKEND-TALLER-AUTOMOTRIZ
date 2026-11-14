package com.taller.aplicacion.servicio;

import com.taller.dominio.excepcion.RecursoDuplicadoException;
import com.taller.dominio.excepcion.RecursoNoEncontradoException;
import com.taller.dominio.modelo.Vehiculo;
import com.taller.dominio.puerto.VehiculoRepositorio;
import org.springframework.stereotype.Service;

import java.util.List;

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
            throw new RecursoDuplicadoException("La placa %s ya esta registrada".formatted(vehiculo.getPlaca()));
        }
        clientes.obtener(vehiculo.getIdCliente());
        return vehiculos.guardar(vehiculo);
    }

    public Vehiculo obtenerPorPlaca(String placa) {
        return vehiculos.buscarPorPlaca(placa)
                .orElseThrow(() -> new RecursoNoEncontradoException("vehiculo con placa", placa));
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
