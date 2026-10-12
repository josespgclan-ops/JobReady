package com.jobready.web.dtos;

import com.jobready.web.entidades.*;
import lombok.*;
import java.math.BigDecimal;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class EvaluacionSimulacionDTO {
    private Integer idSimulacion;
    private Integer puntajeGeneral;
    private String nivelComunicacion;
    private BigDecimal tiempoPromedioRespuesta;
    private BigDecimal seguridadCalificacion;
    private String feedbackResumen;
    private Boolean evaluadoPorEspecialista;
    private Integer idEspecialista;

    public EvaluacionSimulacion convertirAEntidad() {
        Simulacion simulacion = new Simulacion(); simulacion.setIdSimulacion(idSimulacion);
        EvaluacionSimulacion evaluacion = new EvaluacionSimulacion();
        evaluacion.setSimulacion(simulacion);
        evaluacion.setPuntajeGeneral(puntajeGeneral);
        evaluacion.setNivelComunicacion(nivelComunicacion);
        evaluacion.setTiempoPromedioRespuesta(tiempoPromedioRespuesta);
        evaluacion.setSeguridadCalificacion(seguridadCalificacion);
        evaluacion.setFeedbackResumen(feedbackResumen);
        evaluacion.setEvaluadoPorEspecialista(evaluadoPorEspecialista);
        if (idEspecialista != null) {
            Usuario especialista = new Usuario(); especialista.setIdUsuario(idEspecialista);
            evaluacion.setEspecialista(especialista);
        }
        return evaluacion;
    }
}
