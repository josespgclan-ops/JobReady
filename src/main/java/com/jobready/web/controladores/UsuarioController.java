package com.jobready.web.controladores;

import com.jobready.web.entidades.Usuario;
import com.jobready.web.dtos.UsuarioDTO;
import com.jobready.web.servicios.UsuarioServicio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;
import java.util.List;

@RestController
@RequestMapping("/api")
@PreAuthorize("hasRole('ADMIN')")
public class UsuarioController {
    @Autowired
    private UsuarioServicio usuarioServicio;

    @PostMapping("/usuario") //ENDPOINT
    public Usuario insertar(@RequestBody UsuarioDTO usuarioDTO) {
        return usuarioServicio.insertar(usuarioDTO.convertirAEntidad());
    }

    @GetMapping("/usuarios")
    public List<Usuario> listar() {
        return usuarioServicio.listar();
    }

    @PutMapping("/usuario/{id}")
    public Usuario actualizar(@PathVariable Integer id, @RequestBody UsuarioDTO usuarioDTO) {
        return usuarioServicio.actualizar(id, usuarioDTO.convertirAEntidad());
    }

    @PatchMapping("/usuario/{id}/desactivar")
    public Usuario desactivar(@PathVariable Integer id) {
        return usuarioServicio.desactivar(id);
    }
}
