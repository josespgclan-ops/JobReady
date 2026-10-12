package com.jobready.web.dtos;

import com.jobready.web.entidades.Plan;
import lombok.*;
import java.math.BigDecimal;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class PlanDTO {
    private String nombre;
    private BigDecimal precioMensual;
    private Integer limiteSesiones;
    private Boolean permiteAnalisisCorporal;
    private Boolean permiteAnalisisTonal;
    private Boolean permiteFeedbackHumano;

    public Plan convertirAEntidad() {
        Plan plan = new Plan();
        plan.setNombre(nombre);
        plan.setPrecioMensual(precioMensual);
        plan.setLimiteSesiones(limiteSesiones);
        plan.setPermiteAnalisisCorporal(permiteAnalisisCorporal);
        plan.setPermiteAnalisisTonal(permiteAnalisisTonal);
        plan.setPermiteFeedbackHumano(permiteFeedbackHumano);
        return plan;
    }
}
