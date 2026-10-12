package com.jobready.web.servicios;

import com.jobready.web.entidades.Usuario;
import com.jobready.web.repositorios.UsuarioRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;
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

    public Usuario desactivar(Integer id) {
        Usuario usuario = usuarioRepositorio.findById(id)
                .orElseThrow(() -> new RuntimeException("No existe el usuario con id " + id));
        usuario.setEstado("inactivo");
        return usuarioRepositorio.save(usuario);
    }
}


