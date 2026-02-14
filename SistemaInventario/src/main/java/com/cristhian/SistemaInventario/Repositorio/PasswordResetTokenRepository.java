package com.cristhian.SistemaInventario.Repositorio;

import com.cristhian.SistemaInventario.Modelo.PasswordResetToken;
import com.cristhian.SistemaInventario.Modelo.Usuario;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;

import java.util.Optional;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Integer> {

    Optional<PasswordResetToken> findByToken(String token);

    Optional<PasswordResetToken> findByTokenAndUsadoFalse(String token);

    //void deleteByUsuario_IdUsuario(int idUsuario);

    Optional<PasswordResetToken> findByUsuario(Usuario usuario);

    @Modifying
    @Transactional
    void deleteByUsuario(Usuario usuario);
}

