package com.jobready.web.repositorios;

import com.jobready.web.entidades.Simulacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
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
}


