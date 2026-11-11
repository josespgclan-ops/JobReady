package com.jobready.web.repositorios;

import com.jobready.web.entidades.Simulacion;
import com.jobready.web.entidades.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface SimulacionRepositorio extends JpaRepository<Simulacion, Integer> {
    List<Simulacion> findByUsuario_UsernameOrderByFechaInicioDesc(String username);

    /** Simulaciones terminadas que todavía no tienen evaluación (cola del evaluador). */
    @Query("select s from Simulacion s where lower(s.estado) = 'finalizada' "
            + "and not exists (select e from EvaluacionSimulacion e where e.simulacion = s) "
            + "order by s.fechaFin asc")
    List<Simulacion> findPendientesDeEvaluacion();

    /** Cola de un evaluador: las que tiene asignadas más las que no tienen evaluador asignado. */
    @Query("select s from Simulacion s left join s.evaluador ev where lower(s.estado) = 'finalizada' "
            + "and (ev is null or ev.username = :username) "
            + "and not exists (select e from EvaluacionSimulacion e where e.simulacion = s) "
            + "order by s.fechaFin asc")
    List<Simulacion> findPendientesDeEvaluacionPara(@Param("username") String username);

    /** Carga de trabajo de un evaluador: entrevistas asignadas que aún no evalúa. */
    @Query("select count(s) from Simulacion s where s.evaluador = :evaluador and lower(s.estado) = 'finalizada' "
            + "and not exists (select e from EvaluacionSimulacion e where e.simulacion = s)")
    long contarPendientesAsignadas(@Param("evaluador") Usuario evaluador);

    long countByUsuario_IdUsuarioAndCarrera_IdCarrera(Integer idUsuario, Integer idCarrera);
}


