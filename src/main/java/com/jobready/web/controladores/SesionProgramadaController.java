package com.jobready.web.controladores;

import com.jobready.web.entidades.SesionProgramada;
import com.jobready.web.dtos.SesionProgramadaDTO;
import com.jobready.web.servicios.SesionProgramadaServicio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;
import java.util.List;

@RestController
@RequestMapping("/api")
@PreAuthorize("hasRole('CANDIDATO')")
public class SesionProgramadaController {
    @Autowired
    private SesionProgramadaServicio sesionProgramadaServicio;

    @PostMapping("/sesion-programada")
    public SesionProgramada insertar(@RequestBody SesionProgramadaDTO sesionProgramadaDTO) {
        return sesionProgramadaServicio.insertar(sesionProgramadaDTO.convertirAEntidad());
    }

    @GetMapping("/sesiones-programadas")
    public List<SesionProgramada> listar() {
        return sesionProgramadaServicio.listar();
    }

    @PutMapping("/sesion-programada/{id}")
    public SesionProgramada actualizar(@PathVariable Integer id,
                                       @RequestBody SesionProgramadaDTO sesionProgramadaDTO) {
        return sesionProgramadaServicio.actualizar(id, sesionProgramadaDTO.convertirAEntidad());
    }

    @PatchMapping("/sesion-programada/{id}/cancelar")
    public SesionProgramada cancelar(@PathVariable Integer id) {
        return sesionProgramadaServicio.cancelar(id);
    }
}
