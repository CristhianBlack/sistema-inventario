package com.cristhian.SistemaInventario.ServicioImplement;

import com.cristhian.SistemaInventario.Modelo.Persona;
import com.cristhian.SistemaInventario.Modelo.Usuario;
import com.cristhian.SistemaInventario.Repositorio.UsuarioRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
/**
 * Implementación personalizada de {@link UserDetailsService}
 * utilizada por Spring Security para la autenticación de usuarios.
 *
 * Esta clase se encarga de:
 * - Buscar el usuario en base de datos por username
 * - Validar su existencia
 * - Construir el objeto {@link UserDetails} con sus credenciales y roles
 */
@Service
@Transactional
public class UsuarioDetailsServiceImpl implements UserDetailsService {

    /*private final UsuarioRepository usuarioRepository;

    public UsuarioDetailsServiceImpl(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

        /*Persona p = usuario.getPersona();
        String nombreCompleto = p.getNombre() + " " + p.getApellido();*/

       /* List<GrantedAuthority> authorities = List.of(
                new SimpleGrantedAuthority("ROLE_" + usuario.getRolSeguridad().name())
        );

        return new User(
                usuario.getUsername(),
                usuario.getPassword(),
                usuario.isActivo(),
                true,
                true,
                true,
                authorities
        );
    }*/

    private final UsuarioRepository usuarioRepository;

    public UsuarioDetailsServiceImpl(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    /**
     * Carga un usuario por su nombre de usuario.
     *
     * Este método es invocado automáticamente por Spring Security
     * durante el proceso de autenticación.
     *
     * @param username nombre de usuario
     * @return detalles del usuario requeridos por Spring Security
     * @throws UsernameNotFoundException si el usuario no existe
     */
    @Override
    public UserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException {

        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() ->
                        new UsernameNotFoundException("Usuario no encontrado")
                );

        // Construcción de las autoridades (roles) del usuario
        List<GrantedAuthority> authorities = List.of(
                new SimpleGrantedAuthority(
                        "ROLE_" + usuario.getRolSeguridad().name()
                )
        );

        // Retorno del usuario compatible con Spring Security
        return new User(
                usuario.getUsername(),
                usuario.getPassword(),
                usuario.isActivo(), // habilitado
                true,               // cuenta no expirada
                true,               // credenciales no expiradas
                true,               // cuenta no bloqueada
                authorities
        );
    }
}
