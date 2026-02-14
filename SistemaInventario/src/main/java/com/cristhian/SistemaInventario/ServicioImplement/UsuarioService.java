package com.cristhian.SistemaInventario.ServicioImplement;

import com.cristhian.SistemaInventario.DTO.*;
import com.cristhian.SistemaInventario.Enums.NombreRol;
import com.cristhian.SistemaInventario.Enums.RolSeguridad;
import com.cristhian.SistemaInventario.Excepciones.DuplicadoException;
import com.cristhian.SistemaInventario.Excepciones.PasswordException;
import com.cristhian.SistemaInventario.Excepciones.RecursoNoEncontradoException;
import com.cristhian.SistemaInventario.Modelo.*;
import com.cristhian.SistemaInventario.Repositorio.PersonaRepository;
import com.cristhian.SistemaInventario.Repositorio.PersonaRolRepository;
import com.cristhian.SistemaInventario.Repositorio.UsuarioRepository;
import com.cristhian.SistemaInventario.Security.JwtUtil;
import jakarta.transaction.Transactional;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;

import java.util.List;
/**
 * Servicio encargado de la gestión completa de usuarios del sistema.
 *
 * Responsabilidades:
 * - Creación y edición de usuarios
 * - Asignación automática de roles de seguridad
 * - Autenticación y generación de JWT
 * - Cambio de contraseña
 * - Eliminación lógica (soft delete)
 */
@Service
@Transactional
public class UsuarioService {

   /* private final UsuarioRepository usuarioRepository;
    private final PersonaRepository personaRepository;
    private final PasswordEncoder passwordEncoder;
    private final PersonaRolRepository personaRolRepository;
    private final JwtUtil jwtUtil;

    public UsuarioService(
            UsuarioRepository usuarioRepository,
            PersonaRepository personaRepository,
            PasswordEncoder passwordEncoder,
            PersonaRolRepository personaRolRepository,
            JwtUtil jwtUtil
    ) {
        this.usuarioRepository = usuarioRepository;
        this.personaRepository = personaRepository;
        this.passwordEncoder = passwordEncoder;
        this.personaRolRepository = personaRolRepository;
        this.jwtUtil = jwtUtil;
    }

    public UsuarioResponseDTO crearUsuario(UsuarioRequestDTO dto) {

        if (usuarioRepository.existsByUsername(dto.getUsername())) {
            throw new DuplicadoException("El nombre de usuario ya existe");
        }

        Persona persona = personaRepository.findById(dto.getIdPersona())
                .orElseThrow(() -> new RecursoNoEncontradoException("Persona no encontrada"));

        RolSeguridad rolSeguridad = determinarRolSeguridad(persona);

        Usuario usuario = new Usuario();
        usuario.setUsername(dto.getUsername());
        usuario.setPassword(passwordEncoder.encode(dto.getPassword()));
        usuario.setRolSeguridad(rolSeguridad);
        usuario.setPersona(persona);
        usuario.setActivo(true);

        usuarioRepository.save(usuario);

        return mapToResponse(usuario);
    }

    public List<UsuarioResponseDTO> listarUsuarios() {
        return usuarioRepository.findByActivoTrue()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    public UsuarioResponseDTO obtenerPorId(int id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        return mapToResponse(usuario);
    }

    public UsuarioResponseDTO editarUsuario(int id, UsuarioRequestDTO dto) {

        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado"));

        Persona persona = personaRepository.findById(dto.getIdPersona())
                .orElseThrow(() -> new RecursoNoEncontradoException("Persona no encontrada"));

        List<PersonaRol> test =
                personaRolRepository.findRolesActivosByPersonaId(persona.getIdPersona());

        System.out.println("===== ROLES ACTIVOS BD =====");
        System.out.println("Cantidad: " + test.size());

        test.forEach(r ->
                System.out.println(
                        r.getRolPersona().getNombreRol() + " - activo=" + r.isActivo()
                )
        );

        RolSeguridad rolSeguridad = determinarRolSeguridad(persona);



        usuario.setUsername(dto.getUsername());
        usuario.setRolSeguridad(rolSeguridad);

        if (dto.getPassword() != null && !dto.getPassword().isBlank()) {
            usuario.setPassword(passwordEncoder.encode(dto.getPassword()));
        }

        usuario.setPersona(persona);

        usuarioRepository.save(usuario);

        return mapToResponse(usuario);
    }

    public void eliminarUsuario(int id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        usuario.setActivo(false); // soft delete
        usuarioRepository.save(usuario);
    }

    private UsuarioResponseDTO mapToResponse(Usuario usuario) {
        UsuarioResponseDTO dto = new UsuarioResponseDTO();
        dto.setIdUsuario(usuario.getIdUsuario());
        dto.setUsername(usuario.getUsername());
        dto.setRolSeguridad(usuario.getRolSeguridad().name());
        dto.setActivo(usuario.isActivo());
        dto.setIdPersona(usuario.getPersona().getIdPersona());
        dto.setNombre(usuario.getPersona().getNombre());
        dto.setApellido(usuario.getPersona().getApellido());
        dto.setSegundoApellido(usuario.getPersona().getSegundoApellido());
        return dto;
    }

    //Metodo que permite asingar el rol seguridad deacuerdo al rol de aplicacion creado en persona
    private RolSeguridad determinarRolSeguridad(Persona persona) {

        List<PersonaRol> rolesActivos =
                personaRolRepository.findRolesActivosByPersonaId(persona.getIdPersona());

        System.out.println("ROLES ACTIVOS = " + rolesActivos.size());

        rolesActivos.forEach(r ->
                System.out.println("ROL: " + r.getRolPersona().getNombreRol())
        );

        boolean esAdmin = rolesActivos.stream()
                .anyMatch(r -> r.getRolPersona().getNombreRol() == NombreRol.ADMIN);

        boolean esEmpleado = rolesActivos.stream()
                .anyMatch(r -> r.getRolPersona().getNombreRol() == NombreRol.EMPLEADO);

        if (esAdmin) {
            return RolSeguridad.ADMIN_SISTEMA;
        }

        if (esEmpleado) {
            return RolSeguridad.USER;
        }

        throw new RuntimeException("La persona no tiene rol válido");
    }

    public LoginResponseDTO login(LoginRequestDTO request){

        Usuario usuario = usuarioRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("Usuario no existe"));

        if (!passwordEncoder.matches(request.getPassword(), usuario.getPassword())) {
            throw new RuntimeException("Credenciales incorrectas");
        }

        Persona p = usuario.getPersona();
        String nombreCompleto = p.getNombre() + " " + p.getApellido();

        String token = jwtUtil.generarToken(
                usuario.getUsername(),
                usuario.getRolSeguridad().name()
        );

        return new LoginResponseDTO(
                usuario.getUsername(),
                usuario.getRolSeguridad().name(),
                nombreCompleto,
                token,
                usuario.isDebeCambiarPassword() // MUY IMPORTANTE
        );
    }

    public String cambiarPassword(ChangePasswordDTO request){


        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        Usuario usuarioAuth = (Usuario) auth.getPrincipal();
        String username = usuarioAuth.getUsername();
        System.out.println("USERNAME QUE LLEGA: " + username);

        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario no existe"));

        // Validar contraseña actual
        if(!passwordEncoder.matches(request.getPasswordActual(), usuario.getPassword())){
            throw new PasswordException("La contraseña actual es incorrecta");
        }

        // Validar que no repita contraseña
        if(passwordEncoder.matches(request.getPasswordNueva(), usuario.getPassword())){
            throw new PasswordException("La nueva contraseña no puede ser igual a la anterior");
        }

        // Guardar nueva password encriptada
        usuario.setPassword(passwordEncoder.encode(request.getPasswordNueva()));

        //  CLAVE DEL SISTEMA
        usuario.setDebeCambiarPassword(false);

        usuarioRepository.save(usuario);

        //Cerrar sesión del usuario actual
        SecurityContextHolder.clearContext();

        return "Password cambiada correctamente";
    }*/

    private final UsuarioRepository usuarioRepository;
    private final PersonaRepository personaRepository;
    private final PasswordEncoder passwordEncoder;
    private final PersonaRolRepository personaRolRepository;
    private final JwtUtil jwtUtil;
    private static final String PASSWORD_REGEX = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{8,}$";

    public UsuarioService(
            UsuarioRepository usuarioRepository,
            PersonaRepository personaRepository,
            PasswordEncoder passwordEncoder,
            PersonaRolRepository personaRolRepository,
            JwtUtil jwtUtil
    ) {
        this.usuarioRepository = usuarioRepository;
        this.personaRepository = personaRepository;
        this.passwordEncoder = passwordEncoder;
        this.personaRolRepository = personaRolRepository;
        this.jwtUtil = jwtUtil;
    }

    /**
     * Crea un nuevo usuario asociado a una persona existente.
     *
     * - Valida duplicidad de username
     * - Determina el rol de seguridad según los roles de la persona
     * - Encripta la contraseña
     */
    public UsuarioResponseDTO crearUsuario(UsuarioRequestDTO dto) {

        if (usuarioRepository.existsByUsername(dto.getUsername())) {
            throw new DuplicadoException("El nombre de usuario ya existe");
        }

        Persona persona = personaRepository.findById(dto.getIdPersona())
                .orElseThrow(() -> new RecursoNoEncontradoException("Persona no encontrada"));

        RolSeguridad rolSeguridad = determinarRolSeguridad(persona);

        Usuario usuario = new Usuario();
        usuario.setUsername(dto.getUsername());
        usuario.setPassword(passwordEncoder.encode(dto.getPassword()));
        usuario.setRolSeguridad(rolSeguridad);
        usuario.setPersona(persona);
        usuario.setActivo(true);

        usuarioRepository.save(usuario);

        return mapToResponse(usuario);
    }

    /**
     * Lista todos los usuarios activos del sistema.
     */
    public List<UsuarioResponseDTO> listarUsuarios() {
        return usuarioRepository.findByActivoTrue()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    /**
     * Obtiene un usuario por su identificador.
     */
    public UsuarioResponseDTO obtenerPorId(int id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        return mapToResponse(usuario);
    }

    /**
     * Edita los datos de un usuario existente.
     *
     * - Recalcula el rol de seguridad según los roles activos de la persona
     * - Permite actualizar contraseña si se envía
     */
    public UsuarioResponseDTO editarUsuario(int id, UsuarioRequestDTO dto) {

        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado"));

        Persona persona = personaRepository.findById(dto.getIdPersona())
                .orElseThrow(() -> new RecursoNoEncontradoException("Persona no encontrada"));

        RolSeguridad rolSeguridad = determinarRolSeguridad(persona);

        usuario.setUsername(dto.getUsername());
        usuario.setRolSeguridad(rolSeguridad);

        if (dto.getPassword() != null && !dto.getPassword().isBlank()) {
            usuario.setPassword(passwordEncoder.encode(dto.getPassword()));
        }

        usuario.setPersona(persona);
        usuarioRepository.save(usuario);

        return mapToResponse(usuario);
    }

    /**
     * Elimina un usuario de forma lógica (soft delete).
     */
    public void eliminarUsuario(int id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        usuario.setActivo(false);
        usuarioRepository.save(usuario);
    }

    /**
     * Mapea la entidad Usuario a su DTO de respuesta.
     */
    private UsuarioResponseDTO mapToResponse(Usuario usuario) {
        UsuarioResponseDTO dto = new UsuarioResponseDTO();
        dto.setIdUsuario(usuario.getIdUsuario());
        dto.setUsername(usuario.getUsername());
        dto.setRolSeguridad(usuario.getRolSeguridad().name());
        dto.setActivo(usuario.isActivo());
        dto.setIdPersona(usuario.getPersona().getIdPersona());
        dto.setNombre(usuario.getPersona().getNombre());
        dto.setApellido(usuario.getPersona().getApellido());
        dto.setSegundoApellido(usuario.getPersona().getSegundoApellido());
        return dto;
    }

    /**
     * Determina el rol de seguridad del usuario según los roles
     * activos asociados a la persona.
     */
    private RolSeguridad determinarRolSeguridad(Persona persona) {

        List<PersonaRol> rolesActivos =
                personaRolRepository.findRolesActivosByPersonaId(persona.getIdPersona());

        boolean esAdmin = rolesActivos.stream()
                .anyMatch(r -> r.getRolPersona().getNombreRol() == NombreRol.ADMIN);

        boolean esEmpleado = rolesActivos.stream()
                .anyMatch(r -> r.getRolPersona().getNombreRol() == NombreRol.EMPLEADO);

        if (esAdmin) {
            return RolSeguridad.ADMIN_SISTEMA;
        }

        if (esEmpleado) {
            return RolSeguridad.USER;
        }

        throw new RuntimeException("La persona no tiene rol válido");
    }

    /**
     * Autentica un usuario y genera un token JWT.
     */
    public LoginResponseDTO login(LoginRequestDTO request){

        Usuario usuario = usuarioRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("Usuario no existe"));

        if (!passwordEncoder.matches(request.getPassword(), usuario.getPassword())) {
            throw new RuntimeException("Credenciales incorrectas");
        }

        Persona p = usuario.getPersona();
        String nombreCompleto = p.getNombre() + " " + p.getApellido();

        String token = jwtUtil.generarToken(
                usuario.getUsername(),
                usuario.getRolSeguridad().name()
        );

        return new LoginResponseDTO(
                usuario.getUsername(),
                usuario.getRolSeguridad().name(),
                nombreCompleto,
                token,
                usuario.isDebeCambiarPassword()
        );
    }

    /**
     * Permite cambiar la contraseña del usuario autenticado.
     *
     * - Valida contraseña actual
     * - Evita reutilizar la misma contraseña
     * - Obliga cierre de sesión
     */
    public String cambiarPassword(ChangePasswordDTO request){

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Usuario usuarioAuth = (Usuario) auth.getPrincipal();

        Usuario usuario = usuarioRepository.findByUsername(usuarioAuth.getUsername())
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario no existe"));

        if(!passwordEncoder.matches(request.getPasswordActual(), usuario.getPassword())){
            throw new PasswordException("La contraseña actual es incorrecta");
        }

        if(passwordEncoder.matches(request.getPasswordNueva(), usuario.getPassword())){
            throw new PasswordException("La nueva contraseña no puede ser igual a la anterior");
        }
        validarPassword(request.getPasswordNueva());

        usuario.setPassword(passwordEncoder.encode(request.getPasswordNueva()));
        usuario.setDebeCambiarPassword(false);

        usuarioRepository.save(usuario);

        SecurityContextHolder.clearContext();

        return "Password cambiada correctamente";
    }

    private void validarPassword(String password) {
        if (!password.matches(PASSWORD_REGEX)) {
            throw new IllegalArgumentException(
                    "La contraseña no cumple con los requisitos de seguridad"
            );
        }
    }
}
