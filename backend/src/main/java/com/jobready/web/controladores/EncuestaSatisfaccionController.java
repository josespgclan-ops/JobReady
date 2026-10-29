package com.jobready.web.controladores;

import com.jobready.web.entidades.EncuestaSatisfaccion;
import com.jobready.web.dtos.EncuestaSatisfaccionDTO;
import com.jobready.web.servicios.EncuestaSatisfaccionServicio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;
import java.util.List;

@RestController
@RequestMapping("/api")
public class EncuestaSatisfaccionController {
    @Autowired
    private EncuestaSatisfaccionServicio encuestaSatisfaccionServicio;

    @PostMapping("/encuesta-satisfaccion")
    @PreAuthorize("hasRole('CANDIDATO')")
    public EncuestaSatisfaccion insertar(@RequestBody EncuestaSatisfaccionDTO encuestaSatisfaccionDTO) {
        return encuestaSatisfaccionServicio.insertar(encuestaSatisfaccionDTO.convertirAEntidad());
    }

    @GetMapping("/encuestas-satisfaccion")
    @PreAuthorize("hasRole('ADMIN')")
    public List<EncuestaSatisfaccion> listar() {
        return encuestaSatisfaccionServicio.listar();
    }
}
