package com.cristhian.SistemaInventario.Controlador;

import com.cristhian.SistemaInventario.DTO.CompraDTO;
import com.cristhian.SistemaInventario.DTO.DevolucionDTO;
import com.cristhian.SistemaInventario.DTO.VentaPagoDTO;
import com.cristhian.SistemaInventario.Mensaje.Mensaje;
import com.cristhian.SistemaInventario.Modelo.Compra;
import com.cristhian.SistemaInventario.Modelo.Devolucion;
import com.cristhian.SistemaInventario.Service.IDevolcuonService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/Inventario")
@CrossOrigin(origins = "http://localhost:4200")
public class DevolucionController {

    private final IDevolcuonService devolucionService;


    public DevolucionController(IDevolcuonService devolucionService) {
        this.devolucionService = devolucionService;
    }

    @GetMapping("/Devolucion")
    public ResponseEntity<List<DevolucionDTO>> listarCompras(){
        List<DevolucionDTO> response = devolucionService.listarDevoluciones().stream()
                .map(DevolucionDTO :: new).toList(); // mapeo entidad → DTO
        return ResponseEntity.ok(response);
    }

    @GetMapping("/Devolucion/{id}")
    public ResponseEntity<?> obtenerCompraPorId(@PathVariable int id){
        Optional<Devolucion> data = devolucionService.buscarDevolucionPorId(id);

        if (data.isEmpty()) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(new Mensaje("No existe el registro buscado"));
        }
        return ResponseEntity.ok(new DevolucionDTO(data.get()));
    }

    @PostMapping("/Devolucion")
    public ResponseEntity<?> guardarCompra(@Valid @RequestBody DevolucionDTO devolucionDTO){
        try{
            var devolucion = devolucionService.crearDevolucion(devolucionDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(new DevolucionDTO(devolucion));
        }catch (IllegalArgumentException e){
            return ResponseEntity.badRequest().body(new Mensaje(e.getMessage()));
        }
    }

}
