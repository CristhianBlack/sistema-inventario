package com.cristhian.SistemaInventario.Controlador;

import com.cristhian.SistemaInventario.DTO.CiudadDTO;
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
    public ResponseEntity<List<RolPersonaDTO>> listarRolesPersonas(){
        List<RolPersonaDTO> response = rolPersonaService.listarRolPersonaActivo().stream()
                .map(RolPersonaDTO :: new).toList(); // mapeo entidad → DTO
        return ResponseEntity.ok(response);
    }

    @GetMapping("/rolPersonas/{id}")
    public ResponseEntity<?> obtenerRolPersonaPorId(@PathVariable int id){
        Optional<RolPersona> data = rolPersonaService.buscarRolPersonaId(id);

        if (data.isEmpty()) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(new Mensaje("No existe el registro buscado"));
        }
        return ResponseEntity.ok(new RolPersonaDTO(data.get()));
    }

    @PostMapping("/rolPersonas")
    public ResponseEntity<?> guardarRolPersona(@Valid @RequestBody RolPersonaDTO rolPersonaDTO){
        try{
            var rol = rolPersonaService.guardarRolPersona(rolPersonaDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(new RolPersonaDTO(rol));
        }catch (IllegalArgumentException e){
            return ResponseEntity.badRequest().body(new Mensaje(e.getMessage()));
        }
    }

    @PutMapping("/rolPersonas/{id}")
    public ResponseEntity<?> actualizarRolPersona(@PathVariable int id, @RequestBody RolPersonaDTO rolPersonaDTO){
        try{
            var actualizado = rolPersonaService.actualizarRolPersona(id, rolPersonaDTO);
            return ResponseEntity.status(HttpStatus.OK).body(new RolPersonaDTO(actualizado));
        }catch (IllegalArgumentException e){
            return ResponseEntity.badRequest().body(new Mensaje(e.getMessage()));
        }
    }

    @DeleteMapping("/rolPersonas/{id}")
    public ResponseEntity<?> eliminarRolPersona(@PathVariable int id){
        try{
            rolPersonaService.eliminarRolPersona(id);
            return ResponseEntity.status(HttpStatus.OK).body(new Mensaje("Rol eliminado con éxito"));
        }catch (IllegalArgumentException e){
            return ResponseEntity.badRequest().body(new Mensaje(e.getMessage()));
        }
    }
}
