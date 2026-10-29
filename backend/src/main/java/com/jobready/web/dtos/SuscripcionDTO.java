package com.jobready.web.dtos;

import com.jobready.web.entidades.*;
import lombok.*;
import java.time.LocalDate;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class SuscripcionDTO {
    private Integer idUsuario;
    private Integer idPlan;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private String estado;

    public Suscripcion convertirAEntidad() {
        Usuario usuario = new Usuario(); usuario.setIdUsuario(idUsuario);
        Plan plan = new Plan(); plan.setIdPlan(idPlan);
        Suscripcion suscripcion = new Suscripcion();
        suscripcion.setUsuario(usuario);
        suscripcion.setPlan(plan);
        suscripcion.setFechaInicio(fechaInicio);
        suscripcion.setFechaFin(fechaFin);
        suscripcion.setEstado(estado);
        return suscripcion;
    }
}
