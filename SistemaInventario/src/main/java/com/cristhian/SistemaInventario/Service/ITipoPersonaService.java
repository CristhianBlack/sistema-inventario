package com.cristhian.SistemaInventario.Service;


import com.cristhian.SistemaInventario.DTO.TipoPersonaDTO;
import com.cristhian.SistemaInventario.Modelo.TipoPersona;
import com.cristhian.SistemaInventario.Repositorio.TipoPersonaRepository;

import java.util.List;
import java.util.Optional;

public interface ITipoPersonaService {

    List<TipoPersona> listarTipoPersonaActiva();
    Optional<TipoPersona> buscarTipoPersonaId(int id);
    public void guardarTipoPersonaPorDefecto();
    public void desactivarTipoDocumento(int id);
}
