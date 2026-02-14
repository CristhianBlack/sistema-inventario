package com.cristhian.SistemaInventario.ServicioImplement;

import com.cristhian.SistemaInventario.DTO.UnidadMedidaDTO;
import com.cristhian.SistemaInventario.Excepciones.DuplicadoException;
import com.cristhian.SistemaInventario.Excepciones.RecursoNoEncontradoException;
import com.cristhian.SistemaInventario.Excepciones.ValidacionException;
import com.cristhian.SistemaInventario.Mensaje.Mensaje;
import com.cristhian.SistemaInventario.Modelo.Ciudad;
import com.cristhian.SistemaInventario.Modelo.UnidadMedida;
import com.cristhian.SistemaInventario.Repositorio.UnidadMedidaRepository;
import com.cristhian.SistemaInventario.Service.IUnidadMedidaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Optional;
/**
 * Servicio encargado de la gestión de las unidades de medida.
 * Contiene la lógica para crear, listar, actualizar y
 * desactivar unidades de medida de forma controlada.
 */
@Service
@Transactional
public class UnidadMedidaServiceImpl implements IUnidadMedidaService {

    private static final Logger logger =
            LoggerFactory.getLogger(UnidadMedidaServiceImpl.class);

    @Autowired
    private UnidadMedidaRepository unidadMedidaRepository;

    /**
     * Lista todas las unidades de medida activas.
     */
    @Override
    public List<UnidadMedida> listarUnidades() {
        return unidadMedidaRepository.findByActivoTrue();
    }

    /**
     * Busca una unidad de medida por su ID.
     */
    @Override
    public Optional<UnidadMedida> buscarUnidadId(int id) {
        return unidadMedidaRepository.findById(id);
    }

    /**
     * Guarda una nueva unidad de medida.
     *
     * Valida duplicados por nombre y reactiva la unidad
     * si ya existe pero se encuentra inactiva.
     */
    @Override
    public UnidadMedida guardar(UnidadMedidaDTO dto) {

        String nombre = dto.getNombreMedida() == null
                ? ""
                : dto.getNombreMedida().trim();

        String sigla = dto.getSigla() == null
                ? ""
                : dto.getSigla().trim();

        // Validación de campos obligatorios
        if (nombre.isEmpty()) {
            throw new ValidacionException(
                    "El nombre de la unidad es obligatorio"
            );
        }

        Optional<UnidadMedida> unidadExistente =
                unidadMedidaRepository.findByNombreMedidaIgnoreCase(nombre);

        // Si la unidad ya existe
        if (unidadExistente.isPresent()) {

            UnidadMedida unidad = unidadExistente.get();

            // Si existe pero está inactiva → se reactiva
            if (!unidad.isActivo()) {
                logger.info("Unidad encontrada inactiva. Se activará nuevamente.");
                unidad.setActivo(true);
                return unidadMedidaRepository.save(unidad);
            }

            // Si ya existe y está activa → error
            logger.info("La unidad ya existe y está activa.");
            throw new DuplicadoException(
                    "Ya existe una unidad de medida con ese nombre"
            );
        }

        // Creación de la unidad a partir del DTO
        UnidadMedida unidad = new UnidadMedida(dto);
        return unidadMedidaRepository.save(unidad);
    }

    /**
     * Actualiza una unidad de medida existente.
     *
     * Valida que no exista otra unidad con el mismo nombre.
     */
    @Override
    public UnidadMedida actualizarUnidadeMedida(int id, UnidadMedidaDTO dto) {

        UnidadMedida unidadExistente =
                unidadMedidaRepository.findById(id)
                        .orElseThrow(() ->
                                new RecursoNoEncontradoException(
                                        "No existe esa unidad de Medida"
                                )
                        );

        String nuevoNombre = dto.getNombreMedida().trim();
        String nuevaSigla = dto.getSigla().trim();

        // Validación de duplicados en otras unidades
        if (unidadMedidaRepository.existsOtherWithSameName(id, nuevoNombre)) {
            throw new DuplicadoException(
                    "Ya existe otra unidad de medida con ese nombre"
            );
        }

        // Actualización de campos
        unidadExistente.setNombreMedida(nuevoNombre);
        unidadExistente.setSigla(nuevaSigla);

        return unidadMedidaRepository.save(unidadExistente);
    }

    /**
     * Desactiva una unidad de medida de forma lógica.
     */
    @Override
    public void borrar(int id) {

        UnidadMedida unidad =
                unidadMedidaRepository.findById(id)
                        .orElseThrow(() ->
                                new RecursoNoEncontradoException(
                                        "No existe la unidad de medida"
                                )
                        );

        unidad.setActivo(false);
        unidadMedidaRepository.save(unidad);
    }
}

