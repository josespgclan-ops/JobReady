package com.jobready.web.repositorios;

import com.jobready.web.entidades.PreguntaEntrevista;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PreguntaEntrevistaRepositorio extends JpaRepository<PreguntaEntrevista, Integer> {
    List<PreguntaEntrevista> findByCarrera_IdCarreraAndActivoTrue(Integer idCarrera);
}
