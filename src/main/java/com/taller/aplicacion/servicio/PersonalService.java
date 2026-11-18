package com.taller.aplicacion.servicio;

import com.taller.dominio.excepcion.RecursoNoEncontradoException;
import com.taller.dominio.modelo.Mecanico;
import com.taller.dominio.puerto.MecanicoRepositorio;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PersonalService {

    /**
     * Un mecanico que se puede elegir en una orden: el usuario con rol
     * mecanico y su ficha de empleado, que es lo que la orden guarda.
     */
    public record MecanicoAsignable(String idMecanico, String nombre, String usuario,
                                    String especialidad, boolean disponible) {
    }


    private final MecanicoRepositorio mecanicos;
    private final com.taller.dominio.puerto.UsuarioRepositorio usuarios;

    public PersonalService(MecanicoRepositorio mecanicos,
                           com.taller.dominio.puerto.UsuarioRepositorio usuarios) {
        this.mecanicos = mecanicos;
        this.usuarios = usuarios;
    }

    /**
     * Los usuarios activos con rol mecanico que tienen ficha de empleado. Un
     * usuario sin ficha no aparece: la orden no tendria a quien referenciar.
     */
    public List<MecanicoAsignable> asignables() {
        var fichas = mecanicos.listar().stream()
                .collect(java.util.stream.Collectors.toMap(Mecanico::getId, m -> m));
        return usuarios.listar().stream()
                .filter(u -> u.getRol() == com.taller.dominio.modelo.Rol.MECANICO && u.isActivo())
                .filter(u -> u.getIdPersona() != null && fichas.containsKey(u.getIdPersona()))
                .map(u -> {
                    Mecanico m = fichas.get(u.getIdPersona());
                    return new MecanicoAsignable(m.getId(), m.getNombre(), u.getUsuario(),
                            m.getEspecialidad(), m.isDisponible());
                })
                .sorted(java.util.Comparator.comparing(MecanicoAsignable::nombre))
                .toList();
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
