package com.jobready.web.controladores;

import com.jobready.web.dtos.ResultadoSimulacionDTO;
import com.jobready.web.entidades.EvaluacionSimulacion;
import com.jobready.web.dtos.EvaluacionSimulacionDTO;
import com.jobready.web.servicios.EvaluacionSimulacionServicio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import java.util.List;

@RestController
@RequestMapping("/api")
public class EvaluacionSimulacionController {
    @Autowired
    private EvaluacionSimulacionServicio evaluacionSimulacionServicio;

    @PostMapping("/evaluacion-simulacion")
    @PreAuthorize("hasRole('EVALUADOR')")
    public EvaluacionSimulacion insertar(@RequestBody EvaluacionSimulacionDTO evaluacionSimulacionDTO,
                                         Authentication authentication) {
        return evaluacionSimulacionServicio.registrarComoEspecialista(evaluacionSimulacionDTO.convertirAEntidad(), authentication.getName());
    }

    @GetMapping("/evaluaciones-simulacion")
    @PreAuthorize("hasAnyRole('ADMIN', 'CANDIDATO', 'EVALUADOR')")
    public List<EvaluacionSimulacion> listar() {
        return evaluacionSimulacionServicio.listar();
    }

    @PutMapping("/evaluacion-simulacion/{id}")
    @PreAuthorize("hasRole('EVALUADOR')")
    public EvaluacionSimulacion actualizar(@PathVariable Integer id,
                                           @RequestBody EvaluacionSimulacionDTO evaluacionSimulacionDTO) {
        return evaluacionSimulacionServicio.actualizar(id, evaluacionSimulacionDTO.convertirAEntidad());
    }

    @DeleteMapping("/evaluacion-simulacion/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'EVALUADOR')")
    public void eliminar(@PathVariable Integer id) {
        evaluacionSimulacionServicio.eliminar(id);
    }

    /** US21: evaluaciones registradas por el evaluador que inició sesión. */
    @GetMapping("/evaluaciones-simulacion/mias")
    @PreAuthorize("hasRole('EVALUADOR')")
    public List<EvaluacionSimulacion> listarMias(Authentication authentication) {
        return evaluacionSimulacionServicio.listarPorEspecialista(authentication.getName());
    }

    @GetMapping("/resultado/{idSimulacion}")
    @PreAuthorize("hasRole('CANDIDATO')")
    public ResultadoSimulacionDTO obtenerResultado(@PathVariable Integer idSimulacion,
                                                   Authentication authentication) {
        return evaluacionSimulacionServicio.obtenerResultado(idSimulacion, authentication.getName());
    }

}
