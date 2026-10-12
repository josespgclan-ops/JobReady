package com.jobready.web.dtos;

import com.jobready.web.entidades.*;
import lombok.*;
import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ArchivoSimulacionDTO {
    private Integer idSimulacion;
    private String tipoArchivo;
    private String urlAlmacenamiento;
    private BigDecimal pesoMb;

    public ArchivoSimulacion convertirAEntidad() {
        Simulacion simulacion = new Simulacion(); simulacion.setIdSimulacion(idSimulacion);
        ArchivoSimulacion archivo = new ArchivoSimulacion();
        archivo.setSimulacion(simulacion);
        archivo.setTipoArchivo(tipoArchivo);
        archivo.setUrlAlmacenamiento(urlAlmacenamiento);
        archivo.setPesoMb(pesoMb);
        return archivo;
    }
}
