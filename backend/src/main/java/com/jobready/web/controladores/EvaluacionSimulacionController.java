package com.jobready.web.controladores;

import com.jobready.web.dtos.ResultadoSimulacionDTO;
import com.jobready.web.entidades.EvaluacionSimulacion;
import com.jobready.web.dtos.EvaluacionSimulacionDTO;
import com.jobready.web.servicios.EvaluacionSimulacionServicio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.multipart.MultipartFile;
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

    /** Misma evaluación, enviada como multipart para adjuntar el PDF de indicaciones al candidato. */
    @PostMapping(value = "/evaluacion-simulacion", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('EVALUADOR')")
    public EvaluacionSimulacion insertarConPdf(@RequestPart("evaluacion") EvaluacionSimulacionDTO evaluacionSimulacionDTO,
                                               @RequestPart(value = "pdf", required = false) MultipartFile pdf,
                                               Authentication authentication) {
        return evaluacionSimulacionServicio.registrarComoEspecialista(
                evaluacionSimulacionDTO.convertirAEntidad(), authentication.getName(), pdf);
    }

    @GetMapping("/evaluaciones-simulacion")
    @PreAuthorize("hasAnyRole('ADMIN', 'EVALUADOR')") // el candidato usa /resultado/{idSimulacion}
    public List<EvaluacionSimulacion> listar() {
        return evaluacionSimulacionServicio.listar();
    }

    @PutMapping("/evaluacion-simulacion/{id}")
    @PreAuthorize("hasRole('EVALUADOR')")
    public EvaluacionSimulacion actualizar(@PathVariable Integer id,
                                           @RequestBody EvaluacionSimulacionDTO evaluacionSimulacionDTO,
                                           Authentication authentication) {
        return evaluacionSimulacionServicio.actualizarPropia(id, evaluacionSimulacionDTO.convertirAEntidad(), authentication.getName());
    }

    @DeleteMapping("/evaluacion-simulacion/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'EVALUADOR')")
    public void eliminar(@PathVariable Integer id, Authentication authentication) {
        boolean esAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> "ROLE_ADMIN".equals(a.getAuthority()));
        evaluacionSimulacionServicio.eliminar(id, esAdmin ? null : authentication.getName());
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
