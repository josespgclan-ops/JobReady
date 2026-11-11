package com.jobready.web.servicios;

import com.jobready.web.entidades.ArchivoSimulacion;
import com.jobready.web.entidades.EvaluacionSimulacion;
import com.jobready.web.entidades.RespuestaEntrevista;
import com.jobready.web.entidades.Simulacion;
import com.jobready.web.repositorios.ArchivoSimulacionRepositorio;
import com.jobready.web.repositorios.EvaluacionSimulacionRepositorio;
import com.jobready.web.repositorios.RespuestaEntrevistaRepositorio;
import com.jobready.web.repositorios.SimulacionRepositorio;
import com.jobready.web.repositorios.UsuarioRepositorio;
import com.jobready.web.entidades.Usuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import com.jobready.web.dtos.ResultadoSimulacionDTO;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;


@Service
public class EvaluacionSimulacionServicio implements Serializable {
    /** Estado de la simulación una vez que el evaluador envía su evaluación. */
    public static final String ESTADO_EVALUADA = "evaluada";
    private static final String ESTADO_FINALIZADA = "finalizada";
    private static final int MAX_TEXTO = 4000;

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
    @Autowired
    private RespuestaEntrevistaRepositorio respuestaEntrevistaRepositorio;
    @Autowired
    private InformeEvaluacionPdfServicio informePdfServicio;

    public EvaluacionSimulacion insertar(EvaluacionSimulacion evaluacionSimulacion) {
        return evaluacionSimulacionRepositorio.save(evaluacionSimulacion);
    }

    public List<EvaluacionSimulacion> listar() {
        return evaluacionSimulacionRepositorio.findAll();
    }

    /**
     * US06: registra la evaluación con el evaluador que inició sesión como especialista.
     * Calcula el puntaje general, genera el PDF de retroalimentación y marca la entrevista como evaluada,
     * todo en una sola transacción: si el PDF no se puede generar o guardar, no se registra nada.
     */
    @Transactional
    public EvaluacionSimulacion registrarComoEspecialista(EvaluacionSimulacion evaluacion, String username) {
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
        Usuario especialista = usuarioRepositorio.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuario no encontrado"));
        evaluacion.setSimulacion(simulacion);
        evaluacion.setEspecialista(especialista);
        evaluacion.setEvaluadoPorEspecialista(true);
        evaluacion.setFechaEvaluacion(LocalDateTime.now());
        evaluacion.setPuntajeGeneral(calcularPuntajeGeneral(evaluacion));
        evaluacion.setTiempoPromedioRespuesta(calcularTiempoPromedio(simulacion));
        EvaluacionSimulacion guardada;
        try {
            // saveAndFlush: si otro evaluador la registró a la vez, el índice único lo detecta aquí.
            guardada = evaluacionSimulacionRepositorio.saveAndFlush(evaluacion);
        } catch (DataIntegrityViolationException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Esta simulacion ya fue evaluada por otro especialista");
        }
        guardarPdf(guardada);
        simulacion.setEstado(ESTADO_EVALUADA);
        simulacionRepositorio.save(simulacion);
        return guardada;
    }

    /** Genera el PDF con los datos ya guardados y reemplaza cualquier PDF anterior de la evaluación. */
    private void guardarPdf(EvaluacionSimulacion evaluacion) {
        Integer idSimulacion = evaluacion.getSimulacion().getIdSimulacion();
        byte[] pdf = informePdfServicio.generar(evaluacion);
        archivoSimulacionServicio.eliminarPdfsEvaluacion(idSimulacion);
        archivoSimulacionServicio.guardar(evaluacion.getSimulacion(), ArchivoSimulacionServicio.TIPO_PDF_EVALUACION,
                MediaType.APPLICATION_PDF_VALUE, pdf, null);
    }

    /** Promedio de los tres porcentajes, redondeado al entero más cercano. */
    private int calcularPuntajeGeneral(EvaluacionSimulacion evaluacion) {
        int seguridad = evaluacion.getSeguridadCalificacion().movePointRight(2).intValueExact();
        return BigDecimal.valueOf(seguridad + evaluacion.getClaridadRespuestas() + evaluacion.getDominioTema())
                .divide(BigDecimal.valueOf(3), 0, RoundingMode.HALF_UP)
                .intValue();
    }

    /** Promedio de duración de las respuestas escritas; si la práctica es solo un video, su duración. */
    private BigDecimal calcularTiempoPromedio(Simulacion simulacion) {
        double promedio = respuestaEntrevistaRepositorio.findBySimulacion_IdSimulacion(simulacion.getIdSimulacion())
                .stream()
                .map(RespuestaEntrevista::getDuracionSegundos)
                .filter(d -> d != null && d > 0)
                .mapToInt(Integer::intValue)
                .average()
                .orElse(simulacion.getDuracionSegundos() != null ? simulacion.getDuracionSegundos() : 0);
        return BigDecimal.valueOf(promedio).setScale(1, RoundingMode.HALF_UP);
    }

    private void validarRubrica(EvaluacionSimulacion evaluacion) {
        BigDecimal seguridad = evaluacion.getSeguridadCalificacion();
        if (seguridad == null || seguridad.signum() < 0 || seguridad.compareTo(BigDecimal.ONE) > 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La seguridad al responder debe estar entre 0 y 100 %");
        }
        validarPorcentaje(evaluacion.getClaridadRespuestas(), "La claridad de respuestas");
        validarPorcentaje(evaluacion.getDominioTema(), "El dominio del tema");
        evaluacion.setOportunidadesMejora(validarTexto(evaluacion.getOportunidadesMejora(), "las oportunidades de mejora"));
        evaluacion.setRecomendaciones(validarTexto(evaluacion.getRecomendaciones(), "las recomendaciones"));
    }

    private static void validarPorcentaje(Integer valor, String campo) {
        if (valor == null || valor < 0 || valor > 100) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, campo + " debe estar entre 0 y 100 %");
        }
    }

    /** Obligatorio y con un máximo razonable; solo se quitan los espacios de los extremos, el resto se guarda tal cual. */
    private String validarTexto(String texto, String campo) {
        if (texto == null || texto.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Completa " + campo);
        }
        String limpio = texto.strip();
        if (limpio.length() > MAX_TEXTO) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "El texto de " + campo + " no puede superar los " + MAX_TEXTO + " caracteres");
        }
        // El PDF debe contener el texto completo: se rechazan los caracteres que no se pueden dibujar.
        String noSoportados = informePdfServicio.caracteresNoSoportados(limpio);
        if (!noSoportados.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "El texto de " + campo + " tiene caracteres que no se pueden incluir en el PDF: " + noSoportados);
        }
        return limpio;
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
                idArchivoPdf,
                evaluacion.getSimulacion().getUsuario().getNombreCompleto(),
                evaluacion.getClaridadRespuestas(),
                evaluacion.getDominioTema(),
                evaluacion.getOportunidadesMejora(),
                evaluacion.getRecomendaciones()
        );
    }

    /**
     * El evaluador solo puede corregir sus propias evaluaciones; el autor y la simulación no cambian.
     * El PDF se vuelve a generar para que siempre coincida con lo guardado.
     */
    @Transactional
    public EvaluacionSimulacion actualizarPropia(Integer id, EvaluacionSimulacion datos, String username) {
        EvaluacionSimulacion evaluacion = buscarPropia(id, username);
        validarRubrica(datos);
        evaluacion.setSeguridadCalificacion(datos.getSeguridadCalificacion());
        evaluacion.setClaridadRespuestas(datos.getClaridadRespuestas());
        evaluacion.setDominioTema(datos.getDominioTema());
        evaluacion.setOportunidadesMejora(datos.getOportunidadesMejora());
        evaluacion.setRecomendaciones(datos.getRecomendaciones());
        evaluacion.setPuntajeGeneral(calcularPuntajeGeneral(evaluacion));
        EvaluacionSimulacion guardada = evaluacionSimulacionRepositorio.save(evaluacion);
        guardarPdf(guardada);
        return guardada;
    }

    /**
     * Si username es null (administrador) puede borrar cualquiera; si no, solo las propias.
     * La entrevista vuelve a la cola de pendientes.
     */
    @Transactional
    public void eliminar(Integer id, String username) {
        EvaluacionSimulacion evaluacion = username == null
                ? evaluacionSimulacionRepositorio.findById(id)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No existe la evaluacion con id " + id))
                : buscarPropia(id, username);
        Simulacion simulacion = evaluacion.getSimulacion();
        // El PDF de retroalimentación pertenece a la evaluación: se elimina con ella.
        archivoSimulacionServicio.eliminarPdfsEvaluacion(simulacion.getIdSimulacion());
        evaluacionSimulacionRepositorio.delete(evaluacion);
        if (ESTADO_EVALUADA.equalsIgnoreCase(simulacion.getEstado())) {
            simulacion.setEstado(ESTADO_FINALIZADA);
            simulacionRepositorio.save(simulacion);
        }
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
