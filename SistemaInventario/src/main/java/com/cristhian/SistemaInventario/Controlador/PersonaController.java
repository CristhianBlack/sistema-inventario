package com.cristhian.SistemaInventario.Controlador;/*package com.cristhian.SistemaInventario.Controlador;

import com.cristhian.SistemaInventario.DTO.PersonaDTO;
import com.cristhian.SistemaInventario.Mensaje.Mensaje;
import com.cristhian.SistemaInventario.Modelo.*;
import com.cristhian.SistemaInventario.Repositorio.*;
import com.cristhian.SistemaInventario.Service.IPersonaService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@RestController
@RequestMapping("/Inventario")
@CrossOrigin(origins = "http://localhost:4200")
public class PersonaController {

    private final IPersonaService personaService;
    private final TipoDocumentoRepository tipoDocumentoRepository;
    private final TipoPersonaRepository tipoPersonaRepository;
    private final CiudadRepository ciudadRepository;
    private final RolPersonaRepository rolPersonaRepository;

    private final PersonaRolRepository personaRolRepository;

    public PersonaController(IPersonaService personaService, TipoDocumentoRepository tipoDocumentoRepository,
                             TipoPersonaRepository tipoPersonaRepository,
                             CiudadRepository ciudadRepository, RolPersonaRepository rolPersonaRepository,
                             PersonaRolRepository personaRolRepository) {
        this.personaService = personaService;
        this.tipoDocumentoRepository = tipoDocumentoRepository;
        this.tipoPersonaRepository = tipoPersonaRepository;
        this.ciudadRepository = ciudadRepository;
        this.rolPersonaRepository = rolPersonaRepository;
        this.personaRolRepository = personaRolRepository;
    }

    // Listar personas activas
    @GetMapping("/Personas")
    public ResponseEntity<List<Persona>> listarPersonas() {
        List<Persona> listado = personaService.listarPersonasActivas();
        return new ResponseEntity<>(listado, HttpStatus.OK);
    }

    // Obtener persona por ID
    @GetMapping("/Personas/{id}")
    public ResponseEntity<?> obtenerPersonaPorID(@PathVariable int id) {
        Optional<Persona> personaOPT = personaService.buscarPersonaID(id);
        if (personaOPT.isPresent()) {
            return new ResponseEntity<>(personaOPT.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(new Mensaje("No existe esa persona."), HttpStatus.NOT_FOUND);
        }
    }

    // Agregar nueva persona
    @PostMapping("/Personas")
    public ResponseEntity<?> agregarPersona(@Valid @RequestBody PersonaDTO personaDTO) {
        try {
            System.out.println("🔹 Iniciando creación de persona...");
            System.out.println("📩 Datos recibidos en PersonaDTO: " + personaDTO);

            // Validar si ya existe
            if (personaService.existsPersonaByDocumento(personaDTO.getDocumentoPersona())) {
                System.out.println("⚠️ Ya existe una persona con el documento: " + personaDTO.getDocumentoPersona());
                return new ResponseEntity<>(new Mensaje("Ya existe una persona con ese documento"), HttpStatus.BAD_REQUEST);
            }

            // Buscar entidades relacionadas
            System.out.println("🔍 Buscando entidades relacionadas...");
            TipoDocumento tipoDocumento = tipoDocumentoRepository.findById(personaDTO.getIdTipoDocumento())
                    .orElseThrow(() -> new RuntimeException("Tipo de documento no encontrado"));

            TipoPersona tipoPersona = tipoPersonaRepository.findById(personaDTO.getIdTipoPersona())
                    .orElseThrow(() -> new RuntimeException("Tipo de persona no encontrado"));

            Ciudad ciudad = ciudadRepository.findById(personaDTO.getIdCiudad())
                    .orElseThrow(() -> new RuntimeException("Ciudad no encontrada"));

            System.out.println("✅ Entidades encontradas:");
            System.out.println("   TipoDocumento: " + tipoDocumento.getNombreTipoDocumento());
            System.out.println("   TipoPersona: " + tipoPersona.getNombreTipoPersona());
            System.out.println("   Ciudad: " + ciudad.getCiudad());

            // Crear persona
            Persona persona = new Persona();
            persona.setNombre(personaDTO.getNombre());
            persona.setApellido(personaDTO.getApellido());
            persona.setSegundoApellido(personaDTO.getSegundoApellido());
            persona.setTipoDocumento(tipoDocumento);
            persona.setDocumentoPersona(personaDTO.getDocumentoPersona());
            persona.setDireccion(personaDTO.getDireccion());
            persona.setTelefono(personaDTO.getTelefono());
            persona.setEmail(personaDTO.getEmail());
            persona.setCiudad(ciudad);
            persona.setTipoPersona(tipoPersona);
            persona.setActivo(true);

            System.out.println("📝 Persona antes de guardar: " + persona);

            // Guardar persona
            Persona personaGuardada = personaService.guardarPersona(persona);
            System.out.println("✅ Persona guardada con ID: " + personaGuardada.getIdPersona());

            // Guardar roles
            if (personaDTO.getIdsRoles() != null && !personaDTO.getIdsRoles().isEmpty()) {
                System.out.println("🎭 Roles recibidos: " + personaDTO.getIdsRoles());
                for (Integer idRol : personaDTO.getIdsRoles()) {
                    RolPersona rol = rolPersonaRepository.findById(idRol)
                            .orElseThrow(() -> new RuntimeException("Rol no encontrado: " + idRol));

                    PersonaRol personaRol = new PersonaRol();
                    personaRol.setPersona(personaGuardada);
                    personaRol.setRolPersona(rol);
                    personaRol.setFechaAsignacion(java.time.LocalDate.now());
                    personaRol.setActivo(true);

                    personaRolRepository.save(personaRol);
                    System.out.println("✅ Rol asignado: " + rol.getNombreRol() +
                            " → Persona ID: " + personaGuardada.getIdPersona());
                }
            } else {
                System.out.println("ℹ️ No se recibieron roles para asignar.");
            }

            System.out.println("🎉 Persona creada exitosamente con sus roles.");
            return new ResponseEntity<>(new Mensaje("Persona creada con éxito y roles asignados."), HttpStatus.CREATED);

        } catch (Exception e) {
            System.out.println("❌ Error al crear persona:");
            e.printStackTrace();
            return new ResponseEntity<>(new Mensaje("Error al crear la persona: " + e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }



    // Actualizar persona existente
    @PutMapping("/Personas/{id}")
    public ResponseEntity<?> actualizarPersona(@PathVariable int id, @Valid @RequestBody PersonaDTO personaDTO) {
        try {
            Optional<Persona> personaOPT = personaService.buscarPersonaID(id);
            if (!personaOPT.isPresent()) {
                return new ResponseEntity<>(new Mensaje("No existe esa persona."), HttpStatus.NOT_FOUND);
            }

            Persona persona = personaOPT.get();

            // Buscar entidades relacionadas
            TipoDocumento tipoDocumento = tipoDocumentoRepository.findById(personaDTO.getIdTipoDocumento())
                    .orElseThrow(() -> new RuntimeException("Tipo de documento no encontrado"));

            TipoPersona tipoPersona = tipoPersonaRepository.findById(personaDTO.getIdTipoPersona())
                    .orElseThrow(() -> new RuntimeException("Tipo de persona no encontrado"));

            Ciudad ciudad = ciudadRepository.findById(personaDTO.getIdCiudad())
                    .orElseThrow(() -> new RuntimeException("Ciudad no encontrada"));

            // Actualizar campos de la persona
            persona.setNombre(personaDTO.getNombre());
            persona.setApellido(personaDTO.getApellido());
            persona.setSegundoApellido(personaDTO.getSegundoApellido());
            persona.setTipoDocumento(tipoDocumento);
            persona.setDocumentoPersona(personaDTO.getDocumentoPersona());
            persona.setDireccion(personaDTO.getDireccion());
            persona.setTelefono(personaDTO.getTelefono());
            persona.setEmail(personaDTO.getEmail());
            persona.setCiudad(ciudad);
            persona.setTipoPersona(tipoPersona);

            Persona personaActualizada = personaService.guardarPersona(persona);

            // Actualizar roles sin eliminar registros
            List<PersonaRol> rolesActuales = personaRolRepository.findByPersona_IdPersona(personaActualizada.getIdPersona());

            // Convertimos la lista actual en un set de IDs para comparar
            Set<Integer> nuevosRoles = new HashSet<>(personaDTO.getIdsRoles() != null ? personaDTO.getIdsRoles() : List.of());

            // Desactivar los roles que ya no están seleccionados
            for (PersonaRol pr : rolesActuales) {
                int idRolActual = pr.getRolPersona().getIdRolPersona();
                if (!nuevosRoles.contains(idRolActual)) {
                    pr.setActivo(false);
                    personaRolRepository.save(pr);
                }
            }

            // Activar o crear los roles seleccionados
            for (Integer idRol : nuevosRoles) {
                Optional<PersonaRol> existente = rolesActuales.stream()
                        .filter(pr -> pr.getRolPersona().getIdRolPersona() == idRol)
                        .findFirst();

                if (existente.isPresent()) {
                    PersonaRol pr = existente.get();
                    pr.setActivo(true); // reactivar si estaba desactivado
                    personaRolRepository.save(pr);
                } else {
                    RolPersona rol = rolPersonaRepository.findById(idRol)
                            .orElseThrow(() -> new RuntimeException("Rol no encontrado: " + idRol));

                    PersonaRol nuevo = new PersonaRol();
                    nuevo.setPersona(personaActualizada);
                    nuevo.setRolPersona(rol);
                    nuevo.setFechaAsignacion(LocalDate.now());
                    nuevo.setActivo(true);
                    personaRolRepository.save(nuevo);
                }
            }

            return new ResponseEntity<>(new Mensaje("Persona y roles actualizados correctamente."), HttpStatus.OK);

        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(new Mensaje("Error al actualizar la persona: " + e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }


    // Eliminar persona (cambio de estado o eliminación lógica)
    @DeleteMapping("/Personas/{id}")
    public ResponseEntity<?> eliminarPersona(@PathVariable int id) {
        try {
            Optional<Persona> personaOPT = personaService.buscarPersonaID(id);
            if (!personaOPT.isPresent()) {
                return new ResponseEntity<>(new Mensaje("No existe la persona seleccionada"), HttpStatus.NOT_FOUND);
            }

            personaService.eliminarPersona(id);
            return new ResponseEntity<>(new Mensaje("Se eliminó la persona con éxito"), HttpStatus.OK);

        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(new Mensaje("Error interno al eliminar la persona"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}*/

import com.cristhian.SistemaInventario.DTO.CiudadDTO;
import com.cristhian.SistemaInventario.DTO.PersonaDTO;
import com.cristhian.SistemaInventario.Mensaje.Mensaje;
import com.cristhian.SistemaInventario.Modelo.Ciudad;
import com.cristhian.SistemaInventario.Modelo.Persona;
import com.cristhian.SistemaInventario.Service.IPersonaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/Inventario")
@CrossOrigin(origins = "http://localhost:4200")
public class PersonaController {

    private final IPersonaService personaService;

    public PersonaController(IPersonaService personaService) {
        this.personaService = personaService;
    }

    /*@PostMapping("/Personas")
    public ResponseEntity<?> crearPersona(@Valid @RequestBody PersonaDTO personaDto) {
        System.out.println("DEBUG DTO completo: " + personaDto);
        System.out.println("DEBUG tipoDocumento: " + personaDto.getIdTipoDocumento());
        System.out.println("DEBUG tipoPersona: " + personaDto.getIdTipoPersona());
        System.out.println("DEBUG ciudad: " + personaDto.getIdCiudad());
        System.out.println("DEBUG idsRoles: " + personaDto.getIdsRoles());
        personaService.crearPersonaConRolDefault(personaDto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new Mensaje("Persona creada correctamente"));
    }

    @PutMapping("/Personas/{id}")
    public ResponseEntity<?> actualizarPersona(@PathVariable Integer id,
                                               @Valid @RequestBody PersonaDTO dto) {
        personaService.actualizarPersona(id, dto);
        return ResponseEntity.ok(new Mensaje("Persona actualizada correctamente"));
    }

    @GetMapping("/Personas")
    public ResponseEntity<List<Persona>> listarPersonas() {
        return ResponseEntity.ok(personaService.listarPersonas());
    }

    @GetMapping("/Personas/{id}")
    public ResponseEntity<?> obtenerPersona(@PathVariable Integer id) {
        return ResponseEntity.ok(personaService.buscarPorId(id));
    }

    @DeleteMapping("/Personas/{id}")
    public ResponseEntity<?> eliminarPersona(@PathVariable Integer id){
        personaService.eliminarPersona(id);
        return new ResponseEntity<>(new Mensaje("Se eliminó la persona con éxito"), HttpStatus.OK);
    }*/

    @GetMapping("/Personas")
    public ResponseEntity<List<PersonaDTO>> listarPersona(){
        List<PersonaDTO> response = personaService.listarPersonas().stream()
                .map(PersonaDTO :: new).toList(); // mapeo entidad → DTO
        return ResponseEntity.ok(response);
    }

    @GetMapping("/Personas/{id}")
    public ResponseEntity<?> obtenerPersona(@PathVariable int id){
        Optional<Persona> data = personaService.buscarPorId(id);

        if (data.isEmpty()) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(new Mensaje("No existe el registro buscado"));
        }
        return ResponseEntity.ok(new PersonaDTO(data.get()));

    }

    @PostMapping("/Personas")
    public ResponseEntity<?> crearPersona(@Valid @RequestBody PersonaDTO personaDTO) {

        try{
            var persona = personaService.crearPersonaConRolDefault(personaDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(new PersonaDTO(persona));
        }catch (IllegalArgumentException e){
            return ResponseEntity.badRequest().body(new Mensaje(e.getMessage()));
        }
    }

    @PutMapping("/Personas/{id}")
    public ResponseEntity<?> actualizarPersona(@PathVariable int id, @RequestBody PersonaDTO personaDTO){
        try{
            var actualizado = personaService.actualizarPersona(id, personaDTO);
            return ResponseEntity.status(HttpStatus.OK).body(new PersonaDTO(actualizado));
        }catch (IllegalArgumentException e){
            return ResponseEntity.badRequest().body(new Mensaje(e.getMessage()));
        }

    }

    @DeleteMapping("/Personas/{id}")
    public ResponseEntity<?> eliminarPersona(@PathVariable int id){
        try{
            personaService.eliminarPersona(id);
            return ResponseEntity.status(HttpStatus.OK).body(new Mensaje("Persona eliminada con éxito"));
        }catch (IllegalArgumentException e){
            return ResponseEntity.badRequest().body(new Mensaje(e.getMessage()));
        }
    }
}