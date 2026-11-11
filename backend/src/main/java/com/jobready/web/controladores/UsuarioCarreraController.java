package com.jobready.web.controladores;

import com.jobready.web.entidades.UsuarioCarrera;
import com.jobready.web.dtos.UsuarioCarreraDTO;
import com.jobready.web.servicios.UsuarioCarreraServicio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;
import java.util.List;

/** Carreras elegidas por el candidato conectado; el usuario se toma del token JWT. */
@RestController
@RequestMapping("/api")
@PreAuthorize("hasRole('CANDIDATO')")
public class UsuarioCarreraController {
    @Autowired
    private UsuarioCarreraServicio usuarioCarreraServicio;

    @PostMapping("/usuario-carrera")
    public UsuarioCarrera insertar(@RequestBody UsuarioCarreraDTO usuarioCarreraDTO, Authentication authentication) {
        return usuarioCarreraServicio.insertar(usuarioCarreraDTO.getIdCarrera(), authentication.getName());
    }

    @GetMapping("/usuarios-carreras")
    public List<UsuarioCarrera> listar(Authentication authentication) {
        return usuarioCarreraServicio.listarPorUsuario(authentication.getName());
    }

    @DeleteMapping("/usuario-carrera/{idCarrera}")
    public void eliminar(@PathVariable Integer idCarrera, Authentication authentication) {
        usuarioCarreraServicio.eliminar(idCarrera, authentication.getName());
    }

    @DeleteMapping("/usuario-carrera/{idUsuario}/{idCarrera}")
    public void eliminar(@PathVariable Integer idUsuario, @PathVariable Integer idCarrera, Authentication authentication) {
        usuarioCarreraServicio.eliminar(idUsuario, idCarrera, authentication.getName());
    }
}
