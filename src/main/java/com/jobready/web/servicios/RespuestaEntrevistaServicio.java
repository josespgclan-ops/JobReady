package com.jobready.web.servicios;

import com.jobready.web.entidades.RespuestaEntrevista;
import com.jobready.web.repositorios.RespuestaEntrevistaRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.io.Serializable;
import java.util.List;

@Service
public class RespuestaEntrevistaServicio implements Serializable {
    @Autowired
    private RespuestaEntrevistaRepositorio respuestaEntrevistaRepositorio;

    public RespuestaEntrevista insertar(RespuestaEntrevista respuesta) {
        return respuestaEntrevistaRepositorio.save(respuesta);
    }

    public List<RespuestaEntrevista> listar() {
        return respuestaEntrevistaRepositorio.findAll();
    }

    public List<RespuestaEntrevista> listarPorSimulacion(Integer idSimulacion) {
        return respuestaEntrevistaRepositorio.findBySimulacion_IdSimulacion(idSimulacion);
    }

    public RespuestaEntrevista actualizar(Integer id, RespuestaEntrevista datos) {
        RespuestaEntrevista respuesta = respuestaEntrevistaRepositorio.findById(id)
                .orElseThrow(() -> new RuntimeException("No existe la respuesta con id " + id));
        respuesta.setSimulacion(datos.getSimulacion());
        respuesta.setPregunta(datos.getPregunta());
        respuesta.setArchivo(datos.getArchivo());
        respuesta.setTextoRespuesta(datos.getTextoRespuesta());
        respuesta.setDuracionSegundos(datos.getDuracionSegundos());
        respuesta.setEstado(datos.getEstado());
        return respuestaEntrevistaRepositorio.save(respuesta);
    }

    public void eliminar(Integer id) {
        if (!respuestaEntrevistaRepositorio.existsById(id)) {
            throw new RuntimeException("No existe la respuesta con id " + id);
        }
        respuestaEntrevistaRepositorio.deleteById(id);
    }
}
