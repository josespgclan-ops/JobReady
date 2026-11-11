package com.jobready.web.dtos;

import com.jobready.web.entidades.*;
import lombok.*;
import java.math.BigDecimal;

/**
 * Rúbrica que envía el evaluador: tres porcentajes (0 a 100) y dos textos.
 * El puntaje general no se recibe: el backend lo calcula como el promedio de los tres porcentajes.
 */
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class EvaluacionSimulacionDTO {
    private Integer idSimulacion;
    /** Seguridad al responder (%). */
    private Integer seguridadRespuestas;
    /** Claridad de respuestas (%). */
    private Integer claridadRespuestas;
    /** Dominio del tema (%). */
    private Integer dominioTema;
    private String oportunidadesMejora;
    private String recomendaciones;

    public EvaluacionSimulacion convertirAEntidad() {
        Simulacion simulacion = new Simulacion();
        simulacion.setIdSimulacion(idSimulacion);
        EvaluacionSimulacion evaluacion = new EvaluacionSimulacion();
        evaluacion.setSimulacion(simulacion);
        evaluacion.setSeguridadCalificacion(seguridadRespuestas == null ? null
                : BigDecimal.valueOf(seguridadRespuestas).movePointLeft(2));
        evaluacion.setClaridadRespuestas(claridadRespuestas);
        evaluacion.setDominioTema(dominioTema);
        evaluacion.setOportunidadesMejora(oportunidadesMejora);
        evaluacion.setRecomendaciones(recomendaciones);
        return evaluacion;
    }
}
