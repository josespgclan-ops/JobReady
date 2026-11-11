package com.jobready.web.repositorios;

import com.jobready.web.entidades.EncuestaSatisfaccion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface EncuestaSatisfaccionRepositorio extends JpaRepository<EncuestaSatisfaccion, Integer> {
    List<EncuestaSatisfaccion> findByUsuario_UsernameOrderByFechaEnvioDesc(String username);
}
