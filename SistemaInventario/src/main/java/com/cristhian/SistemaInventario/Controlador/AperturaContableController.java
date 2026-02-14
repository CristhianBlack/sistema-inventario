package com.cristhian.SistemaInventario.Controlador;

import com.cristhian.SistemaInventario.DTO.AperturaCuentaDTO;
import com.cristhian.SistemaInventario.ServicioImplement.AperturaContableService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
@PreAuthorize("hasRole('ADMIN_SISTEMA')")
@RestController
@RequestMapping("/Inventario")
@CrossOrigin("http://localhost:4200")
public class AperturaContableController {

    private final AperturaContableService service;

    public AperturaContableController(AperturaContableService service) {
        this.service = service;
    }

    @PostMapping("/contabilidad/apertura")
    public ResponseEntity<?> crearApertura(
            @RequestBody AperturaCuentaDTO dto
    ) {
        service.crearAsientoApertura(dto);
        return ResponseEntity.ok(
                Map.of("mensaje", "Asiento de apertura creado correctamente")
        );
    }
}
