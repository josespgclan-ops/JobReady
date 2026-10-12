package com.jobready.web.controladores;

import com.jobready.web.entidades.UsuarioCarrera;
import com.jobready.web.dtos.UsuarioCarreraDTO;
import com.jobready.web.servicios.UsuarioCarreraServicio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;
import java.util.List;

@RestController
@RequestMapping("/api")
@PreAuthorize("hasRole('CANDIDATO')")
public class UsuarioCarreraController {
    @Autowired
    private UsuarioCarreraServicio usuarioCarreraServicio;

    @PostMapping("/usuario-carrera")
    public UsuarioCarrera insertar(@RequestBody UsuarioCarreraDTO usuarioCarreraDTO) {
        return usuarioCarreraServicio.insertar(usuarioCarreraDTO.convertirAEntidad());
    }

    @GetMapping("/usuarios-carreras")
    public List<UsuarioCarrera> listar() {
        return usuarioCarreraServicio.listar();
    }

    @DeleteMapping("/usuario-carrera/{idUsuario}/{idCarrera}")
    public void eliminar(@PathVariable Integer idUsuario, @PathVariable Integer idCarrera) {
        usuarioCarreraServicio.eliminar(idUsuario, idCarrera);
    }
}
