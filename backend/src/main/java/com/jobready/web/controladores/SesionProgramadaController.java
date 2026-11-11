package com.jobready.web.controladores;

import com.jobready.web.entidades.SesionProgramada;
import com.jobready.web.dtos.SesionProgramadaDTO;
import com.jobready.web.servicios.SesionProgramadaServicio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;
import java.util.List;

/** Sesiones del candidato conectado; el usuario se toma del token JWT. */
@RestController
@RequestMapping("/api")
@PreAuthorize("hasRole('CANDIDATO')")
public class SesionProgramadaController {
    @Autowired
    private SesionProgramadaServicio sesionProgramadaServicio;

    @PostMapping("/sesion-programada")
    public SesionProgramada insertar(@RequestBody SesionProgramadaDTO sesionProgramadaDTO, Authentication authentication) {
        return sesionProgramadaServicio.insertar(sesionProgramadaDTO.convertirAEntidad(), authentication.getName());
    }

    @GetMapping("/sesiones-programadas")
    public List<SesionProgramada> listar(Authentication authentication) {
        return sesionProgramadaServicio.listarPorUsuario(authentication.getName());
    }

    @PutMapping("/sesion-programada/{id}")
    public SesionProgramada actualizar(@PathVariable Integer id,
                                       @RequestBody SesionProgramadaDTO sesionProgramadaDTO,
                                       Authentication authentication) {
        return sesionProgramadaServicio.actualizar(id, sesionProgramadaDTO.convertirAEntidad(), authentication.getName());
    }

    @PatchMapping("/sesion-programada/{id}/cancelar")
    public SesionProgramada cancelar(@PathVariable Integer id, Authentication authentication) {
        return sesionProgramadaServicio.cancelar(id, authentication.getName());
    }
}
