package com.jobready.web.controladores;

import com.jobready.web.entidades.ArchivoSimulacion;
import com.jobready.web.entidades.ArchivoSimulacionContenido;
import com.jobready.web.dtos.ArchivoSimulacionDTO;
import com.jobready.web.servicios.ArchivoSimulacionServicio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.CacheControl;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;
import java.util.List;

@RestController
@RequestMapping("/api")
public class ArchivoSimulacionController {
    @Autowired
    private ArchivoSimulacionServicio archivoSimulacionServicio;

    @PostMapping("/archivo-simulacion")
    @PreAuthorize("hasAnyRole('CANDIDATO', 'EVALUADOR')")
    public ArchivoSimulacion insertar(@RequestBody ArchivoSimulacionDTO archivoSimulacionDTO) {
        return archivoSimulacionServicio.insertar(archivoSimulacionDTO.convertirAEntidad());
    }

    @GetMapping("/archivos-simulacion")
    @PreAuthorize("hasAnyRole('ADMIN', 'EVALUADOR')")
    public List<ArchivoSimulacion> listar() {
        return archivoSimulacionServicio.listar();
    }

    /** Archivos (video del candidato, PDF del evaluador) de una práctica. El candidato solo ve los suyos. */
    @GetMapping("/archivos-simulacion/simulacion/{idSimulacion}")
    @PreAuthorize("hasAnyRole('ADMIN', 'EVALUADOR', 'CANDIDATO')")
    public List<ArchivoSimulacion> listarPorSimulacion(@PathVariable Integer idSimulacion, Authentication authentication) {
        return archivoSimulacionServicio.listarPorSimulacion(idSimulacion, authentication);
    }

    /** Descarga el contenido almacenado en la BD. El candidato solo puede descargar archivos de sus prácticas. */
    @GetMapping("/archivo-simulacion/{id}/contenido")
    @PreAuthorize("hasAnyRole('ADMIN', 'EVALUADOR', 'CANDIDATO')")
    public ResponseEntity<byte[]> descargar(@PathVariable Integer id, Authentication authentication) {
        ArchivoSimulacionContenido contenido = archivoSimulacionServicio.obtenerContenido(id, authentication);
        boolean esPdf = MediaType.APPLICATION_PDF_VALUE.equals(contenido.getTipoContenido());
        String extension = esPdf ? ".pdf" : contenido.getTipoContenido().contains("mp4") ? ".mp4" : ".webm";
        String nombre = (esPdf ? "indicaciones-evaluacion-" : "entrevista-") + id + extension;
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contenido.getTipoContenido()))
                .header(HttpHeaders.CONTENT_DISPOSITION, ContentDisposition.inline().filename(nombre).build().toString())
                .cacheControl(CacheControl.noStore())
                .body(contenido.getContenido());
    }

    @DeleteMapping("/archivo-simulacion/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'CANDIDATO')")
    public void eliminar(@PathVariable Integer id) {
        archivoSimulacionServicio.eliminar(id);
    }
}
