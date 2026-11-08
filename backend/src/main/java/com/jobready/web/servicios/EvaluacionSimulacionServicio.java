package com.jobready.web.servicios;

import com.jobready.web.entidades.EvaluacionSimulacion;
import com.jobready.web.repositorios.EvaluacionSimulacionRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.io.Serializable;
import java.util.List;
import com.jobready.web.dtos.ResultadoSimulacionDTO;


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

    public ResultadoSimulacionDTO obtenerResultado(Integer idSimulacion) {
        EvaluacionSimulacion evaluacion =
                evaluacionSimulacionRepositorio
                        .findBySimulacion_IdSimulacion(idSimulacion);
        String nombreEspecialista = null;
        if (evaluacion == null) {
            throw new RuntimeException("No existe una evaluacion");
        }
        if (evaluacion.getEspecialista() != null) {
            nombreEspecialista = evaluacion.getEspecialista().getNombreCompleto();
        }
        return new ResultadoSimulacionDTO(
                evaluacion.getSimulacion().getIdSimulacion(),
                evaluacion.getPuntajeGeneral(),
                evaluacion.getNivelComunicacion(),
                evaluacion.getTiempoPromedioRespuesta(),
                evaluacion.getSeguridadCalificacion(),
                evaluacion.getFeedbackResumen(),
                nombreEspecialista
        );
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


