package com.jobready.web.controladores;

import com.jobready.web.entidades.Simulacion;
import com.jobready.web.dtos.EnvioEntrevistaDTO;
import com.jobready.web.dtos.SimulacionDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.multipart.MultipartFile;
import com.jobready.web.servicios.SimulacionServicio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import java.util.List;

@RestController
@RequestMapping("/api")
public class SimulacionController {
    @Autowired
    private SimulacionServicio simulacionServicio;

    @PostMapping("/simulacion")
    @PreAuthorize("hasRole('CANDIDATO')")
    public Simulacion insertar(@RequestBody SimulacionDTO simulacionDTO) {
        return simulacionServicio.insertar(simulacionDTO.convertirAEntidad());
    }

    @GetMapping("/simulaciones")
    @PreAuthorize("hasAnyRole('ADMIN', 'EVALUADOR')") // el candidato usa /simulaciones/mias
    public List<Simulacion> listar() {
        return simulacionServicio.listar();
    }

    /** US15 / US24: prácticas terminadas que esperan evaluación. */
    @GetMapping("/simulaciones/pendientes")
    @PreAuthorize("hasAnyRole('ADMIN', 'EVALUADOR')")
    public List<Simulacion> listarPendientes(Authentication authentication) {
        boolean esAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> "ROLE_ADMIN".equals(a.getAuthority()));
        // El administrador ve toda la cola; cada evaluador, las asignadas a él y las sin asignar.
        return esAdmin
                ? simulacionServicio.listarPendientesDeEvaluacion()
                : simulacionServicio.listarPendientesDeEvaluacionPara(authentication.getName());
    }

    /** Historial del candidato: solo sus propias simulaciones, tomando el usuario del token JWT. */
    @GetMapping("/simulaciones/mias")
    @PreAuthorize("hasRole('CANDIDATO')")
    public List<Simulacion> listarMias(Authentication authentication) {
        return simulacionServicio.listarPorUsuario(authentication.getName());
    }

    /** El candidato envía la grabación de su entrevista para que un evaluador la revise. */
    @PostMapping(value = "/simulacion/enviar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('CANDIDATO')")
    @ResponseStatus(HttpStatus.CREATED)
    public EnvioEntrevistaDTO enviar(@RequestParam("video") MultipartFile video,
                                     @RequestParam("idCarrera") Integer idCarrera,
                                     @RequestParam("duracionSegundos") Integer duracionSegundos,
                                     Authentication authentication) {
        return simulacionServicio.enviarEntrevista(authentication.getName(), idCarrera, duracionSegundos, video);
    }

    @PutMapping("/simulacion/{id}")
    @PreAuthorize("hasRole('CANDIDATO')")
    public Simulacion actualizar(@PathVariable Integer id, @RequestBody SimulacionDTO simulacionDTO) {
        return simulacionServicio.actualizar(id, simulacionDTO.convertirAEntidad());
    }

    @DeleteMapping("/simulacion/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'CANDIDATO')")
    public void eliminar(@PathVariable Integer id) {
        simulacionServicio.eliminar(id);
    }
}
