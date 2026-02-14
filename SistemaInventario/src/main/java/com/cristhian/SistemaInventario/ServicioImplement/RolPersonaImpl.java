package com.cristhian.SistemaInventario.ServicioImplement;

import com.cristhian.SistemaInventario.Controlador.CiudadController;
import com.cristhian.SistemaInventario.DTO.RolPersonaDTO;
import com.cristhian.SistemaInventario.Enums.NombreRol;
import com.cristhian.SistemaInventario.Excepciones.DuplicadoException;
import com.cristhian.SistemaInventario.Excepciones.RecursoNoEncontradoException;
import com.cristhian.SistemaInventario.Mensaje.Mensaje;
import com.cristhian.SistemaInventario.Modelo.*;
import com.cristhian.SistemaInventario.Repositorio.RolPersonaRepository;
import com.cristhian.SistemaInventario.Service.IRolPersonaService;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
/**
 * Servicio encargado de la gestión de roles del sistema.
 * Administra la creación automática de roles, consultas
 * y eliminación lógica de los mismos.
 */
@Service
@Transactional
public class RolPersonaImpl implements IRolPersonaService {

    /*private static final Logger logger = LoggerFactory.getLogger(CiudadController.class);

    private final RolPersonaRepository rolPersonaRepository;

    public RolPersonaImpl(RolPersonaRepository rolPersonaRepository) {
        this.rolPersonaRepository = rolPersonaRepository;
    }

    @PostConstruct
    public void inicializarRolPersona() {
        guardarRolPersonaAutomatico();
    }

    @Override
    public List<RolPersona> listarRolPersonaActivo() {
        return rolPersonaRepository.findByActivoTrue();
    }

    @Override
    public Optional<RolPersona> buscarRolPersonaId(int id) {
        return rolPersonaRepository.findById(id);
    }

    @Override
    @Transactional
    public void guardarRolPersonaAutomatico() {

        List<NombreRol> nombreRolPermitidos = List.of(
                NombreRol.ADMIN,
                NombreRol.PROVEEDOR,
                NombreRol.EMPLEADO
        );
        for (NombreRol nombre : nombreRolPermitidos) {

            if (!rolPersonaRepository.existsByNombreRol(nombre)) {

                RolPersona rol = new RolPersona();
                rol.setNombreRol(nombre);

                // lógica automática para crear el impuesto.
                switch (nombre) {
                    case ADMIN -> {
                       rol.setDescripcion("Este rol puede ser asignado a la persona que sea natural y que puede tener acceso a toda la aplicacion.");
                    }
                    case PROVEEDOR -> {
                        rol.setDescripcion("Este rol puede ser asignado a la persona que sea natural o juridica y que puede ser proveedor.");
                    }
                    case EMPLEADO -> {
                        rol.setDescripcion("Este rol puede ser asignado a la persona que sea natural y que puede tener acceso limitado a ciertas funcionalidades de la aplicacion.");
                    }
                }

                rol.setActivo(true);
                rolPersonaRepository.save(rol);
            }
        }
    }

    @Override
    public void eliminarRolPersona(int id) {
        RolPersona rolPersona = rolPersonaRepository.findById(id).orElse(null);
        if (rolPersona != null){
            rolPersona.setActivo(false);
            rolPersonaRepository.save(rolPersona);
        }
    }*/

    private static final Logger logger =
            LoggerFactory.getLogger(CiudadController.class);

    private final RolPersonaRepository rolPersonaRepository;

    public RolPersonaImpl(RolPersonaRepository rolPersonaRepository) {
        this.rolPersonaRepository = rolPersonaRepository;
    }

    /**
     * Inicializa los roles del sistema al arrancar la aplicación.
     * Se ejecuta automáticamente una sola vez.
     */
    @PostConstruct
    public void inicializarRolPersona() {
        guardarRolPersonaAutomatico();
    }

    /**
     * Listar todos los roles activos.
     */
    @Override
    public List<RolPersona> listarRolPersonaActivo() {
        return rolPersonaRepository.findByActivoTrue();
    }

    /**
     * Buscar un rol por su ID.
     */
    @Override
    public Optional<RolPersona> buscarRolPersonaId(int id) {
        return rolPersonaRepository.findById(id);
    }

    /**
     * Crear automáticamente los roles base del sistema
     * si aún no existen en la base de datos.
     *
     * Roles creados:
     * - ADMIN
     * - PROVEEDOR
     * - EMPLEADO
     */
    @Override
    @Transactional
    public void guardarRolPersonaAutomatico() {

        List<NombreRol> nombreRolPermitidos = List.of(
                NombreRol.ADMIN,
                NombreRol.CLIENTE,
                NombreRol.PROVEEDOR,
                NombreRol.EMPLEADO
        );

        for (NombreRol nombre : nombreRolPermitidos) {

            // Validar si el rol ya existe
            if (!rolPersonaRepository.existsByNombreRol(nombre)) {

                RolPersona rol = new RolPersona();
                rol.setNombreRol(nombre);

                // Asignar descripción según el tipo de rol
                switch (nombre) {
                    case ADMIN -> {
                        rol.setDescripcion(
                                "Este rol puede ser asignado a la persona que sea natural y que puede tener acceso a toda la aplicación."
                        );
                    }
                    case PROVEEDOR -> {
                        rol.setDescripcion(
                                "Este rol puede ser asignado a la persona que sea natural o jurídica y que puede ser proveedor."
                        );
                    }
                    case EMPLEADO -> {
                        rol.setDescripcion(
                                "Este rol puede ser asignado a la persona que sea natural y que puede tener acceso limitado a ciertas funcionalidades de la aplicación."
                        );
                    }
                    case CLIENTE ->{
                        rol.setDescripcion(
                                "Este rol puede ser asignado a la persona que solo sera cliente en la aplicacion. "
                        );
                    }
                }

                rol.setActivo(true);
                rolPersonaRepository.save(rol);
            }
        }
    }

    /**
     * Eliminación lógica de un rol.
     * El rol no se borra físicamente de la base de datos.
     */
    @Override
    public void eliminarRolPersona(int id) {
        RolPersona rolPersona = rolPersonaRepository.findById(id).orElse(null);
        if (rolPersona != null){
            rolPersona.setActivo(false);
            rolPersonaRepository.save(rolPersona);
        }
    }
}
