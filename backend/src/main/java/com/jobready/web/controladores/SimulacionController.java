package com.jobready.web.controladores;

import com.jobready.web.entidades.Simulacion;
import com.jobready.web.dtos.SimulacionDTO;
import com.jobready.web.servicios.SimulacionServicio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import java.util.List;

@RestController
@RequestMapping("/api")
public class SimulacionController {
    @Autowired
    private SimulacionServicio simulacionServicio;

    @PostMapping("/simulacion")
    @PreAuthorize("hasRole('CANDIDATO')")
    public Simulacion insertar(@RequestBody SimulacionDTO simulacionDTO) {
        return simulacionServicio.insertar(simulacionDTO.convertirAEntidad());
    }

    @GetMapping("/simulaciones")
    @PreAuthorize("hasAnyRole('ADMIN', 'CANDIDATO', 'EVALUADOR')")
    public List<Simulacion> listar() {
        return simulacionServicio.listar();
    }

    /** US15 / US24: prácticas terminadas que esperan evaluación. */
    @GetMapping("/simulaciones/pendientes")
    @PreAuthorize("hasAnyRole('ADMIN', 'EVALUADOR')")
    public List<Simulacion> listarPendientes() {
        return simulacionServicio.listarPendientesDeEvaluacion();
    }

    /** Historial del candidato: solo sus propias simulaciones, tomando el usuario del token JWT. */
    @GetMapping("/simulaciones/mias")
    @PreAuthorize("hasRole('CANDIDATO')")
    public List<Simulacion> listarMias(Authentication authentication) {
        return simulacionServicio.listarPorUsuario(authentication.getName());
    }

    @PutMapping("/simulacion/{id}")
    @PreAuthorize("hasRole('CANDIDATO')")
    public Simulacion actualizar(@PathVariable Integer id, @RequestBody SimulacionDTO simulacionDTO) {
        return simulacionServicio.actualizar(id, simulacionDTO.convertirAEntidad());
    }

    @DeleteMapping("/simulacion/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'CANDIDATO')")
    public void eliminar(@PathVariable Integer id) {
        simulacionServicio.eliminar(id);
    }
}
