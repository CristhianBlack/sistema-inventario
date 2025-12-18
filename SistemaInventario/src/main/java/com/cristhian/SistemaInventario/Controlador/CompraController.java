package com.cristhian.SistemaInventario.Controlador;

import com.cristhian.SistemaInventario.DTO.CiudadDTO;
import com.cristhian.SistemaInventario.DTO.CompraDTO;
import com.cristhian.SistemaInventario.Mensaje.Mensaje;
import com.cristhian.SistemaInventario.Modelo.Ciudad;
import com.cristhian.SistemaInventario.Modelo.Compra;
import com.cristhian.SistemaInventario.Service.ICompraService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/Inventario")
@CrossOrigin(origins = "http://localhost:4200")
public class CompraController {

    private final ICompraService compraService;

    public CompraController(ICompraService compraService) {
        this.compraService = compraService;
    }

    @GetMapping("/Compras")
    public ResponseEntity<List<CompraDTO>> listarCompras(){
        List<CompraDTO> response = compraService.listarComprasActivas().stream()
                .map(CompraDTO :: new).toList(); // mapeo entidad → DTO
        return ResponseEntity.ok(response);
    }

    @GetMapping("/Compras/{id}")
    public ResponseEntity<?> obtenerCompraPorId(@PathVariable int id){
        Optional<Compra> data = compraService.BuscarCompraId(id);

        if (data.isEmpty()) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(new Mensaje("No existe el registro buscado"));
        }
        return ResponseEntity.ok(new CompraDTO(data.get()));
    }

    @PostMapping("/Compras")
    public ResponseEntity<?> guardarCompra(@Valid @RequestBody CompraDTO compraDTO){
        try{
            var compra = compraService.guardarCompra(compraDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(new CompraDTO(compra));
        }catch (IllegalArgumentException e){
            return ResponseEntity.badRequest().body(new Mensaje(e.getMessage()));
        }
    }

    @PutMapping("/Compras/{id}")
    public ResponseEntity<?> actualizarCompra(@PathVariable int id, @RequestBody CompraDTO compraDTO){
        try{
            var actualizado = compraService.actualizarCompra(id, compraDTO);
            return ResponseEntity.status(HttpStatus.OK).body(new CompraDTO(actualizado));
        }catch (IllegalArgumentException e){
            return ResponseEntity.badRequest().body(new Mensaje(e.getMessage()));
        }
    }

    @DeleteMapping("/Compras/{id}")
    public ResponseEntity<?> elminarCompra(@PathVariable int id){
        try{
            compraService.eliminarCompra(id);
            return ResponseEntity.status(HttpStatus.OK).body(new Mensaje("Compra eliminada con éxito"));
        }catch (IllegalArgumentException e){
            return ResponseEntity.badRequest().body(new Mensaje(e.getMessage()));
        }
    }
}
