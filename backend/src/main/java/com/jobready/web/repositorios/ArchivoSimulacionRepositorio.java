package com.jobready.web.repositorios;

import com.jobready.web.entidades.ArchivoSimulacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ArchivoSimulacionRepositorio extends JpaRepository<ArchivoSimulacion, Integer> {
    boolean existsByHashContenido(String hashContenido);

    List<ArchivoSimulacion> findBySimulacion_IdSimulacionOrderByFechaCreacionAsc(Integer idSimulacion);

    Optional<ArchivoSimulacion> findFirstBySimulacion_IdSimulacionAndTipoArchivoOrderByFechaCreacionDesc(
            Integer idSimulacion, String tipoArchivo);

    List<ArchivoSimulacion> findBySimulacion_Usuario_UsernameAndTipoArchivo(String username, String tipoArchivo);

    List<ArchivoSimulacion> findBySimulacion_IdSimulacionAndTipoArchivo(Integer idSimulacion, String tipoArchivo);
}
