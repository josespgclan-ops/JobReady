package com.jobready.web.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;

/** Fila del historial del candidato: una práctica, su video y su estado de revisión. */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EnvioEntrevistaDTO {
    private Integer idSimulacion;
    private Integer idCarrera;
    private String nombreCarrera;
    private Integer numeroSimulacion;
    /** Fecha en que se envió el video (o en que terminó la práctica si no tiene video). */
    private LocalDateTime fechaEnvio;
    private Integer duracionSegundos;
    private String estado;
    /** PENDIENTE, REVISADA o EN_CURSO. */
    private String estadoRevision;
    private Integer idArchivoVideo;
    private LocalDateTime fechaEvaluacion;
    /** PDF de retroalimentación del evaluador; null si la práctica no fue evaluada o no tiene PDF. */
    private Integer idArchivoPdf;
}
