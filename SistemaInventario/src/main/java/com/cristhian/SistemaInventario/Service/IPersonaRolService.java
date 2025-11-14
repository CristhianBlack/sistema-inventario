package com.cristhian.SistemaInventario.Service;

import com.cristhian.SistemaInventario.Modelo.PersonaRol;
import com.cristhian.SistemaInventario.Modelo.RolPersona;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface IPersonaRolService {

    List<PersonaRol> findByPersona_IdPersona(Integer idPersona);

    public List<RolPersona> obtenerRolesPorPersona(Integer idPersona);

    public PersonaRol asignarRol(Integer idPersona, Integer idRol);

    public void eliminarRolPersona(Integer idPersonaRol);
}