package com.cristhian.SistemaInventario.Service;

import com.cristhian.SistemaInventario.Modelo.Ciudad;

import java.util.List;
import java.util.Optional;

public interface ICiudadService {

    public List<Ciudad> listarCiudadesActivas();
    public Optional<Ciudad> buscarCiudadId(int id);
    public Ciudad guardarCiudad(Ciudad ciudad);
    public void eliminarCiudad(int id);
    public boolean existByCiudad(String ciudad);
    Optional<Ciudad> findByCiudadIgnoreCase(String ciudad);
}
