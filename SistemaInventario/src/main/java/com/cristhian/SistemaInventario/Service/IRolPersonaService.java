package com.cristhian.SistemaInventario.Service;

import com.cristhian.SistemaInventario.DTO.RolPersonaDTO;
import com.cristhian.SistemaInventario.Modelo.PersonaRol;
import com.cristhian.SistemaInventario.Modelo.RolPersona;

import java.util.List;
import java.util.Optional;

public interface IRolPersonaService {

    public List<RolPersona> listarRolPersonaActivo();
    public Optional<RolPersona> buscarRolPersonaId(int id);
    public RolPersona guardarRolPersona(RolPersonaDTO rolPersonaDTO);
    public RolPersona actualizarRolPersona(int id, RolPersonaDTO rolPersonaDTO);
    public void eliminarRolPersona(int id);

}
