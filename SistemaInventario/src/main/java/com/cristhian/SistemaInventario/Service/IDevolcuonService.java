package com.cristhian.SistemaInventario.Service;

import com.cristhian.SistemaInventario.DTO.DevolucionDTO;
import com.cristhian.SistemaInventario.Modelo.Devolucion;

import java.util.List;
import java.util.Optional;

public interface IDevolcuonService {

    public Devolucion crearDevolucion(DevolucionDTO devolucionDTO);


    // Obtener todas las devoluciones
    public List<Devolucion> listarDevoluciones();

    // Obtener por id
    public Optional<Devolucion> buscarDevolucionPorId(int id);

    // Eliminar
    public void eliminarDevolucion(int id);
}
