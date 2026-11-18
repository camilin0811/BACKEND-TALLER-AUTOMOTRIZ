package com.taller.aplicacion.servicio;

import com.taller.dominio.excepcion.RecursoNoEncontradoException;
import com.taller.dominio.excepcion.ReglaNegocioException;
import com.taller.dominio.modelo.Cita;
import com.taller.dominio.puerto.CitaRepositorio;
import com.taller.dominio.puerto.GeneradorConsecutivos;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class CitaService {

    private final CitaRepositorio citas;
    private final GeneradorConsecutivos consecutivos;
    private final int cuposPorFranja;
    private final int minutosPorFranja;

    public CitaService(CitaRepositorio citas,
                       GeneradorConsecutivos consecutivos,
                       @Value("${taller.agenda.cupos-por-franja:2}") int cuposPorFranja,
                       @Value("${taller.agenda.minutos-por-franja:60}") int minutosPorFranja) {
        this.citas = citas;
        this.consecutivos = consecutivos;
        this.cuposPorFranja = cuposPorFranja;
        this.minutosPorFranja = minutosPorFranja;
    }

    public Cita agendar(String idCliente, String placa, LocalDateTime fechaHora, String motivo, String canal) {
        if (!hayDisponibilidad(fechaHora)) {
            throw new ReglaNegocioException("SIN_CUPO",
                    "No hay cupo el %s. Horarios disponibles más próximos: %s"
                            .formatted(fechaHora, alternativas(fechaHora)));
        }
        Cita cita = Cita.solicitar(idCliente, placa, fechaHora, motivo, canal);
        cita.asignarNumero(consecutivos.siguienteCita());
        return citas.guardar(cita);
    }

    public boolean hayDisponibilidad(LocalDateTime fechaHora) {
        LocalDateTime fin = fechaHora.plus(Duration.ofMinutes(minutosPorFranja));
        return citas.contarEnFranja(fechaHora, fin) < cuposPorFranja;
    }

    /** Tres horarios libres a partir del solicitado, para no dejar al cliente sin opciones. */
    public List<LocalDateTime> alternativas(LocalDateTime desde) {
        List<LocalDateTime> libres = new ArrayList<>();
        LocalDateTime candidato = desde;
        for (int i = 0; i < 24 && libres.size() < 3; i++) {
            candidato = candidato.plus(Duration.ofMinutes(minutosPorFranja));
            if (hayDisponibilidad(candidato)) {
                libres.add(candidato);
            }
        }
        return libres;
    }

    public List<Cita> listarPorFecha(LocalDate fecha) {
        return citas.listarPorFecha(fecha);
    }

    public Cita obtener(String id) {
        return citas.buscarPorId(id).orElseThrow(() -> new RecursoNoEncontradoException("cita", id));
    }

    public Cita confirmar(String id) {
        Cita cita = obtener(id);
        cita.confirmar();
        return citas.guardar(cita);
    }

    public Cita cancelar(String id) {
        Cita cita = obtener(id);
        cita.cancelar();
        return citas.guardar(cita);
    }
}
