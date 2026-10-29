package com.jobready.web.servicios;

import com.jobready.web.entidades.PreguntaEntrevista;
import com.jobready.web.repositorios.PreguntaEntrevistaRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.io.Serializable;
import java.util.List;

@Service
public class PreguntaEntrevistaServicio implements Serializable {
    @Autowired
    private PreguntaEntrevistaRepositorio preguntaEntrevistaRepositorio;

    public PreguntaEntrevista insertar(PreguntaEntrevista pregunta) {
        return preguntaEntrevistaRepositorio.save(pregunta);
    }

    public List<PreguntaEntrevista> listar() {
        return preguntaEntrevistaRepositorio.findAll();
    }

    public List<PreguntaEntrevista> listarPorCarrera(Integer idCarrera) {
        return preguntaEntrevistaRepositorio.findByCarrera_IdCarreraAndActivoTrue(idCarrera);
    }

    public PreguntaEntrevista actualizar(Integer id, PreguntaEntrevista datos) {
        PreguntaEntrevista pregunta = preguntaEntrevistaRepositorio.findById(id)
                .orElseThrow(() -> new RuntimeException("No existe la pregunta con id " + id));
        pregunta.setCarrera(datos.getCarrera());
        pregunta.setTextoPregunta(datos.getTextoPregunta());
        pregunta.setCategoria(datos.getCategoria());
        pregunta.setNivelDificultad(datos.getNivelDificultad());
        pregunta.setActivo(datos.getActivo());
        return preguntaEntrevistaRepositorio.save(pregunta);
    }

    public PreguntaEntrevista desactivar(Integer id) {
        PreguntaEntrevista pregunta = preguntaEntrevistaRepositorio.findById(id)
                .orElseThrow(() -> new RuntimeException("No existe la pregunta con id " + id));
        pregunta.setActivo(false);
        return preguntaEntrevistaRepositorio.save(pregunta);
    }
}
