package com.cristhian.SistemaInventario.Controlador;

import com.cristhian.SistemaInventario.DTO.CompraDTO;
import com.cristhian.SistemaInventario.DTO.VentaDTO;
import com.cristhian.SistemaInventario.Mensaje.Mensaje;
import com.cristhian.SistemaInventario.Modelo.Compra;
import com.cristhian.SistemaInventario.Modelo.Venta;
import com.cristhian.SistemaInventario.Service.IVentaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.List;
import java.util.Optional;
import java.util.Map;
import java.util.HashMap;

import static com.fasterxml.jackson.databind.type.LogicalType.Map;

@RestController
@RequestMapping("/Inventario")
@CrossOrigin(origins = "http://localhost:4200")
public class VentaController {

    private final IVentaService ventaService;

    public VentaController(IVentaService ventaService) {
        this.ventaService = ventaService;
    }

    @GetMapping("/Ventas")
    public ResponseEntity<List<VentaDTO>> listarVentas(){
        List<VentaDTO> response = ventaService.listarVentas().stream()
                .map(VentaDTO :: new).toList(); // mapeo entidad → DTO
        return ResponseEntity.ok(response);
    }

    @GetMapping("/Ventas/{id}")
    public ResponseEntity<?> obtenerVentaPorId(@PathVariable int id){
        Optional<Venta> data = ventaService.buscarVentaPorId(id);

        if (data.isEmpty()) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(new Mensaje("No existe el registro buscado"));
        }
        return ResponseEntity.ok(new VentaDTO(data.get()));
    }

    @PostMapping("/Ventas")
    public ResponseEntity<?> guardarVenta(@Valid @RequestBody VentaDTO ventaDTO){
        try{
            var venta = ventaService.guardarVenta(ventaDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(new VentaDTO(venta));
        }catch (IllegalArgumentException e){
            return ResponseEntity.badRequest().body(new Mensaje(e.getMessage()));
        }
    }

    @PutMapping("/Ventas/{id}/cancelar")
    public ResponseEntity<Void> cancelarVenta(@PathVariable int id) {
        ventaService.cancelarVenta(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("Ventas/{id}/confirmar")
    public ResponseEntity<?> confirmarVenta(@PathVariable("id") int id) {
        ventaService.confirmarVenta(id);

        Map<String, String> response = new HashMap<>();
        response.put("mensaje", "Venta confirmada correctamente");

        return ResponseEntity.ok(response);
    }
}
