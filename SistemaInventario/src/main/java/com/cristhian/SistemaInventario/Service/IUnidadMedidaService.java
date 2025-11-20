package com.cristhian.SistemaInventario.Service;

import com.cristhian.SistemaInventario.DTO.UnidadMedidaDTO;
import com.cristhian.SistemaInventario.Modelo.UnidadMedida;

import java.util.List;
import java.util.Optional;

public interface IUnidadMedidaService {

    public List<UnidadMedida> listarUnidades();

    public Optional<UnidadMedida> buscarUnidadId(int id);

    public UnidadMedida guardar(UnidadMedidaDTO unidadMedidaDto);
    public UnidadMedida actualizarUnidadeMedida(int id, UnidadMedidaDTO dto);

    public void borrar(int id);

    //public boolean existsOtherWithSameName(int id, String nombreMedida);


}
