package com.cristhian.SistemaInventario.Controlador;

import com.cristhian.SistemaInventario.DTO.UnidadMedidaDTO;
import com.cristhian.SistemaInventario.Mensaje.Mensaje;
import com.cristhian.SistemaInventario.Modelo.UnidadMedida;
import com.cristhian.SistemaInventario.ServicioImplement.UnidadMedidaServiceImpl;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/Inventario")
@CrossOrigin(origins = "http://localhost:4200")
public class UnidadMedidaController {


    private UnidadMedidaServiceImpl unidadMedidaService;

    public UnidadMedidaController(UnidadMedidaServiceImpl unidadMedidaService) {
        this.unidadMedidaService = unidadMedidaService;
    }

    @GetMapping("/Unidades")
    public ResponseEntity<List<UnidadMedida>> listadoUnidades(){
        List<UnidadMedida> listado = unidadMedidaService.listarUnidades();
        return new ResponseEntity(listado, HttpStatus.OK);
    }

    @GetMapping("/Unidades/{id}")
    public ResponseEntity<UnidadMedida> buscarUnidadPorId(@PathVariable int id){
        Optional<UnidadMedida> unidadMedida = unidadMedidaService.buscarUnidadId(id);
        if (unidadMedida.isPresent()){
            return new ResponseEntity<>(unidadMedida.get(), HttpStatus.OK);
        }else{
            return new ResponseEntity(new Mensaje("No existe el regitro buscado"), HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/Unidades")
    public ResponseEntity<?> agregarUnidades(@Valid @RequestBody UnidadMedidaDTO unidadMedidaDTO){

        String nombre = unidadMedidaDTO.getNombreMedida() == null ? "" : unidadMedidaDTO.getNombreMedida().trim();
        if (nombre.isEmpty()) {
            return new ResponseEntity<>(new Mensaje("El nombre de la unidad es obligatorio"), HttpStatus.BAD_REQUEST);
        }

        // Usar exists simple (puedes usar normalized exists si lo prefieres)
        if (unidadMedidaService.existsByNombreMedida(nombre)) {
            return new ResponseEntity<>(new Mensaje("Ya existe esa unidad de medida registrada"), HttpStatus.BAD_REQUEST);
        }

        UnidadMedida unidadMedida = new UnidadMedida();
        unidadMedida.setNombreMedida(nombre);
        unidadMedida.setSigla(unidadMedidaDTO.getSigla() == null ? null : unidadMedidaDTO.getSigla().trim());

        unidadMedidaService.guardar(unidadMedida);
        return new ResponseEntity<>(new Mensaje("Unidad de medida registrada con éxito"), HttpStatus.CREATED);
    }


    @PutMapping("/Unidades/{id}")
    public ResponseEntity<?> actualizarUnidadeMedida(@PathVariable int id, @RequestBody UnidadMedidaDTO unidadMedidaDTO) {
        Optional<UnidadMedida> unidadMedidaOpt = unidadMedidaService.buscarUnidadId(id);

        if (!unidadMedidaOpt.isPresent()) {
            return new ResponseEntity<>(new Mensaje("No existe esa unidad de medida"), HttpStatus.NOT_FOUND);
        }

        String nuevoNombre = unidadMedidaDTO.getNombreMedida().trim();
        String siglaNueva = unidadMedidaDTO.getSigla().trim();

        System.out.println("---- Depuración actualización unidad ----");
        System.out.println("ID actual: " + id);
        System.out.println("Nuevo nombre: " + unidadMedidaDTO.getNombreMedida());

        if (unidadMedidaService.existeOtraUnidadConMismoNombre(id, unidadMedidaDTO.getNombreMedida())) {
            System.out.println("→ Ya existe otra unidad con ese nombre");
            return new ResponseEntity<>(new Mensaje("Ya existe otra unidad de medida con ese nombre"), HttpStatus.BAD_REQUEST);
        }

        UnidadMedida unidadMedida = unidadMedidaOpt.get();
        unidadMedida.setNombreMedida(nuevoNombre);
        unidadMedida.setSigla(siglaNueva);

        unidadMedidaService.guardar(unidadMedida);

        return new ResponseEntity<>(new Mensaje("Se actualizó la unidad de medida con éxito"), HttpStatus.OK);
    }





    @DeleteMapping("/Unidades/{id}")
    public ResponseEntity<?> eliminarUnidadMedida(@PathVariable int id){
        try {
            Optional<UnidadMedida> unidadMedida = unidadMedidaService.buscarUnidadId(id);
            if(unidadMedida.isPresent()){
                unidadMedidaService.borrar(id);
                return new ResponseEntity<>(new Mensaje("Unidad de medida eliminada con exito"), HttpStatus.OK);
            }else{
                return new ResponseEntity<>(new Mensaje("No existe la unidad de medida"), HttpStatus.NOT_FOUND);
            }
        }catch (Exception e){
            e.printStackTrace();
            return new ResponseEntity<>(new Mensaje("Error interno al eliminar la unidad medida"), HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }
}
