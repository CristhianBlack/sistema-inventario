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

@Service
@Transactional
public class UnidadMedidaServiceImpl implements IUnidadMedidaService {

    private static final Logger logger = LoggerFactory.getLogger(UnidadMedidaServiceImpl.class);

    @Autowired
    private UnidadMedidaRepository unidadMedidaRepository;

    @Override
    public List<UnidadMedida> listarUnidades(){
        return unidadMedidaRepository.findByActivoTrue();
    }

    @Override
    public Optional<UnidadMedida> buscarUnidadId(int id){
        return unidadMedidaRepository.findById(id);
    }

    @Override
    public UnidadMedida guardar(UnidadMedidaDTO dto) {

        String nombre = dto.getNombreMedida() == null ? "" : dto.getNombreMedida().trim();
        String sigla = dto.getSigla() == null ? "" : dto.getSigla().trim();

        if (nombre == null || nombre.trim().isEmpty()) {
            throw new ValidacionException("El nombre de la unidad es obligatorio");
        }

        Optional<UnidadMedida> unidadExistente = unidadMedidaRepository.findByNombreMedidaIgnoreCase(nombre.trim());

        // Si existe la unidad
        if (unidadExistente.isPresent()) {
            UnidadMedida unidad = unidadExistente.get();

            // Si existe pero está inactiva → Reactivar
            if (!unidad.isActivo()) {
                logger.info("Unidad encontrada inactiva. Se activará nuevamente.");
                unidad.setActivo(true);
                return unidadMedidaRepository.save(unidad);   // ✔ IMPORTANTE: retornar
            }

            // Si ya existe y está activa → Error
            logger.info("La unidad ya existe y está activa.");
            throw new DuplicadoException("Ya existe una unidad de medida con ese nombre");
        }

        // Aquí sí usamos el constructor basado en DTO
        UnidadMedida unidad = new UnidadMedida(dto);

        return unidadMedidaRepository.save(unidad);
    }

    @Override
    public UnidadMedida actualizarUnidadeMedida(int id, UnidadMedidaDTO dto) {

        UnidadMedida unidadExistente = unidadMedidaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe esa unidad de Medida"));

        String nuevoNombre = dto.getNombreMedida().trim();
        String nuevaSigla = dto.getSigla().trim();

        if (unidadMedidaRepository.existsOtherWithSameName(id, nuevoNombre)) {
            throw new DuplicadoException("Ya existe otra unidad de medida con ese nombre");
        }

        // 🔥 Solo actualizamos los campos necesarios
        unidadExistente.setNombreMedida(nuevoNombre);
        unidadExistente.setSigla(nuevaSigla);

        return unidadMedidaRepository.save(unidadExistente);
    }

    @Override
    public void borrar(int id){
        UnidadMedida unidad = unidadMedidaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe la unidad de medida"));

        unidad.setActivo(false);
        unidadMedidaRepository.save(unidad);
    }
}

