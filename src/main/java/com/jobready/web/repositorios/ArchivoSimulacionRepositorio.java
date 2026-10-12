package com.jobready.web.repositorios;

import com.jobready.web.entidades.ArchivoSimulacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ArchivoSimulacionRepositorio extends JpaRepository<ArchivoSimulacion, Integer> {
}


