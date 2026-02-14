package com.cristhian.SistemaInventario.Config;

import com.cristhian.SistemaInventario.Enums.NombreRol;
import com.cristhian.SistemaInventario.Enums.NombreTipoDocumento;
import com.cristhian.SistemaInventario.Enums.NombreTipoPersona;
import com.cristhian.SistemaInventario.Enums.RolSeguridad;
import com.cristhian.SistemaInventario.Modelo.*;
import com.cristhian.SistemaInventario.Repositorio.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Configuration
@RequiredArgsConstructor
public class AdminInitializer implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;
    private final PersonaRepository personaRepository;
    private final CiudadRepository ciudadRepository;
    private final TipoPersonaRepository tipoPersonaRepository;
    private final TipoDocumentoRepository tipoDocumentoRepository;
    private final RolPersonaRepository rolPersonaRepository;
    private final PersonaRolRepository personaRolRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.admin.username}")
    private String adminUsername;

    @Value("${app.admin.password}")
    private String adminPassword;

    @Transactional
    public void run(String... args) {

        crearCiudadBase();
        System.out.println("Datos maestros verificados");
        crearUsuarioPersonaAdminAutomatico();
    }

    //Creamos un metodo que se ejcuta de forma automatica para crear una ciudad default
    private void crearCiudadBase() {

        ciudadRepository.findByCiudad("SIN CIUDAD")
                .orElseGet(() -> {

                    Ciudad ciudad = new Ciudad();
                    ciudad.setCiudad("SIN CIUDAD");

                    return ciudadRepository.save(ciudad);
                });
    }

    private void crearUsuarioPersonaAdminAutomatico(){
        if (usuarioRepository.findByUsername("admin").isPresent()) {
            return;
        }

        System.out.println("🔥 Creando ADMIN inicial...");

        Ciudad ciudad = ciudadRepository.findByCiudad("SIN CIUDAD")
                .orElseThrow();

        TipoPersona tipoPersona = tipoPersonaRepository
                .findByNombreTipoPersona(NombreTipoPersona.PERSONA_NATURAL)
                .orElseThrow();

        TipoDocumento tipoDocumento = tipoDocumentoRepository
                .findByNombreTipoDocumento(NombreTipoDocumento.CEDULA_CIUDADANIA)
                .orElseThrow();

        // -------- PERSONA --------
        Persona persona = new Persona();
        persona.setNombre("ADMIN");
        persona.setApellido("SISTEMA");
        persona.setDocumentoPersona("000000");
        persona.setDireccion("N/A");
        persona.setTelefono("000000");
        persona.setEmail("admin@sistema.com");

        persona.setCiudad(ciudad);
        persona.setTipoPersona(tipoPersona);
        persona.setTipoDocumento(tipoDocumento);

        personaRepository.save(persona);

        // -------- ROL ADMIN --------
        RolPersona rolAdmin = rolPersonaRepository
                .findByNombreRol(NombreRol.ADMIN)
                .orElseThrow();

        PersonaRol personaRol = new PersonaRol();
        personaRol.setPersona(persona);
        personaRol.setRolPersona(rolAdmin);
        personaRol.setFechaAsignacion(LocalDate.now());
        personaRol.setActivo(true);

        personaRolRepository.save(personaRol);

        // -------- USUARIO --------
        Usuario usuario = new Usuario();
        usuario.setUsername(adminUsername);
        usuario.setPassword(passwordEncoder.encode(adminPassword));
        usuario.setPersona(persona);
        usuario.setRolSeguridad(RolSeguridad.ADMIN_SISTEMA);
        usuario.setActivo(true);

        //OBLIGAR CAMBIO DE PASSWORD
        usuario.setDebeCambiarPassword(true);

        usuarioRepository.save(usuario);

        System.out.println("✅ ADMIN creado correctamente");
    }
}
