package com.jobready.web.controladores;

import com.jobready.web.entidades.Carrera;
import com.jobready.web.dtos.CarreraDTO;
import com.jobready.web.servicios.CarreraServicio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;
import java.util.List;

@RestController
@RequestMapping("/api")
public class CarreraController {
    @Autowired
    private CarreraServicio carreraServicio;

    @PostMapping("/carrera")
    @PreAuthorize("hasRole('ADMIN')")
    public Carrera insertar(@RequestBody CarreraDTO carreraDTO) {
        return carreraServicio.insertar(carreraDTO.convertirAEntidad());
    }

    @GetMapping("/carreras")
    @PreAuthorize("hasAnyRole('ADMIN', 'CANDIDATO', 'EVALUADOR')")
    public List<Carrera> listar() {
        return carreraServicio.listar();
    }

    @PutMapping("/carrera/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Carrera actualizar(@PathVariable Integer id, @RequestBody CarreraDTO carreraDTO) {
        return carreraServicio.actualizar(id, carreraDTO.convertirAEntidad());
    }

    @PatchMapping("/carrera/{id}/desactivar")
    @PreAuthorize("hasRole('ADMIN')")
    public Carrera desactivar(@PathVariable Integer id) {
        return carreraServicio.desactivar(id);
    }
}
