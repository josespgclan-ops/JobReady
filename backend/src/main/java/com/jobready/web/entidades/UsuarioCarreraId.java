package com.jobready.web.entidades;

import jakarta.persistence.*;
import lombok.*;
import java.io.Serializable;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class UsuarioCarreraId implements Serializable {
    @Column(name = "id_usuario")
    private Integer idUsuario;
    @Column(name = "id_carrera")
    private Integer idCarrera;
}


