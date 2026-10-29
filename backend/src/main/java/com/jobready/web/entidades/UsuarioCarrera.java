package com.jobready.web.entidades;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "usuario_carreras")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioCarrera {
    @EmbeddedId
    private UsuarioCarreraId id;
    @ManyToOne
    @MapsId("idUsuario")
    @JoinColumn(name = "id_usuario")
    private Usuario usuario;
    @ManyToOne
    @MapsId("idCarrera")
    @JoinColumn(name = "id_carrera")
    private Carrera carrera;
    @Column(name = "fecha_seleccion")
    private LocalDateTime fechaSeleccion;
}


