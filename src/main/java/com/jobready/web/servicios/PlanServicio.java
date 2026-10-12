package com.jobready.web.servicios;

import com.jobready.web.entidades.Plan;
import com.jobready.web.repositorios.PlanRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.io.Serializable;
import java.util.List;

@Service
public class PlanServicio implements Serializable {
    @Autowired
    private PlanRepositorio planRepositorio;

    public Plan insertar(Plan plan) {
        return planRepositorio.save(plan);
    }

    public List<Plan> listar() {
        return planRepositorio.findAll();
    }

    public Plan actualizar(Integer id, Plan datos) {
        Plan plan = planRepositorio.findById(id)
                .orElseThrow(() -> new RuntimeException("No existe el plan con id " + id));
        plan.setNombre(datos.getNombre());
        plan.setPrecioMensual(datos.getPrecioMensual());
        plan.setLimiteSesiones(datos.getLimiteSesiones());
        plan.setPermiteAnalisisCorporal(datos.getPermiteAnalisisCorporal());
        plan.setPermiteAnalisisTonal(datos.getPermiteAnalisisTonal());
        plan.setPermiteFeedbackHumano(datos.getPermiteFeedbackHumano());
        return planRepositorio.save(plan);
    }
}


