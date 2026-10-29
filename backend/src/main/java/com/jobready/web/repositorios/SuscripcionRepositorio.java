package com.jobready.web.repositorios;

import com.jobready.web.entidades.Suscripcion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SuscripcionRepositorio extends JpaRepository<Suscripcion, Integer> {
}


