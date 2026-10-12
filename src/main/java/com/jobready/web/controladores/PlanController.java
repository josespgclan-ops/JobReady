package com.jobready.web.controladores;

import com.jobready.web.entidades.Plan;
import com.jobready.web.dtos.PlanDTO;
import com.jobready.web.servicios.PlanServicio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;
import java.util.List;

@RestController
@RequestMapping("/api")
public class PlanController {
    @Autowired
    private PlanServicio planServicio;

    @PostMapping("/plan")
    @PreAuthorize("hasRole('ADMIN')")
    public Plan insertar(@RequestBody PlanDTO planDTO) {
        return planServicio.insertar(planDTO.convertirAEntidad());
    }

    @GetMapping("/planes")
    @PreAuthorize("hasAnyRole('ADMIN', 'CANDIDATO')")
    public List<Plan> listar() {
        return planServicio.listar();
    }

    @PutMapping("/plan/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Plan actualizar(@PathVariable Integer id, @RequestBody PlanDTO planDTO) {
        return planServicio.actualizar(id, planDTO.convertirAEntidad());
    }
}
