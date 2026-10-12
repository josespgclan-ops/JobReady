package com.jobready.web.dtos;

import com.jobready.web.entidades.*;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class RespuestaEntrevistaDTO {
    private Integer idSimulacion;
    private Integer idPregunta;
    private Integer idArchivo;
    private String textoRespuesta;
    private Integer duracionSegundos;
    private String estado;

    public RespuestaEntrevista convertirAEntidad() {
        Simulacion simulacion = new Simulacion(); simulacion.setIdSimulacion(idSimulacion);
        PreguntaEntrevista pregunta = new PreguntaEntrevista(); pregunta.setIdPregunta(idPregunta);
        RespuestaEntrevista respuesta = new RespuestaEntrevista();
        respuesta.setSimulacion(simulacion);
        respuesta.setPregunta(pregunta);
        if (idArchivo != null) {
            ArchivoSimulacion archivo = new ArchivoSimulacion(); archivo.setIdArchivo(idArchivo);
            respuesta.setArchivo(archivo);
        }
        respuesta.setTextoRespuesta(textoRespuesta);
        respuesta.setDuracionSegundos(duracionSegundos);
        respuesta.setEstado(estado);
        return respuesta;
    }
}
