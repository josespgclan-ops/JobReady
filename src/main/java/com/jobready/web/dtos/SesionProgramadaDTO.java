package com.jobready.web.dtos;

import com.jobready.web.entidades.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalTime;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class SesionProgramadaDTO {
    private Integer idUsuario;
    private Integer idCarrera;
    private LocalDate fechaProgramada;
    private LocalTime horaProgramada;
    private String estado;
    private Boolean recordatorioEnviado;

    public SesionProgramada convertirAEntidad() {
        Usuario usuario = new Usuario(); usuario.setIdUsuario(idUsuario);
        SesionProgramada sesion = new SesionProgramada();
        sesion.setUsuario(usuario);
        if (idCarrera != null) {
            Carrera carrera = new Carrera(); carrera.setIdCarrera(idCarrera);
            sesion.setCarrera(carrera);
        }
        sesion.setFechaProgramada(fechaProgramada);
        sesion.setHoraProgramada(horaProgramada);
        sesion.setEstado(estado);
        sesion.setRecordatorioEnviado(recordatorioEnviado);
        return sesion;
    }
}
