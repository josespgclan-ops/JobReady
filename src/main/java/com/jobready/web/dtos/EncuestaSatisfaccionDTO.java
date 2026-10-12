package com.jobready.web.dtos;

import com.jobready.web.entidades.*;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class EncuestaSatisfaccionDTO {
    private Integer idUsuario;
    private Integer satisfaccionGeneral;
    private String calificacionServicio;
    private String calidadProducto;
    private String comentarioAdicional;
    private String nombreContacto;
    private String correoContacto;

    public EncuestaSatisfaccion convertirAEntidad() {
        EncuestaSatisfaccion encuesta = new EncuestaSatisfaccion();
        if (idUsuario != null) {
            Usuario usuario = new Usuario(); usuario.setIdUsuario(idUsuario);
            encuesta.setUsuario(usuario);
        }
        encuesta.setSatisfaccionGeneral(satisfaccionGeneral);
        encuesta.setCalificacionServicio(calificacionServicio);
        encuesta.setCalidadProducto(calidadProducto);
        encuesta.setComentarioAdicional(comentarioAdicional);
        encuesta.setNombreContacto(nombreContacto);
        encuesta.setCorreoContacto(correoContacto);
        return encuesta;
    }
}
