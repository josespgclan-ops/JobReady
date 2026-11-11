package com.jobready.web.servicios;

import com.jobready.web.entidades.EncuestaSatisfaccion;
import com.jobready.web.entidades.Usuario;
import com.jobready.web.repositorios.EncuestaSatisfaccionRepositorio;
import com.jobready.web.repositorios.UsuarioRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Service
public class EncuestaSatisfaccionServicio implements Serializable {
    private static final Set<String> CALIFICACIONES = Set.of("Excelente", "Bueno", "Regular", "Malo");

    @Autowired
    private EncuestaSatisfaccionRepositorio encuestaSatisfaccionRepositorio;
    @Autowired
    private UsuarioRepositorio usuarioRepositorio;

    /** Registra la encuesta del candidato conectado; el usuario y la fecha los asigna el servidor. */
    public EncuestaSatisfaccion insertar(EncuestaSatisfaccion encuesta, String username) {
        Integer satisfaccion = encuesta.getSatisfaccionGeneral();
        if (satisfaccion == null || satisfaccion < 1 || satisfaccion > 5) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La satisfaccion general debe estar entre 1 y 5");
        }
        if (!CALIFICACIONES.contains(encuesta.getCalificacionServicio())
                || !CALIFICACIONES.contains(encuesta.getCalidadProducto())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Califica el servicio y la calidad de la retroalimentacion");
        }
        String comentario = encuesta.getComentarioAdicional();
        if (comentario != null && comentario.length() > 2000) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El comentario no puede superar los 2000 caracteres");
        }
        Usuario usuario = usuarioRepositorio.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuario no encontrado"));
        encuesta.setUsuario(usuario);
        encuesta.setNombreContacto(usuario.getNombreCompleto());
        encuesta.setCorreoContacto(usuario.getEmail());
        encuesta.setComentarioAdicional(comentario == null || comentario.isBlank() ? null : comentario.trim());
        encuesta.setFechaEnvio(LocalDateTime.now());
        return encuestaSatisfaccionRepositorio.save(encuesta);
    }

    public List<EncuestaSatisfaccion> listar() {
        return encuestaSatisfaccionRepositorio.findAll();
    }

    public List<EncuestaSatisfaccion> listarPorUsuario(String username) {
        return encuestaSatisfaccionRepositorio.findByUsuario_UsernameOrderByFechaEnvioDesc(username);
    }
}
