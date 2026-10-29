package com.jobready.web.servicios;

import com.jobready.web.entidades.EncuestaSatisfaccion;
import com.jobready.web.repositorios.EncuestaSatisfaccionRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.io.Serializable;
import java.util.List;

@Service
public class EncuestaSatisfaccionServicio implements Serializable {
    @Autowired
    private EncuestaSatisfaccionRepositorio encuestaSatisfaccionRepositorio;

    public EncuestaSatisfaccion insertar(EncuestaSatisfaccion encuestaSatisfaccion) {
        return encuestaSatisfaccionRepositorio.save(encuestaSatisfaccion);
    }

    public List<EncuestaSatisfaccion> listar() {
        return encuestaSatisfaccionRepositorio.findAll();
    }
}


