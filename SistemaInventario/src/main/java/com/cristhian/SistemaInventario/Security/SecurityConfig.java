package com.cristhian.SistemaInventario.Security;

import com.cristhian.SistemaInventario.Repositorio.UsuarioRepository;
import com.cristhian.SistemaInventario.ServicioImplement.UsuarioDetailsServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    //Configuración de Security (sin JWT todavía)
    private final UsuarioDetailsServiceImpl usuarioDetailsService;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final UsuarioRepository usuarioRepository;

    public SecurityConfig(
            UsuarioDetailsServiceImpl usuarioDetailsService,
            PasswordEncoder passwordEncoder,
            JwtUtil jwtUtil,
            UsuarioRepository usuarioRepository
    ) {
        this.usuarioDetailsService = usuarioDetailsService;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.usuarioRepository = usuarioRepository;
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(usuarioDetailsService);
        provider.setPasswordEncoder(passwordEncoder);
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration config
    ) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> {})
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/Inventario/auth/**").permitAll()
                        .requestMatchers("/Inventario/contabilidad/**").hasAuthority("ROLE_ADMIN_SISTEMA")
                        .requestMatchers("/Inventario/Compras/**").hasAnyAuthority("ROLE_ADMIN_SISTEMA", "ROLE_USER")
                        .requestMatchers("/Inventario/usuario/**").hasAuthority("ROLE_ADMIN_SISTEMA")
                        //.requestMatchers("/Inventario/Ventas/**").hasAnyRole("ADMIN_SISTEMA", "USER")
                        .anyRequest().authenticated()
                )
                // MANEJO DE ERRORES 401 y 403
                .exceptionHandling(ex -> ex
                        // 401 → no autenticado o token inválido / vencido
                        .authenticationEntryPoint((request, response, authException) -> {
                            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                            response.setContentType("application/json");
                            response.getWriter().write("""
                    {
                      "error": "UNAUTHORIZED",
                      "message": "Token inválido o expirado"
                    }
                """);
                        })

                        // 403 → autenticado pero sin permisos
                        .accessDeniedHandler((request, response, accessDeniedException) -> {
                            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                            response.setContentType("application/json");
                            response.getWriter().write("""
                    {
                      "error": "FORBIDDEN",
                      "message": "No tienes permisos para acceder a este recurso"
                    }
                """);
                        })
                )
                .addFilterBefore(
                        new JwtFilter(jwtUtil, usuarioRepository),
                        UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }
}
