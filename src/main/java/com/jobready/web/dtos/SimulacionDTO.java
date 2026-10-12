package com.jobready.web.dtos;

import com.jobready.web.entidades.*;
import lombok.*;
import java.time.LocalDateTime;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class SimulacionDTO {
    private Integer idUsuario;
    private Integer idCarrera;
    private Integer idSesion;
    private Integer numeroSimulacion;
    private LocalDateTime fechaInicio;
    private LocalDateTime fechaFin;
    private Integer duracionSegundos;
    private String estado;

    public Simulacion convertirAEntidad() {
        Usuario usuario = new Usuario(); usuario.setIdUsuario(idUsuario);
        Carrera carrera = new Carrera(); carrera.setIdCarrera(idCarrera);
        Simulacion simulacion = new Simulacion();
        simulacion.setUsuario(usuario);
        simulacion.setCarrera(carrera);
        if (idSesion != null) {
            SesionProgramada sesion = new SesionProgramada(); sesion.setIdSesion(idSesion);
            simulacion.setSesion(sesion);
        }
        simulacion.setNumeroSimulacion(numeroSimulacion);
        simulacion.setFechaInicio(fechaInicio);
        simulacion.setFechaFin(fechaFin);
        simulacion.setDuracionSegundos(duracionSegundos);
        simulacion.setEstado(estado);
        return simulacion;
    }
}
