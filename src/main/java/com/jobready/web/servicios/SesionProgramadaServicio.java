package com.jobready.web.servicios;

import com.jobready.web.entidades.SesionProgramada;
import com.jobready.web.repositorios.SesionProgramadaRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.io.Serializable;
import java.util.List;

@Service
public class SesionProgramadaServicio implements Serializable {
    @Autowired
    private SesionProgramadaRepositorio sesionProgramadaRepositorio;

    public SesionProgramada insertar(SesionProgramada sesionProgramada) {
        return sesionProgramadaRepositorio.save(sesionProgramada);
    }

    public List<SesionProgramada> listar() {
        return sesionProgramadaRepositorio.findAll();
    }

    public SesionProgramada actualizar(Integer id, SesionProgramada datos) {
        SesionProgramada sesion = sesionProgramadaRepositorio.findById(id)
                .orElseThrow(() -> new RuntimeException("No existe la sesion con id " + id));
        sesion.setUsuario(datos.getUsuario());
        sesion.setCarrera(datos.getCarrera());
        sesion.setFechaProgramada(datos.getFechaProgramada());
        sesion.setHoraProgramada(datos.getHoraProgramada());
        sesion.setEstado(datos.getEstado());
        sesion.setRecordatorioEnviado(datos.getRecordatorioEnviado());
        return sesionProgramadaRepositorio.save(sesion);
    }

    public SesionProgramada cancelar(Integer id) {
        SesionProgramada sesion = sesionProgramadaRepositorio.findById(id)
                .orElseThrow(() -> new RuntimeException("No existe la sesion con id " + id));
        sesion.setEstado("cancelada");
        return sesionProgramadaRepositorio.save(sesion);
    }
}


