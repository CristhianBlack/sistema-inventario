package com.cristhian.SistemaInventario.Controlador;

import com.cristhian.SistemaInventario.DTO.VentaPagoDTO;
import com.cristhian.SistemaInventario.Mensaje.Mensaje;
import com.cristhian.SistemaInventario.Service.IVentaPagoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/Inventario")
@CrossOrigin(origins = "http://localhost:4200")
public class VentaPagoController {

    private final IVentaPagoService ventaPagoService;

    public VentaPagoController(IVentaPagoService ventaPagoService) {
        this.ventaPagoService = ventaPagoService;
    }

    @PostMapping("/Ventas/{idVenta}/Pagos")
    public ResponseEntity<?> registrarPago(
            @PathVariable int idVenta,
            @Valid @RequestBody VentaPagoDTO pagoDTO) {

        ventaPagoService.registrarPago(idVenta, pagoDTO);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new Mensaje("Pago registrado correctamente"));
    }

    @PutMapping("/Pagos/{idPago}/confirmar")
    public ResponseEntity<?> confirmarPago(@PathVariable int idPago) {

        ventaPagoService.confirmarPago(idPago);
        return ResponseEntity.ok(new Mensaje("Pago confirmado correctamente"));
    }

    @PutMapping("/Pagos/{idPago}/rechazar")
    public ResponseEntity<?> rechazarPago(@PathVariable int idPago) {

        ventaPagoService.rechazarPago(idPago);
        return ResponseEntity.ok(new Mensaje("Pago rechazado correctamente"));
    }

    @GetMapping("/Ventas/{idVenta}/Pagos")
    public List<VentaPagoDTO> listarPagosPorVenta(@PathVariable Long idVenta) {
        return ventaPagoService.listarPorVenta(idVenta);
    }
}

