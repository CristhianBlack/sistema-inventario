package com.cristhian.SistemaInventario.Service;

import com.cristhian.SistemaInventario.DTO.PersonaDTO;
import com.cristhian.SistemaInventario.Modelo.Persona;

import java.util.List;

public interface IPersonaService {
  /*  public List<Persona> listarPersonasActivas();
    public Optional<Persona> buscarPersonaID(int id);
    public Persona guardarPersona(Persona persona);
    public void eliminarPersona(int id);
    public boolean existsPersonaByDocumento(String documentoPersona);
    Optional<Persona> findByPersonaIgnoreCase(String nombre);*/

    Persona crearPersonaConRolDefault(PersonaDTO personaDto);

    Persona actualizarPersona(Integer id, PersonaDTO personaDTO);

    List<Persona> listarPersonas();

    Persona buscarPorId(Integer id);

    void eliminarPersona(Integer id);




}
