package com.jobready.web.controladores;

import com.jobready.web.entidades.EncuestaSatisfaccion;
import com.jobready.web.dtos.EncuestaSatisfaccionDTO;
import com.jobready.web.servicios.EncuestaSatisfaccionServicio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;
import java.util.List;

@RestController
@RequestMapping("/api")
public class EncuestaSatisfaccionController {
    @Autowired
    private EncuestaSatisfaccionServicio encuestaSatisfaccionServicio;

    /** El candidato conectado envía su opinión; el usuario se toma del token JWT. */
    @PostMapping("/encuesta-satisfaccion")
    @PreAuthorize("hasRole('CANDIDATO')")
    public EncuestaSatisfaccion insertar(@RequestBody EncuestaSatisfaccionDTO encuestaSatisfaccionDTO,
                                         Authentication authentication) {
        return encuestaSatisfaccionServicio.insertar(encuestaSatisfaccionDTO.convertirAEntidad(), authentication.getName());
    }

    @GetMapping("/encuestas-satisfaccion")
    @PreAuthorize("hasRole('ADMIN')")
    public List<EncuestaSatisfaccion> listar() {
        return encuestaSatisfaccionServicio.listar();
    }

    /** Encuestas que ya envió el candidato conectado. */
    @GetMapping("/encuestas-satisfaccion/mias")
    @PreAuthorize("hasRole('CANDIDATO')")
    public List<EncuestaSatisfaccion> listarMias(Authentication authentication) {
        return encuestaSatisfaccionServicio.listarPorUsuario(authentication.getName());
    }
}
