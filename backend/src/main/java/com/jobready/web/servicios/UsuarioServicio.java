package com.jobready.web.servicios;

import com.jobready.web.dtos.UsuarioActualDTO;
import com.jobready.web.entidades.Usuario;
import com.jobready.web.repositorios.UsuarioRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import java.io.Serializable;
import java.util.List;

@Service
public class UsuarioServicio implements Serializable {
    @Autowired
    private UsuarioRepositorio usuarioRepositorio;
    @Autowired
    private PasswordEncoder passwordEncoder;

    public Usuario insertar(Usuario usuario) {
        usuario.setPasswordHash(passwordEncoder.encode(usuario.getPasswordHash()));
        return usuarioRepositorio.save(usuario);
    }

    public List<Usuario> listar() {
        return usuarioRepositorio.findAll();
    }

    /** Datos públicos de la cuenta que inició sesión (para saludarla en la barra superior). */
    public UsuarioActualDTO obtenerActual(String username) {
        Usuario usuario = usuarioRepositorio.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));
        return new UsuarioActualDTO(usuario.getUsername(), usuario.getNombreCompleto());
    }

    public Usuario actualizar(Integer id, Usuario datos) {
        Usuario usuario = usuarioRepositorio.findById(id)
                .orElseThrow(() -> new RuntimeException("No existe el usuario con id " + id));
        usuario.setRol(datos.getRol());
        usuario.setNombreCompleto(datos.getNombreCompleto());
        usuario.setEmail(datos.getEmail());
        usuario.setUsername(datos.getUsername());
        if (datos.getPasswordHash() != null && !datos.getPasswordHash().isBlank()) {
            usuario.setPasswordHash(passwordEncoder.encode(datos.getPasswordHash()));
        }
        usuario.setTelefono(datos.getTelefono());
        usuario.setNivelActual(datos.getNivelActual());
        usuario.setEstado(datos.getEstado());
        return usuarioRepositorio.save(usuario);
    }

    public Usuario desactivar(Integer id, String usernameAdmin) {
        Usuario usuario = usuarioRepositorio.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No existe el usuario con id " + id));
        // Un administrador no puede suspender su propia cuenta y quedarse sin acceso.
        if (usuario.getUsername().equals(usernameAdmin)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No puedes suspender tu propia cuenta");
        }
        usuario.setEstado("inactivo");
        return usuarioRepositorio.save(usuario);
    }

    public Usuario activar(Integer id) {
        Usuario usuario = usuarioRepositorio.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No existe el usuario con id " + id));
        usuario.setEstado("activo");
        return usuarioRepositorio.save(usuario);
    }
}


