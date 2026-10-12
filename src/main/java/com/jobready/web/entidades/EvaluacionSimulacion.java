package com.jobready.web.entidades;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(name = "evaluaciones_simulacion")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EvaluacionSimulacion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_evaluacion")
    private Integer idEvaluacion;
    @OneToOne
    @JoinColumn(name = "id_simulacion", nullable = false, unique = true)
    private Simulacion simulacion;
    @Column(name = "puntaje_general", nullable = false)
    private Integer puntajeGeneral;
    @Column(name = "nivel_comunicacion", nullable = false, length = 20)
    private String nivelComunicacion;
    @Column(name = "tiempo_promedio_respuesta", nullable = false, precision = 4, scale = 1)
    private BigDecimal tiempoPromedioRespuesta;
    @Column(name = "seguridad_calificacion", nullable = false, precision = 3, scale = 2)
    private BigDecimal seguridadCalificacion;
    @Column(name = "feedback_resumen", columnDefinition = "TEXT")
    private String feedbackResumen;
    @Column(name = "evaluado_por_especialista")
    private Boolean evaluadoPorEspecialista;
    @ManyToOne
    @JoinColumn(name = "id_especialista")
    private Usuario especialista;
}


