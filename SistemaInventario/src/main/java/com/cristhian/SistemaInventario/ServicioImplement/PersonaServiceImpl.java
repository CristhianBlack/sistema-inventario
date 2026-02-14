package com.cristhian.SistemaInventario.ServicioImplement;
import com.cristhian.SistemaInventario.DTO.PersonaDTO;
import com.cristhian.SistemaInventario.Enums.NombreRol;
import com.cristhian.SistemaInventario.Enums.NombreTipoPersona;
import com.cristhian.SistemaInventario.Enums.RolSeguridad;
import com.cristhian.SistemaInventario.Excepciones.DuplicadoException;
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

/**
 * Servicio encargado de la lógica de negocio relacionada con la entidad Persona.
 * Maneja creación, actualización, eliminación lógica y consultas,
 * incluyendo la asignación y validación de roles.
 */
@Transactional
@Service
public class PersonaServiceImpl implements IPersonaService {

    // Repositorio principal de Persona
    private final PersonaRepository personaRepository;

    // Repositorios de entidades relacionadas
    private final TipoDocumentoRepository tipoDocumentoRepository;
    private final TipoPersonaRepository tipoPersonaRepository;
    private final CiudadRepository ciudadRepository;
    private final RolPersonaRepository rolPersonaRepository;
    private final PersonaRolRepository personaRolRepository;

    // Servicio encargado de la gestión de roles de persona
    private final PersonaRolServiceImpl personaRolService;

    /**
     * Inyección de dependencias mediante constructor
     */
    @Autowired
    public PersonaServiceImpl(PersonaRepository personaRepository,
                              TipoDocumentoRepository tipoDocumentoRepository,
                              TipoPersonaRepository tipoPersonaRepository,
                              CiudadRepository ciudadRepository,
                              RolPersonaRepository rolPersonaRepository,
                              PersonaRolRepository personaRolRepository,
                              PersonaRolServiceImpl personaRolService) {

        this.personaRepository = personaRepository;
        this.tipoDocumentoRepository = tipoDocumentoRepository;
        this.tipoPersonaRepository = tipoPersonaRepository;
        this.ciudadRepository = ciudadRepository;
        this.rolPersonaRepository = rolPersonaRepository;
        this.personaRolRepository = personaRolRepository;
        this.personaRolService = personaRolService;
    }

    // -----------------------------------------------------------
    // 1. CREAR PERSONA CON ROL DEFAULT "PROVEEDOR"
    // -----------------------------------------------------------
    @Override
    public Persona crearPersonaConRolDefault(PersonaDTO dto) {

        // Buscar si ya existe una persona con el mismo documento
        Optional<Persona> personaExistente =
                personaRepository.findByDocumentoPersona(dto.getDocumentoPersona().trim());

        // Si la persona ya existe
        if (personaExistente.isPresent()) {
            Persona persona1 = personaExistente.get();

            // Si existe pero está inactiva → se reactiva
            if (!persona1.isActivo()) {
                System.out.println("persona encontrada inactiva. Se activará nuevamente.");
                persona1.setActivo(true);
                return personaRepository.save(persona1);
            }

            // Si existe y está activa → se lanza excepción
            throw new DuplicadoException("Ya existe una persona con ese documento");
        }

        // Mapeo del DTO a entidad Persona
        Persona persona = mapearPersona(dto);

        // Guardar persona en base de datos
        Persona guardada = personaRepository.save(persona);

        // Asignar rol obligatorio por defecto (PROVEEDOR)
        personaRolService.crearRolDefault(guardada);

        // Asignar roles adicionales si vienen en el DTO (ej: ADMIN)
        if (dto.getIdsRoles() != null && !dto.getIdsRoles().isEmpty()) {
            personaRolService.asignarRolesDesdeIds(guardada, dto.getIdsRoles());
        }

        return guardada;
    }

    // -----------------------------------------------------------
    // 2. ACTUALIZAR PERSONA
    // -----------------------------------------------------------
    public Persona actualizarPersona(Integer id, PersonaDTO dto) {

        // Verificar que la persona exista
        Persona persona = personaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("La persona no existe."));

        // Validar si el documento ya pertenece a otra persona
        Optional<Persona> personaConMismoDocumento =
                personaRepository.findByDocumentoPersona(dto.getDocumentoPersona());

        if (personaConMismoDocumento.isPresent() &&
                personaConMismoDocumento.get().getIdPersona() != id) {

            throw new RuntimeException("El documento ya pertenece a otra persona");
        }

        // Obtener entidades relacionadas
        TipoDocumento tipoDocumento = tipoDocumentoRepository.findById(dto.getIdTipoDocumento())
                .orElseThrow(() -> new RuntimeException("Tipo de documento no encontrado"));

        TipoPersona tipoPersona = tipoPersonaRepository.findById(dto.getIdTipoPersona())
                .orElseThrow(() -> new RuntimeException("Tipo de persona no encontrado"));

        Ciudad ciudad = ciudadRepository.findById(dto.getIdCiudad())
                .orElseThrow(() -> new RuntimeException("Ciudad no encontrada"));

        // Obtener el tipo de persona jurídica para validaciones
        TipoPersona nombreTipoPersona = tipoPersonaRepository
                .findByNombreTipoPersona(NombreTipoPersona.PERSONA_JURIDICA)
                .orElseThrow(() ->
                        new RecursoNoEncontradoException("Nombre tipo de Persona no encontrado"));

        // Actualizar datos comunes
        persona.setDireccion(dto.getDireccion());
        persona.setTelefono(dto.getTelefono());
        persona.setEmail(dto.getEmail());
        persona.setDocumentoPersona(dto.getDocumentoPersona());
        persona.setTipoDocumento(tipoDocumento);
        persona.setTipoPersona(tipoPersona);
        persona.setCiudad(ciudad);

        // Validación específica según tipo de persona
        if (tipoPersona.getIdTipoPersona() == nombreTipoPersona.getIdTipoPersona()) {
            // Persona jurídica
            persona.setNombreContacto(dto.getNombreContacto());
            persona.setApellidoContacto(dto.getApellidoContacto());
            persona.setSegundoApellidoContacto(dto.getSegundoApellidoContacto());
            persona.setRazonSocial(dto.getRazonSocial());
        } else {
            // Persona natural
            persona.setNombre(dto.getNombre());
            persona.setApellido(dto.getApellido());
            persona.setSegundoApellido(dto.getSegundoApellido());
        }

        // Guardar cambios
        personaRepository.save(persona);

        // Actualizar roles si vienen en el DTO
        if (dto.getIdsRoles() != null) {
            personaRolService.actualizarRoles(persona, dto.getIdsRoles());
            actualizarRolSeguridad(persona);
        }

        return persona;
    }

    // -----------------------------------------------------------
    // 3. MAPEAR DTO → PERSONA
    // -----------------------------------------------------------
    private Persona mapearPersona(PersonaDTO dto) {

        // Obtener tipo de persona jurídica para validación
        TipoPersona nombreTipoPersona = tipoPersonaRepository
                .findByNombreTipoPersona(NombreTipoPersona.PERSONA_JURIDICA)
                .orElseThrow(() ->
                        new RecursoNoEncontradoException("Nombre tipo de Persona no encontrado"));

        Persona persona = new Persona();

        // Datos básicos
        persona.setDocumentoPersona(dto.getDocumentoPersona());
        persona.setDireccion(dto.getDireccion());
        persona.setTelefono(dto.getTelefono());
        persona.setEmail(dto.getEmail());

        // Validación según tipo de persona
        if (dto.getIdTipoPersona() == nombreTipoPersona.getIdTipoPersona()) {
            // Jurídica
            persona.setNombreContacto(dto.getNombreContacto());
            persona.setApellidoContacto(dto.getApellidoContacto());
            persona.setSegundoApellidoContacto(dto.getSegundoApellidoContacto());
            persona.setRazonSocial(dto.getRazonSocial());
            persona.setNombre("");
            persona.setApellido("");
            persona.setSegundoApellido("");
        } else {
            // Natural
            persona.setNombre(dto.getNombre());
            persona.setApellido(dto.getApellido());
            persona.setSegundoApellido(dto.getSegundoApellido());
            persona.setRazonSocial("");
        }

        // Asignar TipoDocumento por referencia
        TipoDocumento td = new TipoDocumento();
        td.setIdTipoDocumento(dto.getIdTipoDocumento());
        persona.setTipoDocumento(td);

        // Asignar TipoPersona por referencia
        TipoPersona tp = new TipoPersona();
        tp.setIdTipoPersona(dto.getIdTipoPersona());
        persona.setTipoPersona(tp);

        // Asignar Ciudad por referencia
        Ciudad ciudad = new Ciudad();
        ciudad.setIdCiudad(dto.getIdCiudad());
        persona.setCiudad(ciudad);

        return persona;
    }

    // -----------------------------------------------------------
    // 4. LISTAR / BUSCAR / ELIMINAR (LÓGICO)
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
        if (persona != null) {
            persona.setActivo(false);
            personaRepository.save(persona);
        }
    }

    // ---------------------------------------------------------
    // 5. CONSULTAS POR ROL PROVEEDOR
    // ---------------------------------------------------------
    public List<Persona> obtenerPersonasConRolProveedor() {

        RolPersona rolProveedor = rolPersonaRepository
                .findByNombreRol(NombreRol.PROVEEDOR)
                .orElseThrow();

        int idRolProveedor = rolProveedor.getIdRolPersona();
        return personaRolRepository.findPersonasConRol(idRolProveedor);
    }

    public boolean personaTieneRolProveedor(int idPersona) {

        RolPersona rolProveedor = rolPersonaRepository
                .findByNombreRol(NombreRol.PROVEEDOR)
                .orElseThrow();

        int idRolProveedor = rolProveedor.getIdRolPersona();

        return personaRolRepository
                .existsByPersonaIdPersonaAndRolPersonaIdRolPersona(idPersona, idRolProveedor);
    }

    @Override
    public List<PersonaDTO> listarPersonasConRolProveedor() {

        RolPersona rolProveedor = rolPersonaRepository
                .findByNombreRol(NombreRol.PROVEEDOR)
                .orElseThrow();

        int idRolProveedor = rolProveedor.getIdRolPersona();
        return personaRolRepository.findPersonasConRolDTO(idRolProveedor);
    }

    @Override
    public List<PersonaDTO> listarClientes() {
        RolPersona rolCliente = rolPersonaRepository
                .findByNombreRol(NombreRol.CLIENTE)
                .orElseThrow();

        int idRolCliente = rolCliente.getIdRolPersona();
        return personaRolRepository.findPersonasConRolDTO(idRolCliente);
    }

    // ---------------------------------------------------------
    // 6. ACTUALIZAR ROL DE SEGURIDAD (USUARIO)
    // ---------------------------------------------------------
    private void actualizarRolSeguridad(Persona persona) {

        Usuario usuario = persona.getUsuario();
        if (usuario == null) return;

        // Obtener roles activos de la persona
        List<PersonaRol> rolesActivos =
                personaRolRepository.findByPersona(persona)
                        .stream()
                        .filter(PersonaRol::isActivo)
                        .toList();

        // Validar si la persona es ADMIN
        boolean esAdmin = rolesActivos.stream()
                .anyMatch(pr -> pr.getRolPersona().getNombreRol() == NombreRol.ADMIN);

        // Asignar rol de seguridad
        if (esAdmin) {
            usuario.setRolSeguridad(RolSeguridad.ADMIN_SISTEMA);
        } else {
            usuario.setRolSeguridad(RolSeguridad.USER);
        }
    }

}

