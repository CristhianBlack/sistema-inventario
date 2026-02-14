package com.cristhian.SistemaInventario.Controlador;

import com.cristhian.SistemaInventario.DTO.UsuarioRequestDTO;
import com.cristhian.SistemaInventario.DTO.UsuarioResponseDTO;
import com.cristhian.SistemaInventario.ServicioImplement.UsuarioService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/Inventario")
@CrossOrigin("http://localhost:4200")
@PreAuthorize("hasRole('ADMIN_SISTEMA')")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @PostMapping("/usuario")
    public ResponseEntity<UsuarioResponseDTO> crear(@RequestBody UsuarioRequestDTO dto) {
        return ResponseEntity.ok(usuarioService.crearUsuario(dto));
    }

    @GetMapping("/usuario")
    public ResponseEntity<List<UsuarioResponseDTO>> listar() {
        return ResponseEntity.ok(usuarioService.listarUsuarios());
    }

    @GetMapping("/usuario/{id}")
    public ResponseEntity<UsuarioResponseDTO> obtener(@PathVariable int id) {
        return ResponseEntity.ok(usuarioService.obtenerPorId(id));
    }

    @PutMapping("/usuario/{id}")
    public ResponseEntity<UsuarioResponseDTO> editar(
            @PathVariable int id,
            @RequestBody UsuarioRequestDTO dto
    ) {
        return ResponseEntity.ok(usuarioService.editarUsuario(id, dto));
    }

    @DeleteMapping("/usuario/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable int id) {
        usuarioService.eliminarUsuario(id);
        return ResponseEntity.noContent().build();
    }
}
