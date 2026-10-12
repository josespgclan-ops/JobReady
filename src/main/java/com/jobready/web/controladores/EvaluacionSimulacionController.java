package com.jobready.web.controladores;

import com.jobready.web.entidades.EvaluacionSimulacion;
import com.jobready.web.dtos.EvaluacionSimulacionDTO;
import com.jobready.web.servicios.EvaluacionSimulacionServicio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;
import java.util.List;

@RestController
@RequestMapping("/api")
public class EvaluacionSimulacionController {
    @Autowired
    private EvaluacionSimulacionServicio evaluacionSimulacionServicio;

    @PostMapping("/evaluacion-simulacion")
    @PreAuthorize("hasRole('EVALUADOR')")
    public EvaluacionSimulacion insertar(@RequestBody EvaluacionSimulacionDTO evaluacionSimulacionDTO) {
        return evaluacionSimulacionServicio.insertar(evaluacionSimulacionDTO.convertirAEntidad());
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
}
