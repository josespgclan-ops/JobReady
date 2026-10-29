package com.jobready.web.entidades;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "sesiones_programadas")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SesionProgramada {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_sesion")
    private Integer idSesion;
    @ManyToOne
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;
    @ManyToOne
    @JoinColumn(name = "id_carrera")
    private Carrera carrera;
    @Column(name = "fecha_programada", nullable = false)
    private LocalDate fechaProgramada;
    @Column(name = "hora_programada", nullable = false)
    private LocalTime horaProgramada;
    @Column(length = 20)
    private String estado;
    @Column(name = "recordatorio_enviado")
    private Boolean recordatorioEnviado;
}


