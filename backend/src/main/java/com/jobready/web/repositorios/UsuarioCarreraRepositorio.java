package com.jobready.web.repositorios;

import com.jobready.web.entidades.UsuarioCarrera;
import com.jobready.web.entidades.UsuarioCarreraId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface UsuarioCarreraRepositorio extends JpaRepository<UsuarioCarrera, UsuarioCarreraId> {
    List<UsuarioCarrera> findByUsuario_UsernameOrderByFechaSeleccionDesc(String username);
}
