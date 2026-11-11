package com.jobready.web.servicios;

import com.jobready.web.dtos.EnvioEntrevistaDTO;
import com.jobready.web.entidades.ArchivoSimulacion;
import com.jobready.web.entidades.Carrera;
import com.jobready.web.entidades.EvaluacionSimulacion;
import com.jobready.web.entidades.Simulacion;
import com.jobready.web.entidades.Usuario;
import com.jobready.web.repositorios.ArchivoSimulacionRepositorio;
import com.jobready.web.repositorios.CarreraRepositorio;
import com.jobready.web.repositorios.EvaluacionSimulacionRepositorio;
import com.jobready.web.repositorios.RespuestaEntrevistaRepositorio;
import com.jobready.web.repositorios.SimulacionRepositorio;
import com.jobready.web.repositorios.UsuarioRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class SimulacionServicio implements Serializable {
    private static final int DURACION_MAXIMA_SEGUNDOS = 180;

    @Autowired
    private SimulacionRepositorio simulacionRepositorio;
    @Autowired
    private UsuarioRepositorio usuarioRepositorio;
    @Autowired
    private CarreraRepositorio carreraRepositorio;
    @Autowired
    private ArchivoSimulacionRepositorio archivoSimulacionRepositorio;
    @Autowired
    private EvaluacionSimulacionRepositorio evaluacionSimulacionRepositorio;
    @Autowired
    private ArchivoSimulacionServicio archivoSimulacionServicio;
    @Autowired
    private RespuestaEntrevistaRepositorio respuestaEntrevistaRepositorio;

    public Simulacion insertar(Simulacion simulacion) {
        return simulacionRepositorio.save(simulacion);
    }

    public List<Simulacion> listar() {
        return simulacionRepositorio.findAll();
    }

    public List<Simulacion> listarPendientesDeEvaluacion() {
        return simulacionRepositorio.findPendientesDeEvaluacion();
    }

    /** Cola del evaluador conectado: sus entrevistas asignadas y las que no tienen evaluador. */
    public List<Simulacion> listarPendientesDeEvaluacionPara(String username) {
        return simulacionRepositorio.findPendientesDeEvaluacionPara(username);
    }

    /**
     * El candidato envía la grabación de su entrevista. Se crea la simulación finalizada,
     * se guarda el video en la BD y se asigna el evaluador con menos revisiones pendientes,
     * de modo que la práctica aparece en su cola. La misma grabación no se acepta dos veces.
     */
    @Transactional
    public EnvioEntrevistaDTO enviarEntrevista(String username, Integer idCarrera, Integer duracionSegundos,
                                               MultipartFile video) {
        Usuario candidato = usuarioRepositorio.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuario no encontrado"));
        if (idCarrera == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Selecciona la carrera de la entrevista");
        }
        Carrera carrera = carreraRepositorio.findById(idCarrera)
                .filter(c -> !Boolean.FALSE.equals(c.getActivo()))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "La carrera seleccionada no existe"));
        if (duracionSegundos == null || duracionSegundos < 1 || duracionSegundos > DURACION_MAXIMA_SEGUNDOS + 5) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La duración del video no es válida");
        }

        byte[] bytes = archivoSimulacionServicio.leerVideo(video);
        String hash = archivoSimulacionServicio.calcularHash(bytes);
        if (archivoSimulacionServicio.existeVideo(hash)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Este video ya fue enviado");
        }

        LocalDateTime ahora = LocalDateTime.now();
        Simulacion simulacion = new Simulacion();
        simulacion.setUsuario(candidato);
        simulacion.setCarrera(carrera);
        simulacion.setNumeroSimulacion((int) simulacionRepositorio
                .countByUsuario_IdUsuarioAndCarrera_IdCarrera(candidato.getIdUsuario(), carrera.getIdCarrera()) + 1);
        simulacion.setFechaInicio(ahora.minusSeconds(duracionSegundos));
        simulacion.setFechaFin(ahora);
        simulacion.setDuracionSegundos(duracionSegundos);
        simulacion.setEstado("finalizada");
        simulacion.setEvaluador(elegirEvaluador());
        simulacion = simulacionRepositorio.save(simulacion);

        ArchivoSimulacion archivo;
        try {
            archivo = archivoSimulacionServicio.guardar(simulacion, ArchivoSimulacionServicio.TIPO_VIDEO,
                    video.getContentType(), bytes, hash);
        } catch (DataIntegrityViolationException e) {
            // Dos envíos simultáneos del mismo video: el índice único sobre el hash rechaza el segundo.
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Este video ya fue enviado");
        }
        return aEnvio(simulacion, archivo, null, null);
    }

    /** Evaluador activo con menos entrevistas asignadas sin evaluar; null si no hay evaluadores activos. */
    private Usuario elegirEvaluador() {
        return usuarioRepositorio.findByRol_NombreIgnoreCaseAndEstadoIgnoreCaseOrderByIdUsuarioAsc("EVALUADOR", "activo")
                .stream()
                .min(Comparator.comparingLong(simulacionRepositorio::contarPendientesAsignadas))
                .orElse(null);
    }

    /** Historial del candidato: todas sus prácticas con su video y estado de revisión. */
    public List<EnvioEntrevistaDTO> listarEnviosPorUsuario(String username) {
        Map<Integer, ArchivoSimulacion> videos = archivoSimulacionRepositorio
                .findBySimulacion_Usuario_UsernameAndTipoArchivo(username, ArchivoSimulacionServicio.TIPO_VIDEO)
                .stream()
                .collect(Collectors.toMap(a -> a.getSimulacion().getIdSimulacion(), Function.identity(), (a, b) -> a));
        Map<Integer, ArchivoSimulacion> pdfs = archivoSimulacionRepositorio
                .findBySimulacion_Usuario_UsernameAndTipoArchivo(username, ArchivoSimulacionServicio.TIPO_PDF_EVALUACION)
                .stream()
                // Si hubiera más de uno, se ofrece el más reciente.
                .collect(Collectors.toMap(a -> a.getSimulacion().getIdSimulacion(), Function.identity(),
                        (a, b) -> a.getFechaCreacion().isAfter(b.getFechaCreacion()) ? a : b));
        Map<Integer, EvaluacionSimulacion> evaluaciones = evaluacionSimulacionRepositorio
                .findBySimulacion_Usuario_Username(username)
                .stream()
                .collect(Collectors.toMap(e -> e.getSimulacion().getIdSimulacion(), Function.identity(), (a, b) -> a));
        return simulacionRepositorio.findByUsuario_UsernameOrderByFechaInicioDesc(username).stream()
                .map(s -> aEnvio(s, videos.get(s.getIdSimulacion()), evaluaciones.get(s.getIdSimulacion()),
                        pdfs.get(s.getIdSimulacion())))
                .sorted(Comparator.comparing(EnvioEntrevistaDTO::getFechaEnvio,
                        Comparator.nullsLast(Comparator.reverseOrder())))
                .toList();
    }

    private EnvioEntrevistaDTO aEnvio(Simulacion s, ArchivoSimulacion video, EvaluacionSimulacion evaluacion,
                                      ArchivoSimulacion pdf) {
        String estadoRevision;
        if (evaluacion != null) estadoRevision = "REVISADA";
        else if ("finalizada".equalsIgnoreCase(s.getEstado())) estadoRevision = "PENDIENTE";
        else estadoRevision = "EN_CURSO";
        LocalDateTime fechaEnvio = video != null ? video.getFechaCreacion()
                : s.getFechaFin() != null ? s.getFechaFin() : s.getFechaInicio();
        return new EnvioEntrevistaDTO(
                s.getIdSimulacion(),
                s.getCarrera() != null ? s.getCarrera().getIdCarrera() : null,
                s.getCarrera() != null ? s.getCarrera().getNombre() : null,
                s.getNumeroSimulacion(),
                fechaEnvio,
                s.getDuracionSegundos(),
                s.getEstado(),
                estadoRevision,
                video != null ? video.getIdArchivo() : null,
                evaluacion != null ? evaluacion.getFechaEvaluacion() : null,
                evaluacion != null && pdf != null ? pdf.getIdArchivo() : null);
    }

    public List<Simulacion> listarPorUsuario(String username) {
        return simulacionRepositorio.findByUsuario_UsernameOrderByFechaInicioDesc(username);
    }

    public Simulacion actualizar(Integer id, Simulacion datos) {
        Simulacion simulacion = simulacionRepositorio.findById(id)
                .orElseThrow(() -> new RuntimeException("No existe la simulacion con id " + id));
        simulacion.setUsuario(datos.getUsuario());
        simulacion.setCarrera(datos.getCarrera());
        simulacion.setSesion(datos.getSesion());
        simulacion.setNumeroSimulacion(datos.getNumeroSimulacion());
        simulacion.setFechaInicio(datos.getFechaInicio());
        simulacion.setFechaFin(datos.getFechaFin());
        simulacion.setDuracionSegundos(datos.getDuracionSegundos());
        simulacion.setEstado(datos.getEstado());
        return simulacionRepositorio.save(simulacion);
    }

    /**
     * El administrador elimina una entrevista pendiente: borra sus videos (con su contenido),
     * sus respuestas y la práctica, de modo que desaparece del historial del candidato y de la
     * cola del evaluador. Las prácticas ya evaluadas no se eliminan por aquí.
     */
    @Transactional
    public void eliminarEnvioPendiente(Integer idSimulacion) {
        Simulacion simulacion = simulacionRepositorio.findById(idSimulacion)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "La entrevista ya no existe"));
        if (evaluacionSimulacionRepositorio.findBySimulacion_IdSimulacion(idSimulacion) != null) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "La entrevista ya fue evaluada; no se puede eliminar desde revisiones pendientes");
        }
        respuestaEntrevistaRepositorio.deleteAll(respuestaEntrevistaRepositorio.findBySimulacion_IdSimulacion(idSimulacion));
        archivoSimulacionRepositorio.deleteAll(
                archivoSimulacionRepositorio.findBySimulacion_IdSimulacionOrderByFechaCreacionAsc(idSimulacion));
        simulacionRepositorio.delete(simulacion);
    }

    public void eliminar(Integer id) {
        if (!simulacionRepositorio.existsById(id)) {
            throw new RuntimeException("No existe la simulacion con id " + id);
        }
        simulacionRepositorio.deleteById(id);
    }
}


