package com.jobready.web.repositorios;

import com.jobready.web.entidades.EvaluacionSimulacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EvaluacionSimulacionRepositorio extends JpaRepository<EvaluacionSimulacion, Integer> {
    EvaluacionSimulacion findBySimulacion_IdSimulacion(Integer idSimulacion);
}


