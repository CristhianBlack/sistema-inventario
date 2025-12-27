package com.cristhian.SistemaInventario.Controlador;

import com.cristhian.SistemaInventario.DTO.ImpuestoDTO;
import com.cristhian.SistemaInventario.Mensaje.Mensaje;
import com.cristhian.SistemaInventario.Modelo.Impuesto;
import com.cristhian.SistemaInventario.Service.IImpuestoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/Inventario")
@CrossOrigin(origins = "http://localhost:4200")
public class ImpuestoController {

    private final IImpuestoService impuestoService;

    public ImpuestoController(IImpuestoService impuestoService) {
        this.impuestoService = impuestoService;
    }

    @GetMapping("/Impuesto")
    public ResponseEntity<List<ImpuestoDTO>> listarImpuestoActivo(){
        List<ImpuestoDTO> response = impuestoService.listarImpuestosActivos().stream()
                .map(ImpuestoDTO::new)// mapeo entidad → DTO
                .toList();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/Impuesto/{id}")
    public ResponseEntity<?> obtenerImpuestoPorId(@PathVariable int id){
        Optional<Impuesto> data = impuestoService.buscarImpuestoPorId(id);

        if (data.isEmpty()) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(new Mensaje("No existe el registro buscado"));
        }
        return ResponseEntity.ok(new ImpuestoDTO(data.get()));
    }


    @DeleteMapping("/Impuesto/{id}")
    public ResponseEntity<?> eliminarImpuesto(@PathVariable int id){
        try{
            impuestoService.borrarImpuesto(id);
            return ResponseEntity.status(HttpStatus.OK).body(new Mensaje("Impuesto eliminado con éxito"));
        }catch (IllegalArgumentException e){
            return ResponseEntity.badRequest().body(new Mensaje(e.getMessage()));
        }
    }
}
