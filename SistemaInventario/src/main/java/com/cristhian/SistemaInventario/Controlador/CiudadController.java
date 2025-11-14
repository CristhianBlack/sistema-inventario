package com.cristhian.SistemaInventario.Controlador;

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
    public ResponseEntity<List<Ciudad>> listarCiudad(){
        List<Ciudad> listado = ciudadService.listarCiudadesActivas();
        return new ResponseEntity<List<Ciudad>>(listado, HttpStatus.OK);
    }

    @GetMapping("/Ciudades/{id}")
    public ResponseEntity<Ciudad> obtenerCiudadPorId(@PathVariable int id){
        Optional<Ciudad> ciudad = ciudadService.buscarCiudadId(id);
        if(ciudad.isPresent()){
            return new ResponseEntity<>(ciudad.get(), HttpStatus.OK);
        }else{
            return new ResponseEntity(new Mensaje("No existe la ciudad"), HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/Ciudades")
    public ResponseEntity<?> agregarCiudad(@Valid @RequestBody CiudadDTO ciudadDTO) {
        logger.info(" JSON recibido → ciudad: {}, activo: {}", ciudadDTO.getCiudad(), ciudadDTO.isActivo());
        logger.info("ciudad a agregar: " + ciudadDTO.getCiudad());

        Optional<Ciudad> ciudadexistente = ciudadService.findByCiudadIgnoreCase(ciudadDTO.getCiudad().trim());
        if (ciudadexistente.isPresent()) {
            logger.info("reviso si la ciudad esta presente "+String.valueOf(ciudadexistente));
            Ciudad ciudad = ciudadexistente.get();
            if (!ciudad.isActivo()) {
                logger.info("reviso si esta activo "+ ciudad.isActivo());
                ciudad.setActivo(true);
                ciudadService.guardarCiudad(ciudad);
                logger.info("entro al if y debria decir ciudad creada");
                return new ResponseEntity<>(new Mensaje("Ciudad reactivada con exito"), HttpStatus.OK);

            } else {
                logger.info("entro al else y deberia decir ya exite la ciudad");
                return new ResponseEntity<>(new Mensaje("Ya existe una ciudad con ese nombre"), HttpStatus.BAD_REQUEST);
            }
        } else {
            logger.info("Entro al else de crear ciudad con exito");
            Ciudad ciudad = new Ciudad();
            ciudad.setCiudad(ciudadDTO.getCiudad());
            ciudadService.guardarCiudad(ciudad);
            return new ResponseEntity(new Mensaje("Ciudad Creada con exito"), HttpStatus.CREATED);
       }
    }

    @PutMapping("/Ciudades/{id}")
    public ResponseEntity<?> actualizarCiudad(@PathVariable int id, @RequestBody CiudadDTO ciudadDTO){

        Optional<Ciudad> ciudadOpt = ciudadService.buscarCiudadId(id);

        if(!ciudadOpt.isPresent()){
            return new ResponseEntity(new Mensaje("No existe la ciudad"), HttpStatus.NOT_FOUND);
        }

        Ciudad ciudad = ciudadOpt.get();
        ciudad.setCiudad(ciudadDTO.getCiudad());
        ciudad.setActivo(ciudadDTO.isActivo());

        ciudadService.guardarCiudad(ciudad);
        return new ResponseEntity(new Mensaje("Se actualizo con exito la ciudad"), HttpStatus.OK);
    }

    @DeleteMapping("/Ciudades/{id}")
    public ResponseEntity<?> eliminarCiudad(@PathVariable int id){
        try {
            Optional<Ciudad> ciudadOpt = ciudadService.buscarCiudadId(id);

            if (!ciudadOpt.isPresent()) {
                // Si la ciudad no existe, retornamos 404
                return new ResponseEntity<>(new Mensaje("No existe la ciudad seleccionada"), HttpStatus.NOT_FOUND);
            }

            ciudadService.eliminarCiudad(id);
            return new ResponseEntity<>(new Mensaje("Se elimino la ciudad con exito"),HttpStatus.OK);
        }catch (Exception e){
            e.printStackTrace(); // para ver el error exacto en la consola
            return new ResponseEntity<>(new Mensaje("Error interno al eliminar la ciudad"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
