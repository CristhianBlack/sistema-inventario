package com.cristhian.SistemaInventario.Controlador;

import com.cristhian.SistemaInventario.DTO.TipoPersonaDTO;
import com.cristhian.SistemaInventario.Mensaje.Mensaje;
import com.cristhian.SistemaInventario.Modelo.TipoPersona;
import com.cristhian.SistemaInventario.ServicioImplement.TipoPersonaImpl;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/Inventario")
@CrossOrigin(origins = "http://localhost:4200")
public class TipoPersonaController {

    private final TipoPersonaImpl tipoPersonaImpl;

    public TipoPersonaController(TipoPersonaImpl tipoPersonaImpl) {
        this.tipoPersonaImpl = tipoPersonaImpl;
    }

    @GetMapping("/tipoPersonas")
    public ResponseEntity<List<TipoPersona>> listarTipoPersona(){
        List<TipoPersona> listadotipopersona = tipoPersonaImpl.listarTipoPersonaActiva();
        return new ResponseEntity<List<TipoPersona>>(listadotipopersona, HttpStatus.OK);
    }
    @GetMapping("/tipoPersonas/{id}")
    public ResponseEntity<TipoPersona> buscarTipoPersonaId(@PathVariable int id){
        Optional<TipoPersona> tipoPersona = tipoPersonaImpl.buscarTipoPersonaId(id);
        if (!tipoPersona.isPresent()){
            return new ResponseEntity(new Mensaje("No existe el tipo de persona consultado"), HttpStatus.NOT_FOUND);
        }else{
            return new ResponseEntity<>(tipoPersona.get(), HttpStatus.OK);
        }
    }

    @PostMapping("tipoPersonas")
    public ResponseEntity<?> guardarTipoPersona(@Valid @RequestBody TipoPersonaDTO tipoPersonaDTO){
        if(tipoPersonaImpl.existePorNombreTipoPersona(tipoPersonaDTO.getNombreTipoPersona())){
            return new ResponseEntity<>(new Mensaje("Ya existe ese nombre de tipo persona."), HttpStatus.BAD_REQUEST);
        }

        TipoPersona tipoPersona = new TipoPersona();
        tipoPersona.setNombreTipoPersona(tipoPersonaDTO.getNombreTipoPersona());

        tipoPersonaImpl.guardarTipoPersona(tipoPersona);
        return new ResponseEntity<>(new Mensaje("Tipo de persona creada con exito."),HttpStatus.CREATED);
    }

    @PutMapping("/tipoPersonas/{id}")
    public ResponseEntity<?> actualizarTipoPersona(@PathVariable int id, @RequestBody TipoPersonaDTO tipoPersonaDTO){

        Optional<TipoPersona> tipoPersonaOpt = tipoPersonaImpl.buscarTipoPersonaId(id);
        if (!tipoPersonaOpt.isPresent()){
            return new ResponseEntity<>(new Mensaje("El tipo de persona no existe"), HttpStatus.NOT_FOUND);
        }

        TipoPersona tipoPersona = tipoPersonaOpt.get();
        tipoPersona.setNombreTipoPersona(tipoPersonaDTO.getNombreTipoPersona());

        tipoPersonaImpl.guardarTipoPersona(tipoPersona);

        return new ResponseEntity<>(new Mensaje("Se actualizo el tipo de persona con exito."),HttpStatus.OK);
    }

    @DeleteMapping("/tipoPersonas/{id}")
    public ResponseEntity<?> desactivarTipoPersona(@PathVariable int id){

        try{
            Optional<TipoPersona> tipoPersona = tipoPersonaImpl.buscarTipoPersonaId(id);
            if(tipoPersona.isPresent()){
                tipoPersonaImpl.desactivarTipoDocumento(id);
                return new ResponseEntity<>(new Mensaje("El tipo de persona se elimino con exito"), HttpStatus.OK);
            }else{
                return new ResponseEntity<>(new Mensaje("No existe el tipo de persona."),HttpStatus.NOT_FOUND);
            }
        }catch (Exception e){
            e.printStackTrace();
            return new ResponseEntity<>(new Mensaje("Error interno al eliminar el tipo de persona"), HttpStatus.INTERNAL_SERVER_ERROR);
        }



    }
}
