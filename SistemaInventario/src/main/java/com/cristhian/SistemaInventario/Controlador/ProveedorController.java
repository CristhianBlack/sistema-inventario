package com.cristhian.SistemaInventario.Controlador;

import com.cristhian.SistemaInventario.Mensaje.Mensaje;
import com.cristhian.SistemaInventario.DTO.ProveedorDTO;
import com.cristhian.SistemaInventario.Modelo.Ciudad;
import com.cristhian.SistemaInventario.Modelo.Proveedor;
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
    private  final CiudadServiceImpl ciudadService;

    public ProveedorController(ProveedorServiceImpl proveedorService, CiudadServiceImpl ciudadService) {
        this.proveedorService = proveedorService;
        this.ciudadService = ciudadService;
    }

    // @Autowired
    //private ProveedorService proveedorService;
    //@Autowired
    //private CiudadService ciudadService;

    @GetMapping("/Proveedores")
    public ResponseEntity<List<Proveedor>> listarProveedor(){
        List<Proveedor> listado = proveedorService.listarProveedoresActivos();
        return new ResponseEntity(listado, HttpStatus.OK);
    }

    @GetMapping("/Proveedores/{id}")
    public ResponseEntity<Proveedor> obtenerProveedorPorId(@PathVariable int id){
        Optional<Proveedor> proveedor = proveedorService.buscarProveedorId(id);
        if(!proveedor.isPresent()){
            return new ResponseEntity(new Mensaje("No Existe el proveedor"), HttpStatus.NOT_FOUND);
        }else{
            return new ResponseEntity<>(proveedor.get(), HttpStatus.OK);

        }
    }

    @PostMapping("/Proveedores")
    public ResponseEntity<?> agregarProveedor(@Valid @RequestBody ProveedorDTO proveedorDTO){
        System.out.println("proveedor a agregar: "+proveedorDTO);

        // Validamos de que la ciudad exista en la base dedatos
        Optional<Ciudad> ciudadOptional = ciudadService.buscarCiudadId(proveedorDTO.getIdCiudad());

        if(!ciudadOptional.isPresent()){
            return new ResponseEntity<>(new Mensaje("No existe la ciudad"), HttpStatus.BAD_REQUEST);
        }

        Proveedor proveedor = new Proveedor();
        proveedor.setRazonSocial(proveedorDTO.getRazonSocial());
        proveedor.setNitProveedor(proveedorDTO.getNitProveedor());
        proveedor.setDescripcionProveedor(proveedorDTO.getDescripcionProveedor());
        proveedor.setFechaCreacion(proveedorDTO.getFechaCreacion());
        proveedor.setCiudad(ciudadOptional.get());

        proveedorService.guardarProveedor(proveedor);
        return new ResponseEntity<>(new Mensaje("Proveedor registrado con exito"), HttpStatus.CREATED);
    }

    @PutMapping("/Proveedores/{id}")
    public ResponseEntity<?> actualizarProveedor(@PathVariable("id") int id,@Valid @RequestBody ProveedorDTO proveedorDTO){

        Optional<Proveedor> proveedor = proveedorService.buscarProveedorId(id);

        if(!proveedor.isPresent()){
            return new ResponseEntity<>(new Mensaje("No existe ese proveedor registrado"), HttpStatus.NOT_FOUND);
        }

        // Validamos de que la ciudad exista en la base dedatos
        Optional<Ciudad> ciudadOptional = ciudadService.buscarCiudadId(proveedorDTO.getIdCiudad());

        if(!ciudadOptional.isPresent()){
            return new ResponseEntity<>(new Mensaje("No existe la ciudad"), HttpStatus.BAD_REQUEST);
        }

        Proveedor proveedores = proveedor.get();
        proveedores.setRazonSocial(proveedorDTO.getRazonSocial());
        proveedores.setNitProveedor(proveedorDTO.getNitProveedor());
        proveedores.setDescripcionProveedor(proveedorDTO.getDescripcionProveedor());
        proveedores.setFechaCreacion(proveedorDTO.getFechaCreacion());
        proveedores.setCiudad(ciudadOptional.get());// obtiene el id de la cidad si esxite

        this.proveedorService.guardarProveedor(proveedores);
        return new ResponseEntity(new Mensaje("Se actualizo el proveedor con exito."), HttpStatus.OK);
    }

    @DeleteMapping("/Proveedores/{id}")
    public ResponseEntity<?> eliminarProveedor(@PathVariable int id){
        try{
            Optional<Proveedor> proveedor = proveedorService.buscarProveedorId(id);
            if(!proveedor.isPresent()){
                return new ResponseEntity<>(new Mensaje("No existe el proveedor"), HttpStatus.NOT_FOUND);
            }else{
                proveedorService.borrarProveedor(id);
                return new ResponseEntity<>(new Mensaje("Proveedor eliminado con exito"), HttpStatus.OK);
            }
        }catch (Exception e){
            e.printStackTrace();
            return new ResponseEntity<>(new Mensaje("Error interno al eliminar el proveedor"), HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }
}
