package com.jobready.web.dtos;

import com.jobready.web.entidades.Carrera;
import com.jobready.web.entidades.PreguntaEntrevista;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class PreguntaEntrevistaDTO {
    private Integer idCarrera;
    private String textoPregunta;
    private String categoria;
    private String nivelDificultad;
    private Boolean activo;

    public PreguntaEntrevista convertirAEntidad() {
        PreguntaEntrevista pregunta = new PreguntaEntrevista();
        if (idCarrera != null) {
            Carrera carrera = new Carrera();
            carrera.setIdCarrera(idCarrera);
            pregunta.setCarrera(carrera);
        }
        pregunta.setTextoPregunta(textoPregunta);
        pregunta.setCategoria(categoria);
        pregunta.setNivelDificultad(nivelDificultad);
        pregunta.setActivo(activo);
        return pregunta;
    }
}
