package com.jobready.web.servicios;

import com.jobready.web.entidades.Carrera;
import com.jobready.web.entidades.SesionProgramada;
import com.jobready.web.entidades.Usuario;
import com.jobready.web.repositorios.CarreraRepositorio;
import com.jobready.web.repositorios.SesionProgramadaRepositorio;
import com.jobready.web.repositorios.UsuarioRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Sesiones de práctica que organiza el candidato. El dueño siempre es el usuario del token:
 * se ignora cualquier idUsuario enviado por el cliente.
 */
@Service
public class SesionProgramadaServicio implements Serializable {
    @Autowired
    private SesionProgramadaRepositorio sesionProgramadaRepositorio;
    @Autowired
    private UsuarioRepositorio usuarioRepositorio;
    @Autowired
    private CarreraRepositorio carreraRepositorio;

    public SesionProgramada insertar(SesionProgramada datos, String username) {
        Usuario usuario = usuarioRepositorio.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuario no encontrado"));
        SesionProgramada sesion = new SesionProgramada();
        sesion.setUsuario(usuario);
        aplicarDatos(sesion, datos);
        sesion.setEstado("programada");
        sesion.setRecordatorioEnviado(false);
        return sesionProgramadaRepositorio.save(sesion);
    }

    public List<SesionProgramada> listarPorUsuario(String username) {
        return sesionProgramadaRepositorio
                .findByUsuario_UsernameOrderByFechaProgramadaAscHoraProgramadaAsc(username);
    }

    public SesionProgramada actualizar(Integer id, SesionProgramada datos, String username) {
        SesionProgramada sesion = buscarPropia(id, username);
        if ("cancelada".equalsIgnoreCase(sesion.getEstado())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "La sesion ya fue cancelada");
        }
        aplicarDatos(sesion, datos);
        return sesionProgramadaRepositorio.save(sesion);
    }

    public SesionProgramada cancelar(Integer id, String username) {
        SesionProgramada sesion = buscarPropia(id, username);
        sesion.setEstado("cancelada");
        return sesionProgramadaRepositorio.save(sesion);
    }

    private void aplicarDatos(SesionProgramada sesion, SesionProgramada datos) {
        if (datos.getFechaProgramada() == null || datos.getHoraProgramada() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Indica la fecha y la hora de la sesion");
        }
        if (LocalDateTime.of(datos.getFechaProgramada(), datos.getHoraProgramada()).isBefore(LocalDateTime.now())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La sesion debe programarse en una fecha futura");
        }
        Carrera carrera = null;
        if (datos.getCarrera() != null && datos.getCarrera().getIdCarrera() != null) {
            carrera = carreraRepositorio.findById(datos.getCarrera().getIdCarrera())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "La carrera seleccionada no existe"));
        }
        sesion.setCarrera(carrera);
        sesion.setFechaProgramada(datos.getFechaProgramada());
        sesion.setHoraProgramada(datos.getHoraProgramada());
    }

    private SesionProgramada buscarPropia(Integer id, String username) {
        SesionProgramada sesion = sesionProgramadaRepositorio.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No existe la sesion con id " + id));
        if (!sesion.getUsuario().getUsername().equals(username)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "La sesion pertenece a otro usuario");
        }
        return sesion;
    }
}
