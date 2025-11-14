package com.cristhian.SistemaInventario.ServicioImplement;

import com.cristhian.SistemaInventario.Modelo.Persona;
import com.cristhian.SistemaInventario.Repositorio.PersonaRepository;
import com.cristhian.SistemaInventario.Service.IPersonaService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class PersonaServiceImpl implements IPersonaService {

    private final PersonaRepository personaRepository;

    public PersonaServiceImpl(PersonaRepository personaRepository) {
        this.personaRepository = personaRepository;
    }

    @Override
    public List<Persona> listarPersonasActivas() {
        return personaRepository.findByActivoTrue();
    }

    @Override
    public Optional<Persona> buscarPersonaID(int id) {
        return personaRepository.findById(id);
    }

    @Override
    public Persona guardarPersona(Persona persona) {

        return personaRepository.save(persona);
    }

    @Override
    public void eliminarPersona(int id) {
        Persona persona = personaRepository.findById(id).orElseGet(null);
        if(persona != null){
            persona.setActivo(false);
            personaRepository.save(persona);
        }
    }

    @Override
    public boolean existsPersonaByDocumento(String documentoPersona){
        return personaRepository.existsByDocumentoPersona(documentoPersona);
    }

    @Override
    public Optional<Persona> findByPersonaIgnoreCase(String nombre) {
        return Optional.empty();
    }
}
