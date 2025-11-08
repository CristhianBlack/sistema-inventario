package com.cristhian.SistemaInventario.Controlador;

import com.cristhian.SistemaInventario.DTO.TipoDocumentoDTO;
import com.cristhian.SistemaInventario.Mensaje.Mensaje;
import com.cristhian.SistemaInventario.Modelo.TipoDocumento;
import com.cristhian.SistemaInventario.Repositorio.TipoDocumentoRepository;
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

    private final TipoDocumentoImpl tipoDocumentoImpl;
    private final TipoDocumentoRepository tipoDocumentoRepository;

    public TipoDocumentoController(TipoDocumentoImpl tipoDocumentoImpl,
                                   TipoDocumentoRepository tipoDocumentoRepository) {
        this.tipoDocumentoImpl = tipoDocumentoImpl;
        this.tipoDocumentoRepository = tipoDocumentoRepository;
    }

    @GetMapping("/tipoDocumentos")
    public ResponseEntity<List<TipoDocumento>> listarTipoDocumento(){
        List<TipoDocumento> listado = tipoDocumentoImpl.listarTipoDocumentoActivo();
        return new ResponseEntity<List<TipoDocumento>>(listado, HttpStatus.OK);
    }

    @GetMapping("/tipoDocumentos/{id}")
    public ResponseEntity<TipoDocumento> buscarTipoDocumento(@PathVariable int id){
        Optional<TipoDocumento> tipoDocumentoOpt = tipoDocumentoImpl.buscarTipoDucmentoId(id);
        if(!tipoDocumentoOpt.isPresent()){
            return new ResponseEntity(new Mensaje("No existe el tipo de documento consultado"), HttpStatus.NOT_FOUND);
        }else{
            return new ResponseEntity<>(tipoDocumentoOpt.get(), HttpStatus.OK);
        }
    }

    @PostMapping("/tipoDocumentos")
    public ResponseEntity<?> guardarTipoDocumento(@RequestBody TipoDocumentoDTO tipoDocumentoDTO){
        if(tipoDocumentoImpl.existePorNombreTipoDocumento(tipoDocumentoDTO.getNombreTipoDocumento())){
            return new ResponseEntity<>(new Mensaje("Ya existe ese tipo de documento."),HttpStatus.BAD_REQUEST);
        }

        TipoDocumento tipoDocumento = new TipoDocumento();
        tipoDocumento.setNombreTipoDocumento(tipoDocumentoDTO.getNombreTipoDocumento());
        tipoDocumento.setSigla(tipoDocumentoDTO.getSigla());

        tipoDocumentoImpl.guardarTipoDocumento(tipoDocumento);
        return new ResponseEntity<>(new Mensaje("El tipo de docmuento se guardo con exito."),HttpStatus.CREATED);

    }

    @PutMapping("/tipoDocumentos/{id}")
    public ResponseEntity<?> actualizarTipoDocumento(@PathVariable int id, @RequestBody TipoDocumentoDTO tipoDocumentoDTO){
        Optional<TipoDocumento> tipoDocumentoOpt = tipoDocumentoImpl.buscarTipoDucmentoId(id);
        if (!tipoDocumentoOpt.isPresent()){
            return new ResponseEntity<>(new Mensaje("El tipo de documento no existe."), HttpStatus.NOT_FOUND);
        }

        TipoDocumento tipoDocumento = tipoDocumentoOpt.get();
        tipoDocumento.setNombreTipoDocumento(tipoDocumentoDTO.getNombreTipoDocumento());
        tipoDocumento.setSigla(tipoDocumentoDTO.getSigla());

        tipoDocumentoImpl.guardarTipoDocumento(tipoDocumento);
        return new ResponseEntity<>(new Mensaje("Se actualizo el tipo de documento con exito."), HttpStatus.OK);
    }

    @DeleteMapping("tipoDocumentos/{id}")
    public ResponseEntity<?> desactivarTipodocumento(@PathVariable int id){

        try{
            Optional<TipoDocumento> tipoDocumentoOpt = tipoDocumentoImpl.buscarTipoDucmentoId(id);
            if (tipoDocumentoOpt.isPresent()){
                tipoDocumentoImpl.desactivarTipoDcomuento(id);
                return new ResponseEntity<>(new Mensaje("Se elimino el tipo de documentos con exito."), HttpStatus.OK);
            }else{
                return  new ResponseEntity<>(new Mensaje("No existe el tipo de documento."), HttpStatus.NOT_FOUND);
            }
        }catch (Exception e){
            e.printStackTrace();
            return new ResponseEntity<>(new Mensaje("Error interno al eliminar el tipo de documento."),HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
