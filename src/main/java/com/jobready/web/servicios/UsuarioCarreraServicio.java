package com.jobready.web.servicios;

import com.jobready.web.entidades.UsuarioCarrera;
import com.jobready.web.repositorios.UsuarioCarreraRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.io.Serializable;
import java.util.List;

@Service
public class UsuarioCarreraServicio implements Serializable {
    @Autowired
    private UsuarioCarreraRepositorio usuarioCarreraRepositorio;

    public UsuarioCarrera insertar(UsuarioCarrera usuarioCarrera) {
        return usuarioCarreraRepositorio.save(usuarioCarrera);
    }

    public List<UsuarioCarrera> listar() {
        return usuarioCarreraRepositorio.findAll();
    }

    public void eliminar(Integer idUsuario, Integer idCarrera) {
        com.jobready.web.entidades.UsuarioCarreraId id =
                new com.jobready.web.entidades.UsuarioCarreraId(idUsuario, idCarrera);
        if (!usuarioCarreraRepositorio.existsById(id)) {
            throw new RuntimeException("No existe la relacion entre usuario y carrera");
        }
        usuarioCarreraRepositorio.deleteById(id);
    }
}


