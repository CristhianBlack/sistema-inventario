package com.cristhian.SistemaInventario.ServicioImplement;

import com.cristhian.SistemaInventario.Modelo.PersonaRol;
import com.cristhian.SistemaInventario.Modelo.RolPersona;
import com.cristhian.SistemaInventario.Repositorio.PersonaRepository;
import com.cristhian.SistemaInventario.Repositorio.PersonaRolRepository;
import com.cristhian.SistemaInventario.Repositorio.RolPersonaRepository;
import com.cristhian.SistemaInventario.Service.IPersonaRolService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
public class PersonaRolServiceImpl implements IPersonaRolService {

    private final PersonaRolRepository personaRolRepository;
    private final PersonaRepository personaRepository;
    private final RolPersonaRepository rolPersonaRepository;

    public PersonaRolServiceImpl(PersonaRolRepository personaRolRepository, PersonaRepository personaRepository, RolPersonaRepository rolPersonaRepository) {
        this.personaRolRepository = personaRolRepository;
        this.personaRepository = personaRepository;
        this.rolPersonaRepository = rolPersonaRepository;
    }

    @Override
    public List<PersonaRol> findByPersona_IdPersona(Integer idPersona) {
        return null;
    }



    @Override
    public List<RolPersona> obtenerRolesPorPersona(Integer idPersona) {
        return personaRolRepository.findRolesByPersonaId(idPersona);
    }

    @Override
    public PersonaRol asignarRol(Integer idPersona, Integer idRol) {
        var persona = personaRepository.findById(idPersona)
                .orElseThrow(() -> new RuntimeException("Persona no encontrada"));

        var rol = rolPersonaRepository.findById(idRol)
                .orElseThrow(() -> new RuntimeException("Rol no encontrado"));

        PersonaRol personaRol = new PersonaRol();
        personaRol.setPersona(persona);
        personaRol.setRolPersona(rol);
        personaRol.setFechaAsignacion(LocalDate.now());
        personaRol.setActivo(true);

        return personaRolRepository.save(personaRol);
    }

    @Override
    public void eliminarRolPersona(Integer idPersonaRol) {
        personaRolRepository.deleteById(idPersonaRol);
    }
}
