package com.jobready.web.dtos;

import com.jobready.web.entidades.Rol;
import com.jobready.web.entidades.Usuario;
import lombok.*;
import java.math.BigDecimal;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class UsuarioDTO {
    private Integer idRol;
    private String nombreCompleto;
    private String email;
    private String username;
    private String passwordHash;
    private String telefono;
    private BigDecimal nivelActual;
    private String estado;

    public Usuario convertirAEntidad() {
        Rol rol = new Rol();
        rol.setIdRol(idRol);
        Usuario usuario = new Usuario();
        usuario.setRol(rol);
        usuario.setNombreCompleto(nombreCompleto);
        usuario.setEmail(email);
        usuario.setUsername(username);
        usuario.setPasswordHash(passwordHash);
        usuario.setTelefono(telefono);
        usuario.setNivelActual(nivelActual);
        usuario.setEstado(estado);
        return usuario;
    }
}
