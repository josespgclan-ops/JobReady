package com.jobready.web.repositorios;

import com.jobready.web.entidades.Plan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlanRepositorio extends JpaRepository<Plan, Integer> {
}


