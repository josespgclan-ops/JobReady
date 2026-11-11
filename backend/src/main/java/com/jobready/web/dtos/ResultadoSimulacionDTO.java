package com.jobready.web.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ResultadoSimulacionDTO {
    private Integer idSimulacion;
    private Integer puntajeGeneral;
    private String nivelComunicacion;
    private BigDecimal tiempoPromedioRespuesta;
    private BigDecimal seguridadCalificacion;
    private String resumenFeedback;
    private String nombreEspecialista;
    /** Fecha en que el evaluador envió los resultados. */
    private LocalDateTime fechaEvaluacion;
    /** PDF de retroalimentación generado al enviar la evaluación; null en evaluaciones antiguas sin PDF. */
    private Integer idArchivoPdf;
    private String nombreCandidato;
    /** Claridad de respuestas (%); null en evaluaciones anteriores a la rúbrica de tres porcentajes. */
    private Integer claridadRespuestas;
    /** Dominio del tema (%). */
    private Integer dominioTema;
    private String oportunidadesMejora;
    private String recomendaciones;
}
