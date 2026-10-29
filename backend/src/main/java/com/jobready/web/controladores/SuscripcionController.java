package com.jobready.web.controladores;

import com.jobready.web.entidades.Suscripcion;
import com.jobready.web.dtos.SuscripcionDTO;
import com.jobready.web.servicios.SuscripcionServicio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;
import java.util.List;

@RestController
@RequestMapping("/api")
@PreAuthorize("hasRole('CANDIDATO')")
public class SuscripcionController {
    @Autowired
    private SuscripcionServicio suscripcionServicio;

    @PostMapping("/suscripcion")
    public Suscripcion insertar(@RequestBody SuscripcionDTO suscripcionDTO) {
        return suscripcionServicio.insertar(suscripcionDTO.convertirAEntidad());
    }

    @GetMapping("/suscripciones")
    public List<Suscripcion> listar() {
        return suscripcionServicio.listar();
    }

    @PutMapping("/suscripcion/{id}")
    public Suscripcion actualizar(@PathVariable Integer id, @RequestBody SuscripcionDTO suscripcionDTO) {
        return suscripcionServicio.actualizar(id, suscripcionDTO.convertirAEntidad());
    }

    @PatchMapping("/suscripcion/{id}/cancelar")
    public Suscripcion cancelar(@PathVariable Integer id) {
        return suscripcionServicio.cancelar(id);
    }
}
