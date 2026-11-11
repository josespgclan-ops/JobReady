package com.jobready.web.entidades;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

/**
 * Bytes de un archivo de simulación (video del candidato o PDF del evaluador).
 * Se guarda aparte de {@link ArchivoSimulacion} para que listar metadatos no cargue el contenido.
 */
@Entity
@Table(name = "archivos_simulacion_contenido")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ArchivoSimulacionContenido {
    @Id
    @Column(name = "id_archivo")
    private Integer idArchivo;
    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "id_archivo")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private ArchivoSimulacion archivo;
    @Column(name = "tipo_contenido", nullable = false, length = 100)
    private String tipoContenido;
    @Column(name = "contenido", nullable = false)
    private byte[] contenido;
}
