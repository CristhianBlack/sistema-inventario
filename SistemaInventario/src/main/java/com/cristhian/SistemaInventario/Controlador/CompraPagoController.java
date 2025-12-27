package com.cristhian.SistemaInventario.Controlador;

import com.cristhian.SistemaInventario.DTO.CompraPagoDTO;
import com.cristhian.SistemaInventario.DTO.VentaPagoDTO;
import com.cristhian.SistemaInventario.Mensaje.Mensaje;
import com.cristhian.SistemaInventario.Service.ICompraPagoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/Inventario")
@CrossOrigin(origins = "http://localhost:4200")
public class CompraPagoController {

    private final ICompraPagoService compraPagoService;

    public CompraPagoController(ICompraPagoService compraPagoService) {
        this.compraPagoService = compraPagoService;
    }

    @PostMapping("/Compras/{id}/Pagos")
    public ResponseEntity<?> registrarPago(
            @PathVariable int id,
            @Valid @RequestBody CompraPagoDTO pagoDTO) {

        compraPagoService.registrarPago(id, pagoDTO);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new Mensaje("Pago registrado correctamente"));
    }

    @PutMapping("/Pago/{id}/confirmar")
    public ResponseEntity<?> confirmarPago(@PathVariable int id) {

        compraPagoService.confirmarPago(id);
        return ResponseEntity.ok(new Mensaje("Pago confirmado correctamente"));
    }

    @PutMapping("/Pago/{id}/rechazar")
    public ResponseEntity<?> rechazarPago(@PathVariable int id) {

        compraPagoService.rechazarPago(id);
        return ResponseEntity.ok(new Mensaje("Pago rechazado correctamente"));
    }

    @GetMapping("/Compras/{id}/Pagos")
    public List<CompraPagoDTO> listarPagosPorCompra(@PathVariable Long id) {
        return compraPagoService.listarPorVenta(id);
    }
}
