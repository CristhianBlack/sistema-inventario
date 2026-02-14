package com.cristhian.SistemaInventario.Security;

import com.cristhian.SistemaInventario.Modelo.Usuario;
import com.cristhian.SistemaInventario.Repositorio.UsuarioRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class UsuarioAutenticadoProvider {

    private final UsuarioRepository usuarioRepository;

    public UsuarioAutenticadoProvider(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    public Usuario obtenerUsuarioAutenticado() {

        Authentication auth = SecurityContextHolder
                .getContext()
                .getAuthentication();

        if (auth == null || !auth.isAuthenticated()) {
            throw new RuntimeException("Usuario no autenticado");
        }

        return (Usuario) auth.getPrincipal(); // YA ES ENTIDAD
    }
}
