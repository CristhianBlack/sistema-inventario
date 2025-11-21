package com.cristhian.SistemaInventario.ServicioImplement;

import com.cristhian.SistemaInventario.Controlador.CiudadController;
import com.cristhian.SistemaInventario.DTO.RolPersonaDTO;
import com.cristhian.SistemaInventario.Excepciones.DuplicadoException;
import com.cristhian.SistemaInventario.Excepciones.RecursoNoEncontradoException;
import com.cristhian.SistemaInventario.Mensaje.Mensaje;
import com.cristhian.SistemaInventario.Modelo.Ciudad;
import com.cristhian.SistemaInventario.Modelo.PersonaRol;
import com.cristhian.SistemaInventario.Modelo.Proveedor;
import com.cristhian.SistemaInventario.Modelo.RolPersona;
import com.cristhian.SistemaInventario.Repositorio.RolPersonaRepository;
import com.cristhian.SistemaInventario.Service.IRolPersonaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class RolPersonaImpl implements IRolPersonaService {

    private static final Logger logger = LoggerFactory.getLogger(CiudadController.class);

    private final RolPersonaRepository rolPersonaRepository;

    public RolPersonaImpl(RolPersonaRepository rolPersonaRepository) {
        this.rolPersonaRepository = rolPersonaRepository;
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
    public RolPersona guardarRolPersona(RolPersonaDTO rolPersonaDTO) {

        String nombreNormalizado = rolPersonaDTO.getNombreRol().trim();

        logger.info("JSON recibido → nombre: {}, activo: {}", nombreNormalizado, rolPersonaDTO.isActivo());

        Optional<RolPersona> rolExistente =
                rolPersonaRepository.findByNombreRolIgnoreCase(nombreNormalizado);

        if (rolExistente.isPresent()) {
            RolPersona rolPersona = rolExistente.get();

            // Si existe pero está inactivo → lo reactivamos
            if (!rolPersona.isActivo()) {
                rolPersona.setActivo(true);
                logger.info("Rol encontrado inactivo. Reactivando...");
                return rolPersonaRepository.save(rolPersona);
            }
            // Si existe y está activo → ERROR
            logger.warn("Ya existe el rol y está activo");
            throw new DuplicadoException("Ya existe un rol con ese nombre");
        }

        // Si no existe → lo creamos
        logger.info("Creando nuevo rol persona...");
        RolPersona nuevoRol = new RolPersona(rolPersonaDTO);
        return rolPersonaRepository.save(nuevoRol);
    }

    @Override
    public RolPersona actualizarRolPersona(int id, RolPersonaDTO rolPersonaDTO){

        // Validar si existe el rol con ese ID
        RolPersona rolExistente = rolPersonaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("El rol no existe"));

        String nombreNormalizado = rolPersonaDTO.getNombreRol().trim();

        // Verificar si hay otro rol con el mismo nombre
        Optional<RolPersona> rolConMismoNombre =
                rolPersonaRepository.findByNombreRolIgnoreCase(rolPersonaDTO.getNombreRol().trim());

        if (rolConMismoNombre.isPresent()
                && rolConMismoNombre.get().getIdRolPersona() != id) {
            throw new DuplicadoException("Ya existe otro rol con ese nombre");
        }


        rolExistente.setNombreRol(nombreNormalizado);
        rolExistente.setDescripcion(rolPersonaDTO.getDescripcion());

        return rolPersonaRepository.save(rolExistente);

    }

    @Override
    public void eliminarRolPersona(int id) {
        RolPersona rolPersona = rolPersonaRepository.findById(id).orElse(null);
        if (rolPersona != null){
            rolPersona.setActivo(false);
            rolPersonaRepository.save(rolPersona);
        }
    }




}
