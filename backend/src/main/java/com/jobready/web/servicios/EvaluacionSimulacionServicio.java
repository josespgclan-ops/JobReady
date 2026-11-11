package com.jobready.web.servicios;

import com.jobready.web.entidades.ArchivoSimulacion;
import com.jobready.web.entidades.EvaluacionSimulacion;
import com.jobready.web.entidades.Simulacion;
import com.jobready.web.repositorios.ArchivoSimulacionRepositorio;
import com.jobready.web.repositorios.EvaluacionSimulacionRepositorio;
import com.jobready.web.repositorios.SimulacionRepositorio;
import com.jobready.web.repositorios.UsuarioRepositorio;
import com.jobready.web.entidades.Usuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import com.jobready.web.dtos.ResultadoSimulacionDTO;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;


@Service
public class EvaluacionSimulacionServicio implements Serializable {
    @Autowired
    private EvaluacionSimulacionRepositorio evaluacionSimulacionRepositorio;
    @Autowired
    private UsuarioRepositorio usuarioRepositorio;
    @Autowired
    private SimulacionRepositorio simulacionRepositorio;
    @Autowired
    private ArchivoSimulacionRepositorio archivoSimulacionRepositorio;
    @Autowired
    private ArchivoSimulacionServicio archivoSimulacionServicio;

    public EvaluacionSimulacion insertar(EvaluacionSimulacion evaluacionSimulacion) {
        return evaluacionSimulacionRepositorio.save(evaluacionSimulacion);
    }

    public List<EvaluacionSimulacion> listar() {
        return evaluacionSimulacionRepositorio.findAll();
    }

    /**
     * US06: registra la evaluación con el evaluador que inició sesión como especialista.
     * Valida los rangos y evita evaluar dos veces la misma simulación.
     */
    public EvaluacionSimulacion registrarComoEspecialista(EvaluacionSimulacion evaluacion, String username) {
        return registrarComoEspecialista(evaluacion, username, null);
    }

    /**
     * Igual que el registro normal, pero con un PDF opcional de indicaciones para el candidato.
     * La evaluación y el PDF se guardan en la misma transacción.
     */
    @Transactional
    public EvaluacionSimulacion registrarComoEspecialista(EvaluacionSimulacion evaluacion, String username,
                                                          MultipartFile pdf) {
        if (evaluacion.getSimulacion() == null || evaluacion.getSimulacion().getIdSimulacion() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Falta la simulacion a evaluar");
        }
        Simulacion simulacion = simulacionRepositorio.findById(evaluacion.getSimulacion().getIdSimulacion())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No existe la simulacion"));
        if (evaluacionSimulacionRepositorio.findBySimulacion_IdSimulacion(simulacion.getIdSimulacion()) != null) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Esta simulacion ya fue evaluada por otro especialista");
        }
        // Una entrevista asignada a un evaluador solo la evalúa ese evaluador.
        if (simulacion.getEvaluador() != null && !username.equals(simulacion.getEvaluador().getUsername())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Esta entrevista está asignada a otro evaluador");
        }
        validarRubrica(evaluacion);
        byte[] bytesPdf = pdf != null && !pdf.isEmpty() ? archivoSimulacionServicio.leerPdf(pdf) : null;
        Usuario especialista = usuarioRepositorio.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuario no encontrado"));
        evaluacion.setSimulacion(simulacion);
        evaluacion.setEspecialista(especialista);
        evaluacion.setEvaluadoPorEspecialista(true);
        evaluacion.setFechaEvaluacion(LocalDateTime.now());
        EvaluacionSimulacion guardada = evaluacionSimulacionRepositorio.save(evaluacion);
        if (bytesPdf != null) {
            archivoSimulacionServicio.guardar(simulacion, ArchivoSimulacionServicio.TIPO_PDF_EVALUACION,
                    MediaType.APPLICATION_PDF_VALUE, bytesPdf, null);
        }
        return guardada;
    }

    private void validarRubrica(EvaluacionSimulacion evaluacion) {
        Integer puntaje = evaluacion.getPuntajeGeneral();
        if (puntaje == null || puntaje < 0 || puntaje > 100) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El puntaje general debe estar entre 0 y 100");
        }
        if (evaluacion.getSeguridadCalificacion() == null
                || evaluacion.getSeguridadCalificacion().compareTo(java.math.BigDecimal.ZERO) < 0
                || evaluacion.getSeguridadCalificacion().compareTo(java.math.BigDecimal.ONE) > 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La seguridad debe estar entre 0 y 1");
        }
        if (evaluacion.getNivelComunicacion() == null || evaluacion.getNivelComunicacion().isBlank()
                || evaluacion.getFeedbackResumen() == null || evaluacion.getFeedbackResumen().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Completa el nivel de comunicacion y el feedback");
        }
    }

    public List<EvaluacionSimulacion> listarPorEspecialista(String username) {
        return evaluacionSimulacionRepositorio.findByEspecialista_UsernameOrderByIdEvaluacionDesc(username);
    }

    public ResultadoSimulacionDTO obtenerResultado(Integer idSimulacion, String username) {
        EvaluacionSimulacion evaluacion =
                evaluacionSimulacionRepositorio
                        .findBySimulacion_IdSimulacion(idSimulacion);
        String nombreEspecialista = null;
        if (evaluacion == null) {
            // 404: la simulación todavía no tiene evaluación publicada.
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "La simulacion aun no tiene evaluacion");
        }
        // 403: un candidato solo puede ver el resultado de sus propias simulaciones.
        if (!evaluacion.getSimulacion().getUsuario().getUsername().equals(username)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "La simulacion pertenece a otro usuario");
        }
        if (evaluacion.getEspecialista() != null) {
            nombreEspecialista = evaluacion.getEspecialista().getNombreCompleto();
        }
        Integer idArchivoPdf = archivoSimulacionRepositorio
                .findFirstBySimulacion_IdSimulacionAndTipoArchivoOrderByFechaCreacionDesc(
                        idSimulacion, ArchivoSimulacionServicio.TIPO_PDF_EVALUACION)
                .map(ArchivoSimulacion::getIdArchivo)
                .orElse(null);
        return new ResultadoSimulacionDTO(
                evaluacion.getSimulacion().getIdSimulacion(),
                evaluacion.getPuntajeGeneral(),
                evaluacion.getNivelComunicacion(),
                evaluacion.getTiempoPromedioRespuesta(),
                evaluacion.getSeguridadCalificacion(),
                evaluacion.getFeedbackResumen(),
                nombreEspecialista,
                evaluacion.getFechaEvaluacion(),
                idArchivoPdf
        );
    }

    /** El evaluador solo puede corregir sus propias evaluaciones; el autor y la simulación no cambian. */
    public EvaluacionSimulacion actualizarPropia(Integer id, EvaluacionSimulacion datos, String username) {
        EvaluacionSimulacion evaluacion = buscarPropia(id, username);
        validarRubrica(datos);
        evaluacion.setPuntajeGeneral(datos.getPuntajeGeneral());
        evaluacion.setNivelComunicacion(datos.getNivelComunicacion());
        evaluacion.setTiempoPromedioRespuesta(datos.getTiempoPromedioRespuesta());
        evaluacion.setSeguridadCalificacion(datos.getSeguridadCalificacion());
        evaluacion.setFeedbackResumen(datos.getFeedbackResumen());
        return evaluacionSimulacionRepositorio.save(evaluacion);
    }

    /** Si username es null (administrador) puede borrar cualquiera; si no, solo las propias. */
    public void eliminar(Integer id, String username) {
        EvaluacionSimulacion evaluacion = username == null
                ? evaluacionSimulacionRepositorio.findById(id)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No existe la evaluacion con id " + id))
                : buscarPropia(id, username);
        // El PDF de indicaciones pertenece a la evaluación: se elimina con ella.
        archivoSimulacionServicio.eliminarPdfsEvaluacion(evaluacion.getSimulacion().getIdSimulacion());
        evaluacionSimulacionRepositorio.delete(evaluacion);
    }

    private EvaluacionSimulacion buscarPropia(Integer id, String username) {
        EvaluacionSimulacion evaluacion = evaluacionSimulacionRepositorio.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No existe la evaluacion con id " + id));
        if (evaluacion.getEspecialista() == null || !username.equals(evaluacion.getEspecialista().getUsername())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Solo puedes modificar tus propias evaluaciones");
        }
        return evaluacion;
    }
}


