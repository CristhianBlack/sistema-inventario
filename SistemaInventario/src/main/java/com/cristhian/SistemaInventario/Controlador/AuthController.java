package com.cristhian.SistemaInventario.Controlador;

import com.cristhian.SistemaInventario.DTO.ChangePasswordDTO;
import com.cristhian.SistemaInventario.DTO.LoginRequestDTO;
import com.cristhian.SistemaInventario.DTO.LoginResponseDTO;
import com.cristhian.SistemaInventario.Modelo.Persona;
import com.cristhian.SistemaInventario.Modelo.Usuario;
import com.cristhian.SistemaInventario.Repositorio.UsuarioRepository;
import com.cristhian.SistemaInventario.Security.JwtUtil;
import com.cristhian.SistemaInventario.ServicioImplement.UsuarioService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/Inventario")
@CrossOrigin("http://localhost:4200")
public class AuthController {

    private final UsuarioService usuarioService;

    public AuthController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @PostMapping("/auth/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody LoginRequestDTO request) {

        LoginResponseDTO response = usuarioService.login(request);

        return ResponseEntity.ok(response);
    }

    @PutMapping("/auth/cambiar-password")
    public ResponseEntity<?> cambiarPassword(@RequestBody ChangePasswordDTO request){

        usuarioService.cambiarPassword(request);

        return ResponseEntity.ok("Contraseña actualizada correctamente");
    }
}
