package com.jobready.web.entidades;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "roles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Rol {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_rol")
    private Integer idRol;
    @Column(nullable = false, unique = true, length = 50)
    private String nombre;
    @Column(length = 255)
    private String descripcion;
}


