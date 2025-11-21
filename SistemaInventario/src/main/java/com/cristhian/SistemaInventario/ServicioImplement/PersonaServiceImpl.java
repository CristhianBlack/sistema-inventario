package com.cristhian.SistemaInventario.ServicioImplement;/*package com.cristhian.SistemaInventario.ServicioImplement;

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
}*/

import com.cristhian.SistemaInventario.DTO.PersonaDTO;
import com.cristhian.SistemaInventario.Excepciones.RecursoNoEncontradoException;
import com.cristhian.SistemaInventario.Modelo.*;
import com.cristhian.SistemaInventario.Repositorio.*;
import com.cristhian.SistemaInventario.Service.IPersonaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;


@Transactional
@Service
public class PersonaServiceImpl implements IPersonaService {

    private final PersonaRepository personaRepository;
    private final TipoDocumentoRepository tipoDocumentoRepository;
    private final TipoPersonaRepository tipoPersonaRepository;
    private final CiudadRepository ciudadRepository;
    private final RolPersonaRepository rolPersonaRepository;
    private final PersonaRolRepository personaRolRepository;

    @Autowired
    public PersonaServiceImpl(PersonaRepository personaRepository,
                              TipoDocumentoRepository tipoDocumentoRepository,
                              TipoPersonaRepository tipoPersonaRepository,
                              CiudadRepository ciudadRepository,
                              RolPersonaRepository rolPersonaRepository,
                              PersonaRolRepository personaRolRepository) {

        this.personaRepository = personaRepository;
        this.tipoDocumentoRepository = tipoDocumentoRepository;
        this.tipoPersonaRepository = tipoPersonaRepository;
        this.ciudadRepository = ciudadRepository;
        this.rolPersonaRepository = rolPersonaRepository;
        this.personaRolRepository = personaRolRepository;
    }

    // -----------------------------------------------------------
    // 1. CREAR PERSONA CON ROL DEFAULT "CLIENTE"
    // -----------------------------------------------------------
    @Override
    public Persona crearPersonaConRolDefault(PersonaDTO dto) {

        Persona persona = mapearPersona(dto);
        Persona guardada = personaRepository.save(persona);

        RolPersona rolCliente = rolPersonaRepository.findByNombreRolIgnoreCase("CLIENTE")
                .orElseThrow(() -> new RuntimeException("Rol CLIENTE no existe"));

        PersonaRol pr = new PersonaRol();
        pr.setPersona(guardada);
        pr.setRolPersona(rolCliente);
        pr.setActivo(true);
        pr.setFechaAsignacion(LocalDate.now());

        personaRolRepository.save(pr);

        return guardada;
    }

    // -----------------------------------------------------------
    // 2. ACTUALIZAR PERSONA
    // -----------------------------------------------------------

    public Persona actualizarPersona(Integer id, PersonaDTO dto) {

        // 1. Verificar que exista la persona
        Persona persona = personaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("La persona no existe."));

        // 2. Validar documento duplicado
        Optional<Persona> personaConMismoDocumento =
                personaRepository.findByDocumentoPersona(dto.getDocumentoPersona());

        // SI existe una persona con ese documento
        // Y esa persona NO es la misma que estamos editando → DUPLICADO
        if (personaConMismoDocumento.isPresent() &&
                personaConMismoDocumento.get().getIdPersona() != id ) {

            throw new RuntimeException("El documento ya pertenece a otra persona");
        }


        // 3. Obtener entidades relacionadas
        TipoDocumento tipoDocumento = tipoDocumentoRepository.findById(dto.getIdTipoDocumento())
                .orElseThrow(() -> new RuntimeException("Tipo de documento no encontrado"));

        TipoPersona tipoPersona = tipoPersonaRepository.findById(dto.getIdTipoPersona())
                .orElseThrow(() -> new RuntimeException("Tipo de persona no encontrado"));

        Ciudad ciudad = ciudadRepository.findById(dto.getIdCiudad())
                .orElseThrow(() -> new RuntimeException("Ciudad no encontrada"));

        // 4. Actualizar datos básicos
        persona.setNombre(dto.getNombre());
        persona.setApellido(dto.getApellido());
        persona.setSegundoApellido(dto.getSegundoApellido());
        persona.setDireccion(dto.getDireccion());
        persona.setTelefono(dto.getTelefono());
        persona.setEmail(dto.getEmail());
        persona.setDocumentoPersona(dto.getDocumentoPersona());
        persona.setTipoDocumento(tipoDocumento);
        persona.setTipoPersona(tipoPersona);
        persona.setCiudad(ciudad);

        personaRepository.save(persona);

        // 5. Actualizar roles si vienen en el DTO
        if (dto.getIdsRoles() != null) {
            actualizarRoles(persona, dto.getIdsRoles());
        }
        return persona;
    }

    // -----------------------------------------------------------
    // 3. ACTUALIZAR ROLES (ADMIN)
    // -----------------------------------------------------------
    private void actualizarRoles(Persona persona, List<Integer> idsRoles) {

        List<PersonaRol> actuales = personaRolRepository.findByPersona(persona);

        // Desactivar roles no seleccionados
        for (PersonaRol pr : actuales) {
            if (!idsRoles.contains(pr.getRolPersona().getIdRolPersona())) {
                pr.setActivo(false);
                personaRolRepository.save(pr);
            }
        }

        // Activar o crear nuevos
        for (Integer idRol : idsRoles) {
            boolean yaTiene = actuales.stream()
                    .anyMatch(pr -> pr.getRolPersona().getIdRolPersona() == idRol);

            if (!yaTiene) {
                RolPersona nuevo = rolPersonaRepository.findById(idRol)
                        .orElseThrow(() -> new RuntimeException("Rol no encontrado"));

                PersonaRol pr = new PersonaRol();
                pr.setPersona(persona);
                pr.setRolPersona(nuevo);
                pr.setActivo(true);
                pr.setFechaAsignacion(LocalDate.now());

                personaRolRepository.save(pr);
            }
        }
    }

    // -----------------------------------------------------------
    // 4. MAPEAR DTO → PERSONA
    // -----------------------------------------------------------
    private Persona mapearPersona(PersonaDTO dto) {

        Persona persona = new Persona();

        persona.setDocumentoPersona(dto.getDocumentoPersona());
        persona.setNombre(dto.getNombre());
        persona.setApellido(dto.getApellido());
        persona.setSegundoApellido(dto.getSegundoApellido());
        persona.setDireccion(dto.getDireccion());
        persona.setTelefono(dto.getTelefono());
        persona.setEmail(dto.getEmail());

        // Asignar TipoDocumento
        TipoDocumento td = new TipoDocumento();
        td.setIdTipoDocumento(dto.getIdTipoDocumento());
        persona.setTipoDocumento(td);

        // Asignar TipoPersona
        TipoPersona tp = new TipoPersona();
        tp.setIdTipoPersona(dto.getIdTipoPersona());
        persona.setTipoPersona(tp);

        // Asignar Ciudad
        Ciudad ciudad = new Ciudad();
        ciudad.setIdCiudad(dto.getIdCiudad());
        persona.setCiudad(ciudad);

        return persona;
    }

    // -----------------------------------------------------------
    // 5. LISTAR / BUSCAR
    // -----------------------------------------------------------
    @Override
    public List<Persona> listarPersonas() {
        return personaRepository.findAll();
    }

    @Override
    public Optional<Persona> buscarPorId(Integer id) {
        return personaRepository.findById(id);
    }

    @Override
    public void eliminarPersona(Integer id) {
        Persona persona = personaRepository.findById(id).orElse(null);
        if(persona != null){
            persona.setActivo(false);
            personaRepository.save(persona);
        }
    }
}

