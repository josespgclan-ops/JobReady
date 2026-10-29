package com.jobready.web.controladores;

import com.jobready.web.entidades.ArchivoSimulacion;
import com.jobready.web.dtos.ArchivoSimulacionDTO;
import com.jobready.web.servicios.ArchivoSimulacionServicio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;
import java.util.List;

@RestController
@RequestMapping("/api")
public class ArchivoSimulacionController {
    @Autowired
    private ArchivoSimulacionServicio archivoSimulacionServicio;

    @PostMapping("/archivo-simulacion")
    @PreAuthorize("hasAnyRole('CANDIDATO', 'EVALUADOR')")
    public ArchivoSimulacion insertar(@RequestBody ArchivoSimulacionDTO archivoSimulacionDTO) {
        return archivoSimulacionServicio.insertar(archivoSimulacionDTO.convertirAEntidad());
    }

    @GetMapping("/archivos-simulacion")
    @PreAuthorize("hasAnyRole('ADMIN', 'EVALUADOR')")
    public List<ArchivoSimulacion> listar() {
        return archivoSimulacionServicio.listar();
    }

    @DeleteMapping("/archivo-simulacion/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'CANDIDATO')")
    public void eliminar(@PathVariable Integer id) {
        archivoSimulacionServicio.eliminar(id);
    }
}
