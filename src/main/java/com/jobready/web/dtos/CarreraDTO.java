package com.jobready.web.dtos;

import com.jobready.web.entidades.Carrera;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class CarreraDTO {
    private String nombre;
    private String descripcion;
    private Boolean activo;

    public Carrera convertirAEntidad() {
        Carrera carrera = new Carrera();
        carrera.setNombre(nombre);
        carrera.setDescripcion(descripcion);
        carrera.setActivo(activo);
        return carrera;
    }
}
