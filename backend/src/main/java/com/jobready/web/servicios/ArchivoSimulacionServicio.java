package com.jobready.web.servicios;

import com.jobready.web.entidades.ArchivoSimulacion;
import com.jobready.web.repositorios.ArchivoSimulacionRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.io.Serializable;
import java.util.List;

@Service
public class ArchivoSimulacionServicio implements Serializable {
    @Autowired
    private ArchivoSimulacionRepositorio archivoSimulacionRepositorio;

    public ArchivoSimulacion insertar(ArchivoSimulacion archivoSimulacion) {
        return archivoSimulacionRepositorio.save(archivoSimulacion);
    }

    public List<ArchivoSimulacion> listar() {
        return archivoSimulacionRepositorio.findAll();
    }

    public void eliminar(Integer id) {
        if (!archivoSimulacionRepositorio.existsById(id)) {
            throw new RuntimeException("No existe el archivo con id " + id);
        }
        archivoSimulacionRepositorio.deleteById(id);
    }
}


