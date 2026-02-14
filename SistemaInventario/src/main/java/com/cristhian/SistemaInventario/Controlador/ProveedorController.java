package com.cristhian.SistemaInventario.Controlador;

import com.cristhian.SistemaInventario.DTO.CiudadDTO;
import com.cristhian.SistemaInventario.DTO.ProveedorPersonaDTO;
import com.cristhian.SistemaInventario.Mensaje.Mensaje;
import com.cristhian.SistemaInventario.DTO.ProveedorDTO;
import com.cristhian.SistemaInventario.Modelo.Ciudad;
import com.cristhian.SistemaInventario.Modelo.Proveedor;
import com.cristhian.SistemaInventario.Repositorio.ProveedorRepository;
import com.cristhian.SistemaInventario.ServicioImplement.CiudadServiceImpl;
import com.cristhian.SistemaInventario.ServicioImplement.ProveedorServiceImpl;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/Inventario")
@CrossOrigin(origins = "http://localhost:4200")
public class ProveedorController {

    private final ProveedorServiceImpl proveedorService;
    private final CiudadServiceImpl ciudadService;
    private final ProveedorRepository proveedorRepository;

    public ProveedorController(ProveedorServiceImpl proveedorService, CiudadServiceImpl ciudadService,
                               ProveedorRepository proveedorRepository) {
        this.proveedorService = proveedorService;
        this.ciudadService = ciudadService;
        this.proveedorRepository = proveedorRepository;
    }

    // @Autowired
    //private ProveedorService proveedorService;
    //@Autowired
    //private CiudadService ciudadService;

    @GetMapping("/Proveedores")
    public ResponseEntity<List<ProveedorPersonaDTO>> listarProveedor() {
        return ResponseEntity.ok(
                proveedorService.obtenerListadoProveedorPersona()
        );
    }

    @GetMapping("/Proveedores/{id}")
    public ResponseEntity<?> obtenerProveedorPorId(@PathVariable int id){
        Optional<Proveedor> data = proveedorService.buscarProveedorId(id);

        if (data.isEmpty()) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(new Mensaje("No existe el registro buscado"));
        }
        return ResponseEntity.ok(new ProveedorDTO(data.get()));
    }

    @PostMapping("/Proveedor")
    public ResponseEntity<?> agregarProveedor(@Valid @RequestBody ProveedorDTO proveedorDTO){
        try{
            var proveedor = proveedorService.guardarProveedor(proveedorDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(new ProveedorDTO(proveedor));
        }catch (IllegalArgumentException e){
            return ResponseEntity.badRequest().body(new Mensaje(e.getMessage()));
        }
    }

    @PutMapping("/Proveedores/{id}")
    public ResponseEntity<?> actualizarProveedor(@PathVariable("id") int id,@Valid @RequestBody ProveedorDTO proveedorDTO){
        try{
            var actualizado = proveedorService.actualizarProveedor(id, proveedorDTO);
            return ResponseEntity.status(HttpStatus.OK).body(new ProveedorDTO(actualizado));
        }catch (IllegalArgumentException e){
            return ResponseEntity.badRequest().body(new Mensaje(e.getMessage()));
        }
    }

    @DeleteMapping("/Proveedores/{id}")
    public ResponseEntity<?> eliminarProveedor(@PathVariable int id){
        try{
            proveedorService.borrarProveedor(id);
            return ResponseEntity.status(HttpStatus.OK).body(new Mensaje("Proveedor eliminado con éxito"));
        }catch (IllegalArgumentException e){
            return ResponseEntity.badRequest().body(new Mensaje(e.getMessage()));
        }
    }

    @GetMapping("/Proveedores-persona")
    public ResponseEntity<List<ProveedorPersonaDTO>> listarProveedorPersona() {
        return ResponseEntity.ok(proveedorService.obtenerListadoProveedorPersona());
    }

}


