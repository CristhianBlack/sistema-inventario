package com.cristhian.SistemaInventario.ServicioImplement;

import com.cristhian.SistemaInventario.Modelo.PasswordResetToken;
import com.cristhian.SistemaInventario.Modelo.Usuario;
import com.cristhian.SistemaInventario.Repositorio.PasswordResetTokenRepository;
import com.cristhian.SistemaInventario.Repositorio.UsuarioRepository;
import com.cristhian.SistemaInventario.Excepciones.RecursoNoEncontradoException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@Transactional
public class PasswordResetService {

    private static final int EXPIRACION_MINUTOS = 15;
    private static final String PASSWORD_REGEX = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{8,}$";

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordResetTokenRepository tokenRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /* =====================================================
       1️⃣ Solicitar reset de contraseña
    ===================================================== */
    @Transactional
    public String solicitarReset(String username) {

        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElse(null);

        if (usuario == null) {
            return null;
        }

        String token = UUID.randomUUID().toString();

        PasswordResetToken resetToken = tokenRepository
                .findByUsuario(usuario)
                .orElse(new PasswordResetToken());

        resetToken.setUsuario(usuario);
        resetToken.setToken(token);
        resetToken.setUsado(false);
        resetToken.setFechaExpiracion(
                LocalDateTime.now().plusMinutes(EXPIRACION_MINUTOS)
        );

        tokenRepository.save(resetToken);

        return token;
    }

    /* =====================================================
       2️⃣ Resetear contraseña usando token
    ===================================================== */
    public void resetearPassword(String token, String nuevaPassword) {

        validarPassword(nuevaPassword);

        PasswordResetToken resetToken = tokenRepository
                .findByTokenAndUsadoFalse(token)
                .orElseThrow(() ->
                        new RecursoNoEncontradoException("Token inválido o ya usado"));

        if (resetToken.estaExpirado()) {
            throw new RuntimeException("El token ha expirado");
        }

        Usuario usuario = resetToken.getUsuario();

        usuario.setPassword(passwordEncoder.encode(nuevaPassword));
        usuarioRepository.save(usuario);

        resetToken.setUsado(true);
        tokenRepository.save(resetToken);
    }

    private void validarPassword(String password) {
        if (!password.matches(PASSWORD_REGEX)) {
            throw new IllegalArgumentException(
                    "La contraseña no cumple con los requisitos de seguridad"
            );
        }
    }
}

