package com.jobready.web.repositorios;

import com.jobready.web.entidades.ArchivoSimulacionContenido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ArchivoSimulacionContenidoRepositorio extends JpaRepository<ArchivoSimulacionContenido, Integer> {
}
