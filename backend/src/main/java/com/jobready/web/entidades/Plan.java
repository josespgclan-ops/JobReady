package com.jobready.web.entidades;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(name = "planes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Plan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_plan")
    private Integer idPlan;
    @Column(nullable = false, length = 50)
    private String nombre;
    @Column(name = "precio_mensual", nullable = false, precision = 8, scale = 2)
    private BigDecimal precioMensual;
    @Column(name = "limite_sesiones")
    private Integer limiteSesiones;
    @Column(name = "permite_analisis_corporal")
    private Boolean permiteAnalisisCorporal;
    @Column(name = "permite_analisis_tonal")
    private Boolean permiteAnalisisTonal;
    @Column(name = "permite_feedback_humano")
    private Boolean permiteFeedbackHumano;
}


