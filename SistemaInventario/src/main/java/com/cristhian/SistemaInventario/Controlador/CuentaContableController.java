package com.cristhian.SistemaInventario.Controlador;

import com.cristhian.SistemaInventario.DTO.CiudadDTO;
import com.cristhian.SistemaInventario.DTO.CuentaContableDTO;
import com.cristhian.SistemaInventario.Modelo.CuentaContable;
import com.cristhian.SistemaInventario.ServicioImplement.CuentaContableService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/Inventario")
@CrossOrigin(origins = "http://localhost:4200")
public class CuentaContableController {


    private final CuentaContableService cuentaService;

    public CuentaContableController(CuentaContableService cuentaService) {
        this.cuentaService = cuentaService;
    }

   /* @GetMapping("/contabilidad/cuentas")
    public List<CuentaContable> listarCuentas() {
        return cuentaService.listarCuentas();
    }*/

    @GetMapping("/contabilidad/cuentas")
    public ResponseEntity<List<CuentaContableDTO>> listarcuentas(){
        List<CuentaContableDTO> response = cuentaService.listarCuentas().stream()
                .map(CuentaContableDTO :: new).toList(); // mapeo entidad → DTO
        return ResponseEntity.ok(response);
    }
}
