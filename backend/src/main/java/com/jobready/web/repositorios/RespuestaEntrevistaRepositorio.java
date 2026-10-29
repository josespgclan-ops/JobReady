package com.jobready.web.repositorios;

import com.jobready.web.entidades.RespuestaEntrevista;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface RespuestaEntrevistaRepositorio extends JpaRepository<RespuestaEntrevista, Integer> {
    List<RespuestaEntrevista> findBySimulacion_IdSimulacion(Integer idSimulacion);
}
