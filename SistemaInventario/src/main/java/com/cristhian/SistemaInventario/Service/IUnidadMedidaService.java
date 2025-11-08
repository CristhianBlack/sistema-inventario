package com.cristhian.SistemaInventario.Service;

import com.cristhian.SistemaInventario.Modelo.UnidadMedida;

import java.util.List;
import java.util.Optional;

public interface IUnidadMedidaService {

    public List<UnidadMedida> listarUnidades();

    public Optional<UnidadMedida> buscarUnidadId(int id);

    public UnidadMedida guardar(UnidadMedida unidadMedida);

    public void borrar(int id);

    public boolean existsByNombreMedida(String nombreMedida);

    public boolean existsOtherWithSameName(int id, String nombreMedida);

    public boolean existeOtraUnidadConMismoNombre(int id, String nombreMedida);
}
