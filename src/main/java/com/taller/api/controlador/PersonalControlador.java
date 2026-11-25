package com.taller.api.controlador;

import com.taller.aplicacion.servicio.PersonalService;
import com.taller.dominio.modelo.Mecanico;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Para qué sirve: Mecánicos: los asignables, los disponibles y la ficha de uno (/api/v1/mecanicos).
 * <p>PATRÓN Controller (MVC): recibe la petición HTTP, valida la entrada y delega en la capa de
 *     servicio; no contiene reglas de negocio.
 */
@RestController
@RequestMapping("/api/v1/mecanicos")
@Tag(name = "Personal")
public class PersonalControlador {

    private final PersonalService personal;

    public PersonalControlador(PersonalService personal) {
        this.personal = personal;
    }

    @GetMapping
    @Operation(summary = "Usuarios con rol mecánico que se pueden asignar, con su disponibilidad")
    public List<PersonalService.MecanicoAsignable> asignables() {
        return personal.asignables();
    }

    @GetMapping("/disponibles")
    @Operation(summary = "Mecanicos libres para asignar una orden aprobada")
    public List<Mecanico> disponibles() {
        return personal.disponibles();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Consulta un mecánico")
    public Mecanico obtener(@PathVariable String id) {
        return personal.obtener(id);
    }
}
