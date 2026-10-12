package com.jobready.web.entidades;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "encuestas_satisfaccion")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EncuestaSatisfaccion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_encuesta")
    private Integer idEncuesta;
    @ManyToOne
    @JoinColumn(name = "id_usuario")
    private Usuario usuario;
    @Column(name = "satisfaccion_general", nullable = false)
    private Integer satisfaccionGeneral;
    @Column(name = "calificacion_servicio", nullable = false, length = 20)
    private String calificacionServicio;
    @Column(name = "calidad_producto", nullable = false, length = 20)
    private String calidadProducto;
    @Column(name = "comentario_adicional", columnDefinition = "TEXT")
    private String comentarioAdicional;
    @Column(name = "nombre_contacto", length = 100)
    private String nombreContacto;
    @Column(name = "correo_contacto", length = 120)
    private String correoContacto;
    @Column(name = "fecha_envio")
    private LocalDateTime fechaEnvio;
}


