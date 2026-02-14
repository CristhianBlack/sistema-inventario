package com.cristhian.SistemaInventario.Controlador;

import com.cristhian.SistemaInventario.DTO.CiudadDTO;
import com.cristhian.SistemaInventario.DTO.TipoDocumentoDTO;
import com.cristhian.SistemaInventario.DTO.TipoPersonaDTO;
import com.cristhian.SistemaInventario.Mensaje.Mensaje;
import com.cristhian.SistemaInventario.Modelo.Ciudad;
import com.cristhian.SistemaInventario.Modelo.TipoPersona;
import com.cristhian.SistemaInventario.Service.ITipoPersonaService;
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

    private final ITipoPersonaService tipoPersonaImpl;

    public TipoPersonaController(ITipoPersonaService tipoPersonaImpl) {
        this.tipoPersonaImpl = tipoPersonaImpl;
    }

    @GetMapping("/tipoPersonas")
    public ResponseEntity<List<TipoPersonaDTO>> listarTipoPersona(){
        List<TipoPersonaDTO> response = tipoPersonaImpl.listarTipoPersonaActiva().stream()
                .map(TipoPersonaDTO :: new).toList(); // mapeo entidad → DTO
        return ResponseEntity.ok(response);
    }
    @GetMapping("/tipoPersonas/{id}")
    public ResponseEntity<?> buscarTipoPersonaId(@PathVariable int id){
        Optional<TipoPersona> data = tipoPersonaImpl.buscarTipoPersonaId(id);

        if (data.isEmpty()) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(new Mensaje("No existe el registro buscado"));
        }
        return ResponseEntity.ok(new TipoPersonaDTO(data.get()));
    }


    @DeleteMapping("/tipoPersonas/{id}")
    public ResponseEntity<?> desactivarTipoPersona(@PathVariable int id){
        try{
            tipoPersonaImpl.desactivarTipoDocumento(id);
            return ResponseEntity.status(HttpStatus.OK).body(new Mensaje("Tipo de perosna eliminada con éxito"));
        }catch (IllegalArgumentException e){
            return ResponseEntity.badRequest().body(new Mensaje(e.getMessage()));
        }
    }
}

