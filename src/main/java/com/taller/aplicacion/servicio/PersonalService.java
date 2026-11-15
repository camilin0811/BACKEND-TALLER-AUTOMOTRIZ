package com.taller.aplicacion.servicio;

import com.taller.dominio.excepcion.RecursoNoEncontradoException;
import com.taller.dominio.modelo.Mecanico;
import com.taller.dominio.puerto.MecanicoRepositorio;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PersonalService {

    private final MecanicoRepositorio mecanicos;

    public PersonalService(MecanicoRepositorio mecanicos) {
        this.mecanicos = mecanicos;
    }

    public List<Mecanico> disponibles() {
        return mecanicos.listarDisponibles();
    }

    public Mecanico obtener(String id) {
        return mecanicos.buscarPorId(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("mecanico", id));
    }

    public Mecanico registrar(Mecanico mecanico) {
        return mecanicos.guardar(mecanico);
    }
}
