package com.jobready.web.security.services;

import com.jobready.web.entidades.Rol;
import com.jobready.web.entidades.Usuario;
import com.jobready.web.repositorios.RolRepositorio;
import com.jobready.web.repositorios.UsuarioRepositorio;
import com.jobready.web.security.dtos.RegistroCandidatoDTO;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
public class RegistroCandidatoServicio {
    private final UsuarioRepositorio usuarioRepositorio;
    private final RolRepositorio rolRepositorio;
    private final PasswordEncoder passwordEncoder;

    public RegistroCandidatoServicio(UsuarioRepositorio usuarioRepositorio,
                                     RolRepositorio rolRepositorio,
                                     PasswordEncoder passwordEncoder) {
        this.usuarioRepositorio = usuarioRepositorio;
        this.rolRepositorio = rolRepositorio;
        this.passwordEncoder = passwordEncoder;
    }

    public Usuario registrar(RegistroCandidatoDTO registroDTO) {
        if (usuarioRepositorio.existsByUsername(registroDTO.getUsername())) {
            throw new RuntimeException("El username ya se encuentra registrado");
        }
        if (usuarioRepositorio.existsByEmail(registroDTO.getEmail())) {
            throw new RuntimeException("El email ya se encuentra registrado");
        }

        Rol rolCandidato = rolRepositorio.findByNombreIgnoreCase("CANDIDATO")
                .orElseThrow(() -> new RuntimeException("No existe el rol CANDIDATO"));

        Usuario usuario = new Usuario();
        usuario.setRol(rolCandidato);
        usuario.setNombreCompleto(registroDTO.getNombreCompleto());
        usuario.setEmail(registroDTO.getEmail());
        usuario.setUsername(registroDTO.getUsername());
        usuario.setPasswordHash(passwordEncoder.encode(registroDTO.getPassword()));
        usuario.setTelefono(registroDTO.getTelefono());
        usuario.setNivelActual(BigDecimal.ZERO);
        usuario.setFechaRegistro(LocalDateTime.now());
        usuario.setEstado("activo");
        return usuarioRepositorio.save(usuario);
    }
}
