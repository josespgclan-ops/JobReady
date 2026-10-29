package com.jobready.web.servicios;

import com.jobready.web.entidades.EvaluacionSimulacion;
import com.jobready.web.repositorios.EvaluacionSimulacionRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.io.Serializable;
import java.util.List;

@Service
public class EvaluacionSimulacionServicio implements Serializable {
    @Autowired
    private EvaluacionSimulacionRepositorio evaluacionSimulacionRepositorio;

    public EvaluacionSimulacion insertar(EvaluacionSimulacion evaluacionSimulacion) {
        return evaluacionSimulacionRepositorio.save(evaluacionSimulacion);
    }

    public List<EvaluacionSimulacion> listar() {
        return evaluacionSimulacionRepositorio.findAll();
    }

    public EvaluacionSimulacion actualizar(Integer id, EvaluacionSimulacion datos) {
        EvaluacionSimulacion evaluacion = evaluacionSimulacionRepositorio.findById(id)
                .orElseThrow(() -> new RuntimeException("No existe la evaluacion con id " + id));
        evaluacion.setSimulacion(datos.getSimulacion());
        evaluacion.setPuntajeGeneral(datos.getPuntajeGeneral());
        evaluacion.setNivelComunicacion(datos.getNivelComunicacion());
        evaluacion.setTiempoPromedioRespuesta(datos.getTiempoPromedioRespuesta());
        evaluacion.setSeguridadCalificacion(datos.getSeguridadCalificacion());
        evaluacion.setFeedbackResumen(datos.getFeedbackResumen());
        evaluacion.setEvaluadoPorEspecialista(datos.getEvaluadoPorEspecialista());
        evaluacion.setEspecialista(datos.getEspecialista());
        return evaluacionSimulacionRepositorio.save(evaluacion);
    }

    public void eliminar(Integer id) {
        if (!evaluacionSimulacionRepositorio.existsById(id)) {
            throw new RuntimeException("No existe la evaluacion con id " + id);
        }
        evaluacionSimulacionRepositorio.deleteById(id);
    }
}


