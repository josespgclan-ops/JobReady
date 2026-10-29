package com.jobready.web.entidades;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "simulaciones")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Simulacion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_simulacion")
    private Integer idSimulacion;
    @ManyToOne
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;
    @ManyToOne
    @JoinColumn(name = "id_carrera", nullable = false)
    private Carrera carrera;
    @ManyToOne
    @JoinColumn(name = "id_sesion")
    private SesionProgramada sesion;
    @Column(name = "numero_simulacion", nullable = false)
    private Integer numeroSimulacion;
    @Column(name = "fecha_inicio", nullable = false)
    private LocalDateTime fechaInicio;
    @Column(name = "fecha_fin")
    private LocalDateTime fechaFin;
    @Column(name = "duracion_segundos")
    private Integer duracionSegundos;
    @Column(length = 20)
    private String estado;
}


