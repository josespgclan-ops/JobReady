package com.jobready.web.repositorios;

import com.jobready.web.entidades.Carrera;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CarreraRepositorio extends JpaRepository<Carrera, Integer> {
}


