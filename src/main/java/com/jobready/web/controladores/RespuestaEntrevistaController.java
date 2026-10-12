package com.jobready.web.controladores;

import com.jobready.web.entidades.RespuestaEntrevista;
import com.jobready.web.dtos.RespuestaEntrevistaDTO;
import com.jobready.web.servicios.RespuestaEntrevistaServicio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;
import java.util.List;

@RestController
@RequestMapping("/api")
public class RespuestaEntrevistaController {
    @Autowired
    private RespuestaEntrevistaServicio respuestaEntrevistaServicio;

    @PostMapping("/respuesta-entrevista")
    @PreAuthorize("hasRole('CANDIDATO')")
    public RespuestaEntrevista insertar(@RequestBody RespuestaEntrevistaDTO respuestaDTO) {
        return respuestaEntrevistaServicio.insertar(respuestaDTO.convertirAEntidad());
    }

    @GetMapping("/respuestas-entrevista")
    @PreAuthorize("hasAnyRole('ADMIN', 'CANDIDATO', 'EVALUADOR')")
    public List<RespuestaEntrevista> listar() {
        return respuestaEntrevistaServicio.listar();
    }

    @GetMapping("/respuestas-entrevista/simulacion/{idSimulacion}")
    @PreAuthorize("hasAnyRole('CANDIDATO', 'EVALUADOR')")
    public List<RespuestaEntrevista> listarPorSimulacion(@PathVariable Integer idSimulacion) {
        return respuestaEntrevistaServicio.listarPorSimulacion(idSimulacion);
    }

    @PutMapping("/respuesta-entrevista/{id}")
    @PreAuthorize("hasRole('CANDIDATO')")
    public RespuestaEntrevista actualizar(@PathVariable Integer id,
                                          @RequestBody RespuestaEntrevistaDTO respuestaDTO) {
        return respuestaEntrevistaServicio.actualizar(id, respuestaDTO.convertirAEntidad());
    }

    @DeleteMapping("/respuesta-entrevista/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'CANDIDATO')")
    public void eliminar(@PathVariable Integer id) {
        respuestaEntrevistaServicio.eliminar(id);
    }
}
