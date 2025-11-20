package com.cristhian.SistemaInventario.Service;

import com.cristhian.SistemaInventario.DTO.CiudadDTO;
import com.cristhian.SistemaInventario.Modelo.Ciudad;

import java.util.List;
import java.util.Optional;

public interface ICiudadService {

    public List<Ciudad> listarCiudadesActivas();
    public Optional<Ciudad> buscarCiudadId(int id);
    public Ciudad guardarCiudad(CiudadDTO ciudadDTO);
    public Ciudad actualizarCiudad(int id, CiudadDTO ciudadDTO);
    public void eliminarCiudad(int id);
    public boolean existByCiudad(String ciudad);

}
