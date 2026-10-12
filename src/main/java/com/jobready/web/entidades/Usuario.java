package com.jobready.web.entidades;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "usuarios")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_usuario")
    private Integer idUsuario;
    @ManyToOne
    @JoinColumn(name = "id_rol", nullable = false)
    private Rol rol;
    @Column(name = "nombre_completo", nullable = false, length = 120)
    private String nombreCompleto;
    @Column(nullable = false, unique = true, length = 120)
    private String email;
    @Column(nullable = false, unique = true, length = 50)
    private String username;
    @Column(name = "password_hash", nullable = false, length = 255)
    private String passwordHash;
    @Column(length = 20)
    private String telefono;
    @Column(name = "nivel_actual", precision = 5, scale = 2)
    private BigDecimal nivelActual;
    @Column(name = "fecha_registro")
    private LocalDateTime fechaRegistro;
    @Column(length = 20)
    private String estado;
}


