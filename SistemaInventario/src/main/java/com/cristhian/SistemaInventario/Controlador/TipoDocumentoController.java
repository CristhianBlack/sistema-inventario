package com.cristhian.SistemaInventario.Controlador;

import com.cristhian.SistemaInventario.DTO.CiudadDTO;
import com.cristhian.SistemaInventario.DTO.TipoDocumentoDTO;
import com.cristhian.SistemaInventario.Mensaje.Mensaje;
import com.cristhian.SistemaInventario.Modelo.Ciudad;
import com.cristhian.SistemaInventario.Modelo.TipoDocumento;
import com.cristhian.SistemaInventario.Repositorio.TipoDocumentoRepository;
import com.cristhian.SistemaInventario.Service.ITipoDocumentoService;
import com.cristhian.SistemaInventario.ServicioImplement.TipoDocumentoImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/Inventario")
@CrossOrigin(origins = "http://localhost:4200")
public class TipoDocumentoController {

    private final ITipoDocumentoService tipoDocumentoImpl;
    private final TipoDocumentoRepository tipoDocumentoRepository;

    public TipoDocumentoController(ITipoDocumentoService tipoDocumentoImpl,
                                   TipoDocumentoRepository tipoDocumentoRepository) {
        this.tipoDocumentoImpl = tipoDocumentoImpl;
        this.tipoDocumentoRepository = tipoDocumentoRepository;
    }

    @GetMapping("/tipoDocumentos")
    public ResponseEntity<List<TipoDocumentoDTO>> listarTipoDocumento(){
        List<TipoDocumentoDTO> response = tipoDocumentoImpl.listarTipoDocumentoActivo().stream()
                .map(TipoDocumentoDTO :: new).toList(); // mapeo entidad → DTO
        return ResponseEntity.ok(response);
    }

    @GetMapping("/tipoDocumentos/{id}")
    public ResponseEntity<?> buscarTipoDocumento(@PathVariable int id){
        Optional<TipoDocumento> data = tipoDocumentoImpl.buscarTipoDucmentoId(id);

        if (data.isEmpty()) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(new Mensaje("No existe el registro buscado"));
        }
        return ResponseEntity.ok(new TipoDocumentoDTO(data.get()));
    }

    @PostMapping("/tipoDocumentos")
    public ResponseEntity<?> guardarTipoDocumento(@RequestBody TipoDocumentoDTO tipoDocumentoDTO){
        try{
            var tipo = tipoDocumentoImpl.guardarTipoDocumento(tipoDocumentoDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(new TipoDocumentoDTO(tipo));
        }catch (IllegalArgumentException e){
            return ResponseEntity.badRequest().body(new Mensaje(e.getMessage()));
        }

    }

    @PutMapping("/tipoDocumentos/{id}")
    public ResponseEntity<?> actualizarTipoDocumento(@PathVariable int id, @RequestBody TipoDocumentoDTO tipoDocumentoDTO){
        try{
            var actualizado = tipoDocumentoImpl.actualizarTipoDocumento(id , tipoDocumentoDTO);
            return ResponseEntity.status(HttpStatus.OK).body(new TipoDocumentoDTO(actualizado));
        }catch (IllegalArgumentException e){
            return ResponseEntity.badRequest().body(new Mensaje(e.getMessage()));
        }
    }

    @DeleteMapping("tipoDocumentos/{id}")
    public ResponseEntity<?> desactivarTipodocumento(@PathVariable int id){
        try{
            tipoDocumentoImpl.desactivarTipoDcomuento(id);
            return ResponseEntity.status(HttpStatus.OK).body(new Mensaje("Tipo de Documento eliminado con éxito"));
        }catch (IllegalArgumentException e){
            return ResponseEntity.badRequest().body(new Mensaje(e.getMessage()));
        }

    }
}
