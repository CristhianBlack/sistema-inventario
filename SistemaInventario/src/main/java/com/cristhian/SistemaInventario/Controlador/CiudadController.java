package com.cristhian.SistemaInventario.Controlador;

import com.cristhian.SistemaInventario.DTO.CategoriaDTO;
import com.cristhian.SistemaInventario.DTO.CiudadDTO;
import com.cristhian.SistemaInventario.Mensaje.Mensaje;
import com.cristhian.SistemaInventario.Modelo.Ciudad;
import com.cristhian.SistemaInventario.Service.ICiudadService;
import com.cristhian.SistemaInventario.ServicioImplement.CiudadServiceImpl;
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
public class CiudadController {


    private static final Logger logger = LoggerFactory.getLogger(CiudadController.class);
    private final ICiudadService ciudadService;

    public CiudadController(CiudadServiceImpl ciudadService) {
        this.ciudadService = ciudadService;
    }

    @GetMapping("/Ciudades")
    public ResponseEntity<List<CiudadDTO>> listarCiudad(){
        List<CiudadDTO> response = ciudadService.listarCiudadesActivas().stream()
                .map(CiudadDTO :: new).toList(); // mapeo entidad → DTO
        return ResponseEntity.ok(response);
    }

    @GetMapping("/Ciudades/{id}")
    public ResponseEntity<?> obtenerCiudadPorId(@PathVariable int id){
            Optional<Ciudad> data = ciudadService.buscarCiudadId(id);

            if (data.isEmpty()) {
                return ResponseEntity
                        .status(HttpStatus.NOT_FOUND)
                        .body(new Mensaje("No existe el registro buscado"));
            }
            return ResponseEntity.ok(new CiudadDTO(data.get()));

    }

    @PostMapping("/Ciudades")
    public ResponseEntity<?> agregarCiudad(@Valid @RequestBody CiudadDTO ciudadDTO) {

        try{
            var ciudad = ciudadService.guardarCiudad(ciudadDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(new CiudadDTO(ciudad));
        }catch (IllegalArgumentException e){
            return ResponseEntity.badRequest().body(new Mensaje(e.getMessage()));
        }
    }

    @PutMapping("/Ciudades/{id}")
    public ResponseEntity<?> actualizarCiudad(@PathVariable int id, @RequestBody CiudadDTO ciudadDTO){
        try{
            var actualizado = ciudadService.actualizarCiudad(id, ciudadDTO);
            return ResponseEntity.status(HttpStatus.OK).body(new CiudadDTO(actualizado));
        }catch (IllegalArgumentException e){
            return ResponseEntity.badRequest().body(new Mensaje(e.getMessage()));
        }

    }

    @DeleteMapping("/Ciudades/{id}")
    public ResponseEntity<?> eliminarCiudad(@PathVariable int id){
        try{
            ciudadService.eliminarCiudad(id);
            return ResponseEntity.status(HttpStatus.OK).body(new Mensaje("Ciudad eliminada con éxito"));
        }catch (IllegalArgumentException e){
            return ResponseEntity.badRequest().body(new Mensaje(e.getMessage()));
        }
    }
}
