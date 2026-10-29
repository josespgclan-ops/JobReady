package com.jobready.web.repositorios;

import com.jobready.web.entidades.SesionProgramada;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SesionProgramadaRepositorio extends JpaRepository<SesionProgramada, Integer> {
}


