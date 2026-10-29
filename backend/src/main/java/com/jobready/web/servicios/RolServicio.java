package com.jobready.web.servicios;

import com.jobready.web.entidades.Rol;
import com.jobready.web.repositorios.RolRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.io.Serializable;
import java.util.List;

@Service
public class RolServicio implements Serializable {
    @Autowired
    private RolRepositorio rolRepositorio;

    public Rol insertar(Rol rol) {
        return rolRepositorio.save(rol);
    }

    public List<Rol> listar() {
        return rolRepositorio.findAll();
    }

    public Rol actualizar(Integer id, Rol datos) {
        Rol rol = rolRepositorio.findById(id)
                .orElseThrow(() -> new RuntimeException("No existe el rol con id " + id));
        rol.setNombre(datos.getNombre());
        rol.setDescripcion(datos.getDescripcion());
        return rolRepositorio.save(rol);
    }
}


