package com.jobready.web.controladores;

import com.jobready.web.entidades.PreguntaEntrevista;
import com.jobready.web.dtos.PreguntaEntrevistaDTO;
import com.jobready.web.servicios.PreguntaEntrevistaServicio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;
import java.util.List;

@RestController
@RequestMapping("/api")
public class PreguntaEntrevistaController {
    @Autowired
    private PreguntaEntrevistaServicio preguntaEntrevistaServicio;

    @PostMapping("/pregunta-entrevista")
    @PreAuthorize("hasRole('ADMIN')")
    public PreguntaEntrevista insertar(@RequestBody PreguntaEntrevistaDTO preguntaDTO) {
        return preguntaEntrevistaServicio.insertar(preguntaDTO.convertirAEntidad());
    }

    @GetMapping("/preguntas-entrevista")
    @PreAuthorize("hasAnyRole('ADMIN', 'CANDIDATO', 'EVALUADOR')")
    public List<PreguntaEntrevista> listar() {
        return preguntaEntrevistaServicio.listar();
    }

    @GetMapping("/preguntas-entrevista/carrera/{idCarrera}")
    @PreAuthorize("hasAnyRole('ADMIN', 'CANDIDATO', 'EVALUADOR')")
    public List<PreguntaEntrevista> listarPorCarrera(@PathVariable Integer idCarrera) {
        return preguntaEntrevistaServicio.listarPorCarrera(idCarrera);
    }

    @PutMapping("/pregunta-entrevista/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public PreguntaEntrevista actualizar(@PathVariable Integer id,
                                         @RequestBody PreguntaEntrevistaDTO preguntaDTO) {
        return preguntaEntrevistaServicio.actualizar(id, preguntaDTO.convertirAEntidad());
    }

    @PatchMapping("/pregunta-entrevista/{id}/desactivar")
    @PreAuthorize("hasRole('ADMIN')")
    public PreguntaEntrevista desactivar(@PathVariable Integer id) {
        return preguntaEntrevistaServicio.desactivar(id);
    }
}
