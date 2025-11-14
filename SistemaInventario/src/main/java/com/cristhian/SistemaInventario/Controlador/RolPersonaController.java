package com.cristhian.SistemaInventario.Controlador;

import com.cristhian.SistemaInventario.DTO.PersonaDTO;
import com.cristhian.SistemaInventario.DTO.RolPersonaDTO;
import com.cristhian.SistemaInventario.Mensaje.Mensaje;
import com.cristhian.SistemaInventario.Modelo.Ciudad;
import com.cristhian.SistemaInventario.Modelo.RolPersona;
import com.cristhian.SistemaInventario.Service.IRolPersonaService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/Inventario")
@CrossOrigin(origins = "http://localhost:4200")
public class RolPersonaController {

    private static final Logger logger = LoggerFactory.getLogger(CiudadController.class);
    private final IRolPersonaService rolPersonaService;

    public RolPersonaController(IRolPersonaService rolPersonaService) {
        this.rolPersonaService = rolPersonaService;
    }

    @GetMapping("/rolPersonas")
    public ResponseEntity<List<RolPersona>> listarRolesPersonas(){
        List<RolPersona> rolesPersona = rolPersonaService.listarRolPersonaActivo();
        return new ResponseEntity<List<RolPersona>>(rolesPersona, HttpStatus.OK);
    }

    @GetMapping("/rolPersonas/{id}")
    public ResponseEntity<RolPersona> obtenerRolPersonaPorId(@PathVariable int id){
        Optional<RolPersona> rolpersona = rolPersonaService.buscarRolPersonaId(id);
        if(!rolpersona.isPresent()){
            return new ResponseEntity(new Mensaje("No existe el rol buscado"), HttpStatus.NOT_FOUND);
        }else {
            return new ResponseEntity(rolpersona.get(), HttpStatus.OK);
        }
    }

    @PostMapping("/rolPersonas")
    public ResponseEntity<?> guardarRolPersona(@Valid @RequestBody RolPersonaDTO rolPersonaDTO){
        logger.info(" JSON recibido → rol Persona: {}, activo: {}", rolPersonaDTO.getNombreRol(), rolPersonaDTO.isActivo());
        logger.info("rol persona a agregar: " + rolPersonaDTO.getNombreRol());

        Optional<RolPersona> rolPersonaexistente = rolPersonaService.findByNombreRolIgnoreCase(rolPersonaDTO.getNombreRol().trim());
        if (rolPersonaexistente.isPresent()) {
            logger.info("reviso si el rol esta presente "+String.valueOf(rolPersonaexistente));
            RolPersona rolPersona = rolPersonaexistente.get();
            if (!rolPersona.isActivo()) {
                logger.info("reviso si esta activo "+ rolPersona.isActivo());
                rolPersona.setActivo(true);
                rolPersonaService.guardarRolPersona(rolPersona);
                logger.info("entro al if y debria decir rol persona creado");
                return new ResponseEntity<>(new Mensaje("Rol reactivado con exito"), HttpStatus.OK);

            } else {
                logger.info("entro al else y deberia decir ya exite el rol");
                return new ResponseEntity<>(new Mensaje("Ya existe un rol con ese nombre"), HttpStatus.BAD_REQUEST);
            }
        }else{
            logger.info("Entro al else de crear rol persona con exito");
            RolPersona rolPersona = new RolPersona();
            rolPersona.setNombreRol(rolPersonaDTO.getNombreRol());
            rolPersona.setDescripcion(rolPersonaDTO.getDescripcion());
            rolPersonaService.guardarRolPersona(rolPersona);
            return new ResponseEntity(new Mensaje("El rol ha sido creado con exito."), HttpStatus.CREATED);
        }
    }

    @PutMapping("/rolPersonas/{id}")
    public ResponseEntity<?> actualizarRolPersona(@PathVariable int id, @RequestBody RolPersonaDTO rolPersonaDTO){

        Optional<RolPersona> rolPersonaOPT = rolPersonaService.buscarRolPersonaId(id);
        if(!rolPersonaOPT.isPresent()){
            return new ResponseEntity(new Mensaje("No existe El rol"), HttpStatus.NOT_FOUND);
        }else {
            RolPersona rolPersona = rolPersonaOPT.get();
            rolPersona.setNombreRol(rolPersonaDTO.getNombreRol());
            rolPersona.setDescripcion(rolPersonaDTO.getDescripcion());
            rolPersonaService.guardarRolPersona(rolPersona);
            return new ResponseEntity(new Mensaje("Se actualizo con exito el rol."), HttpStatus.OK);
        }
    }

    @DeleteMapping("/rolPersonas/{id}")
    public ResponseEntity<?> eliminarRolPersona(@PathVariable int id){
        try {
            Optional<RolPersona> rolPersonaOPT = rolPersonaService.buscarRolPersonaId(id);

            if (!rolPersonaOPT.isPresent()) {
                // Si el rol no existe, retornamos 404
                return new ResponseEntity<>(new Mensaje("No existe el rol seleccionada"), HttpStatus.NOT_FOUND);
            }

            rolPersonaService.eliminarRolPersona(id);
            return new ResponseEntity<>(new Mensaje("Se elimino el rol con exito"),HttpStatus.OK);
        }catch (Exception e){
            e.printStackTrace(); // para ver el error exacto en la consola
            return new ResponseEntity<>(new Mensaje("Error interno al eliminar el rol"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
