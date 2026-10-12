package com.jobready.web.servicios;

import com.jobready.web.entidades.Carrera;
import com.jobready.web.repositorios.CarreraRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.io.Serializable;
import java.util.List;

@Service
public class CarreraServicio implements Serializable {
    @Autowired
    private CarreraRepositorio carreraRepositorio;

    public Carrera insertar(Carrera carrera) {
        return carreraRepositorio.save(carrera);
    }

    public List<Carrera> listar() {
        return carreraRepositorio.findAll();
    }

    public Carrera actualizar(Integer id, Carrera datos) {
        Carrera carrera = carreraRepositorio.findById(id)
                .orElseThrow(() -> new RuntimeException("No existe la carrera con id " + id));
        carrera.setNombre(datos.getNombre());
        carrera.setDescripcion(datos.getDescripcion());
        carrera.setActivo(datos.getActivo());
        return carreraRepositorio.save(carrera);
    }

    public Carrera desactivar(Integer id) {
        Carrera carrera = carreraRepositorio.findById(id)
                .orElseThrow(() -> new RuntimeException("No existe la carrera con id " + id));
        carrera.setActivo(false);
        return carreraRepositorio.save(carrera);
    }
}


