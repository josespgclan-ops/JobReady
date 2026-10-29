package com.jobready.web.entidades;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "carreras")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Carrera {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_carrera")
    private Integer idCarrera;
    @Column(nullable = false, unique = true, length = 100)
    private String nombre;
    @Column(columnDefinition = "TEXT")
    private String descripcion;
    private Boolean activo;
}


