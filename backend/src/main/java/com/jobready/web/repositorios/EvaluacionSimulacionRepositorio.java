package com.jobready.web.repositorios;

import com.jobready.web.entidades.EvaluacionSimulacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EvaluacionSimulacionRepositorio extends JpaRepository<EvaluacionSimulacion, Integer> {
}


