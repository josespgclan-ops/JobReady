package com.jobready.web.entidades;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "archivos_simulacion")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ArchivoSimulacion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_archivo")
    private Integer idArchivo;
    @ManyToOne
    @JoinColumn(name = "id_simulacion", nullable = false)
    private Simulacion simulacion;
    @Column(name = "tipo_archivo", nullable = false, length = 30)
    private String tipoArchivo;
    @Column(name = "url_almacenamiento", nullable = false, length = 255)
    private String urlAlmacenamiento;
    @Column(name = "peso_mb", precision = 6, scale = 2)
    private BigDecimal pesoMb;
    @Column(name = "fecha_creacion")
    private LocalDateTime fechaCreacion;
}


