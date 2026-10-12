package com.jobready.web.dtos;

import com.jobready.web.entidades.Rol;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class RolDTO {
    private String nombre;
    private String descripcion;

    public Rol convertirAEntidad() {
        Rol rol = new Rol();
        rol.setNombre(nombre);
        rol.setDescripcion(descripcion);
        return rol;
    }
}
