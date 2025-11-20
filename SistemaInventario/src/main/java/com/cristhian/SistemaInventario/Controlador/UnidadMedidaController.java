package com.cristhian.SistemaInventario.Controlador;

import com.cristhian.SistemaInventario.DTO.UnidadMedidaDTO;
import com.cristhian.SistemaInventario.Mensaje.Mensaje;
import com.cristhian.SistemaInventario.Modelo.UnidadMedida;
import com.cristhian.SistemaInventario.Service.IUnidadMedidaService;
import com.cristhian.SistemaInventario.ServicioImplement.UnidadMedidaServiceImpl;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/Inventario")
@CrossOrigin(origins = "http://localhost:4200")
public class UnidadMedidaController {

    private final IUnidadMedidaService unidadMedidaService;

    public UnidadMedidaController(IUnidadMedidaService unidadMedidaService) {
        this.unidadMedidaService = unidadMedidaService;
    }

    // -------------------------------------------------------------
    // LISTAR
    // -------------------------------------------------------------
    @GetMapping("/Unidades")
    public ResponseEntity<List<UnidadMedidaDTO>> listadoUnidades() {
        List<UnidadMedidaDTO> response = unidadMedidaService.listarUnidades().stream()
                .map(UnidadMedidaDTO::new) // mapeo entidad → DTO
                .toList();
        return ResponseEntity.ok(response);
    }


    // -------------------------------------------------------------
    // BUSCAR POR ID
    // -------------------------------------------------------------
    @GetMapping("/Unidades/{id}")
    public ResponseEntity<?> buscarUnidadPorId(@PathVariable int id) {

        return unidadMedidaService.buscarUnidadId(id)
                .<ResponseEntity<?>>map(unidad ->
                        ResponseEntity.ok(unidad)
                ).orElseGet(() ->
                        ResponseEntity.status(HttpStatus.NOT_FOUND)
                                .body(new Mensaje("No existe el registro buscado"))
                );
    }

    // -------------------------------------------------------------
    // CREAR
    // -------------------------------------------------------------
    @PostMapping("/Unidades")
    public ResponseEntity<?> agregarUnidad(@Valid @RequestBody UnidadMedidaDTO unidadMedidaDto) {
        try {
            unidadMedidaService.guardar(unidadMedidaDto);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new Mensaje("Unidad creada con éxito"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new Mensaje(e.getMessage()));
        }
    }

    // -------------------------------------------------------------
    // ACTUALIZAR
    // -------------------------------------------------------------
    @PutMapping("/Unidades/{id}")
    public ResponseEntity<?> actualizarUnidadMedida(@PathVariable int id,
                                                    @Valid @RequestBody UnidadMedidaDTO unidadMedidaDTO) {

        try {
            unidadMedidaService.actualizarUnidadeMedida(id, unidadMedidaDTO);
            return ResponseEntity.ok(new Mensaje("Unidad actualizada con éxito"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new Mensaje(e.getMessage()));
        }
    }

    // -------------------------------------------------------------
    // ELIMINAR (LÓGICO)
    // -------------------------------------------------------------
    @DeleteMapping("/Unidades/{id}")
    public ResponseEntity<?> eliminarUnidadMedida(@PathVariable int id) {
        try {
            unidadMedidaService.borrar(id);
            return ResponseEntity.ok(new Mensaje("Unidad de medida eliminada con éxito"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new Mensaje(e.getMessage()));
        }
    }
}

