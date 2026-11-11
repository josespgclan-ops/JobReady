package com.jobready.web.servicios;

import com.jobready.web.entidades.Carrera;
import com.jobready.web.entidades.Usuario;
import com.jobready.web.entidades.UsuarioCarrera;
import com.jobready.web.entidades.UsuarioCarreraId;
import com.jobready.web.repositorios.CarreraRepositorio;
import com.jobready.web.repositorios.UsuarioCarreraRepositorio;
import com.jobready.web.repositorios.UsuarioRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/** Carreras que el candidato conectado eligió para practicar. */
@Service
public class UsuarioCarreraServicio implements Serializable {
    @Autowired
    private UsuarioCarreraRepositorio usuarioCarreraRepositorio;
    @Autowired
    private UsuarioRepositorio usuarioRepositorio;
    @Autowired
    private CarreraRepositorio carreraRepositorio;

    /** Asocia la carrera al usuario del token. Si ya estaba elegida, devuelve la asociación existente. */
    public UsuarioCarrera insertar(Integer idCarrera, String username) {
        Usuario usuario = buscarUsuario(username);
        if (idCarrera == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Indica la carrera");
        }
        Carrera carrera = carreraRepositorio.findById(idCarrera)
                .filter(c -> !Boolean.FALSE.equals(c.getActivo()))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "La carrera seleccionada no existe"));
        UsuarioCarreraId id = new UsuarioCarreraId(usuario.getIdUsuario(), carrera.getIdCarrera());
        return usuarioCarreraRepositorio.findById(id).orElseGet(() -> {
            UsuarioCarrera relacion = new UsuarioCarrera();
            relacion.setId(id);
            relacion.setUsuario(usuario);
            relacion.setCarrera(carrera);
            relacion.setFechaSeleccion(LocalDateTime.now());
            return usuarioCarreraRepositorio.save(relacion);
        });
    }

    public List<UsuarioCarrera> listarPorUsuario(String username) {
        return usuarioCarreraRepositorio.findByUsuario_UsernameOrderByFechaSeleccionDesc(username);
    }

    /** Quita una carrera del usuario del token. */
    public void eliminar(Integer idCarrera, String username) {
        Usuario usuario = buscarUsuario(username);
        UsuarioCarreraId id = new UsuarioCarreraId(usuario.getIdUsuario(), idCarrera);
        if (!usuarioCarreraRepositorio.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No tienes esa carrera seleccionada");
        }
        usuarioCarreraRepositorio.deleteById(id);
    }

    /** Ruta antigua con idUsuario en la URL: solo se permite sobre la propia cuenta. */
    public void eliminar(Integer idUsuario, Integer idCarrera, String username) {
        if (!buscarUsuario(username).getIdUsuario().equals(idUsuario)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No puedes modificar las carreras de otro usuario");
        }
        eliminar(idCarrera, username);
    }

    private Usuario buscarUsuario(String username) {
        return usuarioRepositorio.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuario no encontrado"));
    }
}
