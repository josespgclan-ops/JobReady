package com.jobready.web.servicios;

import com.jobready.web.entidades.ArchivoSimulacion;
import com.jobready.web.entidades.ArchivoSimulacionContenido;
import com.jobready.web.entidades.Simulacion;
import com.jobready.web.repositorios.ArchivoSimulacionContenidoRepositorio;
import com.jobready.web.repositorios.ArchivoSimulacionRepositorio;
import com.jobready.web.repositorios.SimulacionRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import java.io.IOException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.HexFormat;
import java.util.List;

@Service
public class ArchivoSimulacionServicio implements Serializable {
    public static final String TIPO_VIDEO = "video";
    public static final String TIPO_PDF_EVALUACION = "pdf_evaluacion";
    private static final long MAX_VIDEO_BYTES = 100L * 1024 * 1024;
    private static final long MAX_PDF_BYTES = 10L * 1024 * 1024;

    @Autowired
    private ArchivoSimulacionRepositorio archivoSimulacionRepositorio;
    @Autowired
    private ArchivoSimulacionContenidoRepositorio contenidoRepositorio;
    @Autowired
    private SimulacionRepositorio simulacionRepositorio;

    public ArchivoSimulacion insertar(ArchivoSimulacion archivoSimulacion) {
        return archivoSimulacionRepositorio.save(archivoSimulacion);
    }

    public List<ArchivoSimulacion> listar() {
        return archivoSimulacionRepositorio.findAll();
    }

    public void eliminar(Integer id) {
        if (!archivoSimulacionRepositorio.existsById(id)) {
            throw new RuntimeException("No existe el archivo con id " + id);
        }
        archivoSimulacionRepositorio.deleteById(id);
    }

    /** Lee y valida el video que envía el candidato; devuelve sus bytes. */
    public byte[] leerVideo(MultipartFile video) {
        if (video == null || video.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No se recibió ningún video");
        }
        String tipo = video.getContentType();
        if (tipo == null || !tipo.toLowerCase().startsWith("video/")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El archivo enviado no es un video");
        }
        if (video.getSize() > MAX_VIDEO_BYTES) {
            throw new ResponseStatusException(HttpStatus.PAYLOAD_TOO_LARGE, "El video supera los 100 MB permitidos");
        }
        return leerBytes(video);
    }

    /** Lee y valida el PDF con las indicaciones de evaluación. */
    public byte[] leerPdf(MultipartFile pdf) {
        if (pdf == null || pdf.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El PDF está vacío");
        }
        if (pdf.getSize() > MAX_PDF_BYTES) {
            throw new ResponseStatusException(HttpStatus.PAYLOAD_TOO_LARGE, "El PDF supera los 10 MB permitidos");
        }
        byte[] bytes = leerBytes(pdf);
        // Se comprueba la firma del archivo, no solo la extensión o el tipo declarado por el navegador.
        if (bytes.length < 5 || !new String(bytes, 0, 5, java.nio.charset.StandardCharsets.US_ASCII).equals("%PDF-")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El archivo adjunto no es un PDF válido");
        }
        return bytes;
    }

    public String calcularHash(byte[] bytes) {
        try {
            return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(bytes));
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 no disponible", e);
        }
    }

    public boolean existeVideo(String hash) {
        return archivoSimulacionRepositorio.existsByHashContenido(hash);
    }

    /** Guarda metadatos y contenido de un archivo asociado a la simulación. */
    @Transactional
    public ArchivoSimulacion guardar(Simulacion simulacion, String tipoArchivo, String tipoContenido,
                                     byte[] bytes, String hash) {
        ArchivoSimulacion archivo = new ArchivoSimulacion();
        archivo.setSimulacion(simulacion);
        archivo.setTipoArchivo(tipoArchivo);
        archivo.setUrlAlmacenamiento("pendiente");
        archivo.setPesoMb(BigDecimal.valueOf(bytes.length)
                .divide(BigDecimal.valueOf(1024L * 1024L), 2, RoundingMode.HALF_UP));
        archivo.setFechaCreacion(LocalDateTime.now());
        archivo.setHashContenido(hash);
        archivo = archivoSimulacionRepositorio.saveAndFlush(archivo);
        archivo.setUrlAlmacenamiento("/api/archivo-simulacion/" + archivo.getIdArchivo() + "/contenido");

        ArchivoSimulacionContenido contenido = new ArchivoSimulacionContenido();
        contenido.setArchivo(archivo);
        contenido.setTipoContenido(tipoContenido);
        contenido.setContenido(bytes);
        contenidoRepositorio.save(contenido);
        return archivo;
    }

    public void eliminarPdfsEvaluacion(Integer idSimulacion) {
        archivoSimulacionRepositorio.deleteAll(
                archivoSimulacionRepositorio.findBySimulacion_IdSimulacionAndTipoArchivo(idSimulacion, TIPO_PDF_EVALUACION));
    }

    /** Archivos de una simulación (metadatos), respetando quién puede verlos. */
    public List<ArchivoSimulacion> listarPorSimulacion(Integer idSimulacion, Authentication authentication) {
        Simulacion simulacion = simulacionRepositorio.findById(idSimulacion)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No existe la simulación"));
        verificarAcceso(simulacion, authentication);
        return archivoSimulacionRepositorio.findBySimulacion_IdSimulacionOrderByFechaCreacionAsc(idSimulacion);
    }

    @Transactional(readOnly = true)
    public ArchivoSimulacionContenido obtenerContenido(Integer idArchivo, Authentication authentication) {
        ArchivoSimulacion archivo = archivoSimulacionRepositorio.findById(idArchivo)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No existe el archivo"));
        verificarAcceso(archivo.getSimulacion(), authentication);
        return contenidoRepositorio.findById(idArchivo)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "El archivo no tiene contenido almacenado"));
    }

    /** Administrador y evaluadores ven cualquier práctica; el candidato solo las suyas. */
    private void verificarAcceso(Simulacion simulacion, Authentication authentication) {
        boolean revisor = authentication.getAuthorities().stream()
                .anyMatch(a -> "ROLE_ADMIN".equals(a.getAuthority()) || "ROLE_EVALUADOR".equals(a.getAuthority()));
        if (revisor) return;
        if (!simulacion.getUsuario().getUsername().equals(authentication.getName())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "El archivo pertenece a otro usuario");
        }
    }

    private byte[] leerBytes(MultipartFile archivo) {
        try {
            return archivo.getBytes();
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No se pudo leer el archivo enviado");
        }
    }
}
