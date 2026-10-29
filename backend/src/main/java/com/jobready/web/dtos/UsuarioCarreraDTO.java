package com.jobready.web.dtos;

import com.jobready.web.entidades.*;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class UsuarioCarreraDTO {
    private Integer idUsuario;
    private Integer idCarrera;

    public UsuarioCarrera convertirAEntidad() {
        Usuario usuario = new Usuario(); usuario.setIdUsuario(idUsuario);
        Carrera carrera = new Carrera(); carrera.setIdCarrera(idCarrera);
        UsuarioCarrera usuarioCarrera = new UsuarioCarrera();
        usuarioCarrera.setId(new UsuarioCarreraId(idUsuario, idCarrera));
        usuarioCarrera.setUsuario(usuario);
        usuarioCarrera.setCarrera(carrera);
        return usuarioCarrera;
    }
}
