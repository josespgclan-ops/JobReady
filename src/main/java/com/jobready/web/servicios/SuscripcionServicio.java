package com.jobready.web.servicios;

import com.jobready.web.entidades.Suscripcion;
import com.jobready.web.repositorios.SuscripcionRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.io.Serializable;
import java.util.List;

@Service
public class SuscripcionServicio implements Serializable {
    @Autowired
    private SuscripcionRepositorio suscripcionRepositorio;

    public Suscripcion insertar(Suscripcion suscripcion) {
        return suscripcionRepositorio.save(suscripcion);
    }

    public List<Suscripcion> listar() {
        return suscripcionRepositorio.findAll();
    }

    public Suscripcion actualizar(Integer id, Suscripcion datos) {
        Suscripcion suscripcion = suscripcionRepositorio.findById(id)
                .orElseThrow(() -> new RuntimeException("No existe la suscripcion con id " + id));
        suscripcion.setUsuario(datos.getUsuario());
        suscripcion.setPlan(datos.getPlan());
        suscripcion.setFechaInicio(datos.getFechaInicio());
        suscripcion.setFechaFin(datos.getFechaFin());
        suscripcion.setEstado(datos.getEstado());
        return suscripcionRepositorio.save(suscripcion);
    }

    public Suscripcion cancelar(Integer id) {
        Suscripcion suscripcion = suscripcionRepositorio.findById(id)
                .orElseThrow(() -> new RuntimeException("No existe la suscripcion con id " + id));
        suscripcion.setEstado("cancelada");
        return suscripcionRepositorio.save(suscripcion);
    }
}


