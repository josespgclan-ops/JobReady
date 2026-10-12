package com.jobready.web.security.controllers;

import com.jobready.web.entidades.Usuario;
import com.jobready.web.security.dtos.RegistroCandidatoDTO;
import com.jobready.web.security.services.RegistroCandidatoServicio;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class RegistroController {
    private final RegistroCandidatoServicio registroCandidatoServicio;

    public RegistroController(RegistroCandidatoServicio registroCandidatoServicio) {
        this.registroCandidatoServicio = registroCandidatoServicio;
    }

    @PostMapping("/registro")
    public Usuario registrar(@RequestBody RegistroCandidatoDTO registroDTO) {
        return registroCandidatoServicio.registrar(registroDTO);
    }
}
