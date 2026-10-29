package com.jobready.web.controladores;

import com.jobready.web.entidades.Rol;
import com.jobready.web.dtos.RolDTO;
import com.jobready.web.servicios.RolServicio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;
import java.util.List;

@RestController
@RequestMapping("/api")
@PreAuthorize("hasRole('ADMIN')")
public class RolController {
    @Autowired
    private RolServicio rolServicio;

    @PostMapping("/rol")
    public Rol insertar(@RequestBody RolDTO rolDTO) {
        return rolServicio.insertar(rolDTO.convertirAEntidad());
    }

    @GetMapping("/roles")
    public List<Rol> listar() {
        return rolServicio.listar();
    }

    @PutMapping("/rol/{id}")
    public Rol actualizar(@PathVariable Integer id, @RequestBody RolDTO rolDTO) {
        return rolServicio.actualizar(id, rolDTO.convertirAEntidad());
    }
}
