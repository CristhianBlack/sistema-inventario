package com.cristhian.SistemaInventario.ServicioImplement;

import com.cristhian.SistemaInventario.DTO.PersonaDTO;
import com.cristhian.SistemaInventario.Enums.NombreRol;
import com.cristhian.SistemaInventario.Excepciones.ConflictoRolException;
import com.cristhian.SistemaInventario.Modelo.Persona;
import com.cristhian.SistemaInventario.Modelo.PersonaRol;
import com.cristhian.SistemaInventario.Modelo.RolPersona;
import com.cristhian.SistemaInventario.Repositorio.PersonaRepository;
import com.cristhian.SistemaInventario.Repositorio.PersonaRolRepository;
import com.cristhian.SistemaInventario.Repositorio.RolPersonaRepository;
import com.cristhian.SistemaInventario.Service.IPersonaRolService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Servicio encargado de la gestión de roles asociados a una persona.
 * Permite:
 * - Asignar roles
 * - Validar conflictos entre roles
 * - Activar / desactivar roles
 * - Manejar rol por defecto
 */
@Service
@Transactional
public class PersonaRolServiceImpl implements IPersonaRolService {

    // Repositorios
    private final PersonaRolRepository personaRolRepository;
    private final PersonaRepository personaRepository;
    private final RolPersonaRepository rolPersonaRepository;

    // Inyección por constructor
    public PersonaRolServiceImpl(PersonaRolRepository personaRolRepository,
                                 PersonaRepository personaRepository,
                                 RolPersonaRepository rolPersonaRepository) {
        this.personaRolRepository = personaRolRepository;
        this.personaRepository = personaRepository;
        this.rolPersonaRepository = rolPersonaRepository;
    }

    /**
     * Obtiene los roles activos asociados a una persona.
     * SOLO se retornan roles con estado activo = true.
     */
    @Override
    public List<PersonaRol> findByPersona_IdPersona(Integer idPersona) {
        return personaRolRepository.findByPersonaId(idPersona)
                .stream()
                .filter(PersonaRol::isActivo)
                .toList();
    }

    /**
     * Retorna los roles (RolPersona) asociados a una persona.
     */
    @Override
    public List<RolPersona> obtenerRolesPorPersona(Integer idPersona) {
        return personaRolRepository.findRolesByPersonaId(idPersona);
    }

    // ---------------------------------------------------------
    // ROL DEFAULT
    // ---------------------------------------------------------

    /**
     * Asigna automáticamente el rol PROVEEDOR a una persona.
     * Se utiliza normalmente al crear una persona por defecto.
     */
    public Persona crearRolDefault(Persona persona) {

        boolean esAdmin = SecurityContextHolder.getContext()
                .getAuthentication()
                .getAuthorities()
                .stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN_SISTEMA"));

        // Admin NO recibe rol automático
        if (esAdmin) {
            return persona;
        }

        RolPersona rolProveedor = rolPersonaRepository
                .findByNombreRol(NombreRol.CLIENTE)
                .orElseThrow(() -> new IllegalStateException("Rol cliente no existe"));

        PersonaRol pr = new PersonaRol();
        pr.setPersona(persona);
        pr.setRolPersona(rolProveedor);
        pr.setActivo(true);
        pr.setFechaAsignacion(LocalDate.now());

        personaRolRepository.save(pr);

        return persona;
    }

    // ---------------------------------------------------------
    // ASIGNAR UN SOLO ROL
    // ---------------------------------------------------------

    /**
     * Asigna un rol específico a una persona.
     * Valida duplicados y conflictos de roles antes de guardar.
     */
    @Override
    public PersonaRol asignarRol(Integer idPersona, Integer idRol) {

        Persona persona = personaRepository.findById(idPersona)
                .orElseThrow(() -> new IllegalStateException("Persona no encontrada"));

        RolPersona rol = rolPersonaRepository.findById(idRol)
                .orElseThrow(() -> new IllegalStateException("Rol no encontrado"));

        // 🚨 Evitar asignar el mismo rol activo más de una vez
        boolean yaExiste = personaRolRepository
                .existsByPersona_IdPersonaAndRolPersona_IdRolPersonaAndActivoTrue(idPersona, idRol);

        if (yaExiste) {
            throw new ConflictoRolException("La persona ya tiene este rol activo");
        }

        personaRolRepository.flush();

        // Obtener roles activos actuales
        List<NombreRol> rolesActuales =
                personaRolRepository.findRolesActivosByPersonaId(idPersona)
                        .stream()
                        .map(pr -> pr.getRolPersona().getNombreRol())
                        .toList();

        // Simular el nuevo estado final de roles
        List<NombreRol> nombres = new ArrayList<>(rolesActuales);
        nombres.add(rol.getNombreRol());

        // Validar conflictos
        validarConflictoRoles(nombres);

        PersonaRol personaRol = new PersonaRol();
        personaRol.setPersona(persona);
        personaRol.setRolPersona(rol);
        personaRol.setFechaAsignacion(LocalDate.now());
        personaRol.setActivo(true);

        return personaRolRepository.save(personaRol);
    }

    // ---------------------------------------------------------
    // ACTUALIZAR ROLES (ADMIN)
    // ---------------------------------------------------------

    /**
     * Actualiza completamente los roles de una persona.
     * Se desactivan los roles no enviados y se activan o crean los nuevos.
     */
    public void actualizarRoles(Persona persona, List<Integer> idsRoles) {

        // 1️⃣ Construir el estado FINAL primero (validación)
        List<RolPersona> roles = rolPersonaRepository.findAllById(idsRoles);

        List<NombreRol> nombres = roles.stream()
                .map(RolPersona::getNombreRol)
                .toList();

        validarConflictoRoles(nombres);

        // 2️⃣ Aplicar cambios en base de datos
        List<PersonaRol> actuales = personaRolRepository.findByPersona(persona);

        // Desactivar roles que ya no están
        for (PersonaRol pr : actuales) {

            Integer idActual = pr.getRolPersona().getIdRolPersona();

            if (!idsRoles.contains(idActual)) {
                pr.setActivo(false);
                personaRolRepository.save(pr);
            }
        }

        // Activar o crear roles
        for (Integer idRol : idsRoles) {

            Optional<PersonaRol> existente = actuales.stream()
                    .filter(pr -> pr.getRolPersona().getIdRolPersona() == idRol)
                    .findFirst();

            if (existente.isPresent()) {

                PersonaRol pr = existente.get();
                pr.setActivo(true);
                personaRolRepository.save(pr);

            } else {

                RolPersona rol = rolPersonaRepository.findById(idRol)
                        .orElseThrow(() -> new IllegalStateException("Rol no encontrado"));

                PersonaRol nuevo = new PersonaRol();
                nuevo.setPersona(persona);
                nuevo.setRolPersona(rol);
                nuevo.setActivo(true);
                nuevo.setFechaAsignacion(LocalDate.now());

                personaRolRepository.save(nuevo);
            }
        }
    }

    /**
     * Elimina un registro PersonaRol por su ID.
     */
    @Override
    public void eliminarRolPersona(Integer idPersonaRol) {
        personaRolRepository.deleteById(idPersonaRol);
    }

    // ---------------------------------------------------------
    // VALIDACIÓN DE CONFLICTOS
    // ---------------------------------------------------------

    /**
     * Valida combinaciones inválidas de roles.
     * Regla actual:
     * - ADMIN no puede coexistir con EMPLEADO ni PROVEEDOR
     */
    private void validarConflictoRoles(List<NombreRol> roles){

        boolean admin = roles.contains(NombreRol.ADMIN);
        boolean empleado = roles.contains(NombreRol.EMPLEADO);
        boolean proveedor = roles.contains(NombreRol.PROVEEDOR);

        if(admin && (empleado || proveedor)){
            throw new ConflictoRolException(
                    "Una persona no puede ser ADMIN y EMPLEADO/PROVEEDOR al mismo tiempo"
            );
        }
    }

    /**
     * Asigna múltiples roles a una persona a partir de una lista de IDs.
     * Evita duplicados y solo crea relaciones inexistentes.
     */
    public void asignarRolesDesdeIds(Persona persona, List<Integer> idsRoles) {

        if (idsRoles == null || idsRoles.isEmpty()) {
            return;
        }

        for (Integer idRol : idsRoles) {

            RolPersona rol = rolPersonaRepository.findById(idRol.intValue())
                    .orElseThrow(() -> new IllegalArgumentException("Rol no existe"));

            boolean yaAsignado = personaRolRepository
                    .existsByPersonaIdPersonaAndRolPersonaIdRolPersona(
                            persona.getIdPersona(),
                            rol.getIdRolPersona()
                    );

            if (!yaAsignado) {
                PersonaRol pr = new PersonaRol();
                pr.setPersona(persona);
                pr.setRolPersona(rol);
                pr.setActivo(true);
                pr.setFechaAsignacion(LocalDate.now());

                personaRolRepository.save(pr);
            }
        }
    }
}

