package com.jobready.web.entidades;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

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
    /** Solo lo tienen las evaluaciones anteriores a la rúbrica de tres porcentajes. */
    @Column(name = "nivel_comunicacion", length = 20)
    private String nivelComunicacion;
    @Column(name = "tiempo_promedio_respuesta", nullable = false, precision = 4, scale = 1)
    private BigDecimal tiempoPromedioRespuesta;
    /** Seguridad al responder, de 0 a 1 (0.85 = 85 %). */
    @Column(name = "seguridad_calificacion", nullable = false, precision = 3, scale = 2)
    private BigDecimal seguridadCalificacion;
    /** Claridad de respuestas (0 a 100 %). */
    @Column(name = "claridad_respuestas")
    private Integer claridadRespuestas;
    /** Dominio del tema (0 a 100 %). */
    @Column(name = "dominio_tema")
    private Integer dominioTema;
    @Column(name = "oportunidades_mejora", columnDefinition = "TEXT")
    private String oportunidadesMejora;
    @Column(name = "recomendaciones", columnDefinition = "TEXT")
    private String recomendaciones;
    @Column(name = "feedback_resumen", columnDefinition = "TEXT")
    private String feedbackResumen;
    @Column(name = "evaluado_por_especialista")
    private Boolean evaluadoPorEspecialista;
    @ManyToOne
    @JoinColumn(name = "id_especialista")
    private Usuario especialista;
    /** Momento en que el evaluador envió los resultados al candidato. */
    @Column(name = "fecha_evaluacion")
    private LocalDateTime fechaEvaluacion;
}


