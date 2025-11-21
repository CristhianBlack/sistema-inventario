package com.cristhian.SistemaInventario.ServicioImplement;

import com.cristhian.SistemaInventario.DTO.TipoPersonaDTO;
import com.cristhian.SistemaInventario.Excepciones.DuplicadoException;
import com.cristhian.SistemaInventario.Excepciones.RecursoNoEncontradoException;
import com.cristhian.SistemaInventario.Mensaje.Mensaje;
import com.cristhian.SistemaInventario.Modelo.TipoDocumento;
import com.cristhian.SistemaInventario.Modelo.TipoPersona;
import com.cristhian.SistemaInventario.Repositorio.TipoPersonaRepository;
import com.cristhian.SistemaInventario.Service.ITipoPersonaService;
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
public class TipoPersonaImpl implements ITipoPersonaService {

    private final TipoPersonaRepository tipoPersonaRepository;

    public TipoPersonaImpl(TipoPersonaRepository tipoPersonaRepository) {
        this.tipoPersonaRepository = tipoPersonaRepository;
    }

    @Override
    public List<TipoPersona> listarTipoPersonaActiva() {
        return tipoPersonaRepository.findByActivoTrue();
    }

    @Override
    public Optional<TipoPersona> buscarTipoPersonaId(int id) {
        return tipoPersonaRepository.findById(id);
    }

    @Override
    public TipoPersona guardarTipoPersona(TipoPersonaDTO tipoPersonaDTO) {

        // Normalizar el nombre
        String nombreNormalizado = tipoPersonaDTO.getNombreTipoPersona().trim();

        // Buscar por nombre ignorando mayúsculas/minúsculas
        Optional<TipoPersona> existente =
                tipoPersonaRepository.findByNombreTipoPersonaIgnoreCase(nombreNormalizado);

        if (existente.isPresent()) {
            TipoPersona tipoPersona = existente.get();

            // Si existe pero está inactivo, lo reactivamos
            if (!tipoPersona.isActivo()) {
                tipoPersona.setActivo(true);
                return tipoPersonaRepository.save(tipoPersona);
            }

            // Si existe y está activo → error
            throw new DuplicadoException("Ya existe un tipo de persona con ese nombre");
        }

        // Crear nueva entidad
        TipoPersona tipoPersonaNuevo = new TipoPersona(tipoPersonaDTO);
        tipoPersonaNuevo.setNombreTipoPersona(nombreNormalizado); // ← importante

        return tipoPersonaRepository.save(tipoPersonaNuevo);
    }

    @Override
    public TipoPersona actualizarTipoPersona(int id, TipoPersonaDTO tipoPersonaDTO) {

        //Validar existencia
        TipoPersona tipoPersonaExistente = tipoPersonaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("El tipo de persona no existe"));

        // Normalizar nombre
        String nombreNormalizado = tipoPersonaDTO.getNombreTipoPersona().trim();

        //Validar duplicados
        Optional<TipoPersona> duplicado = tipoPersonaRepository
                .findByNombreTipoPersonaIgnoreCase(nombreNormalizado);

        if (duplicado.isPresent() && duplicado.get().getIdTipoPersona() != id) {
            throw new DuplicadoException("Ya existe ese tipo de persona");
        }

        // Actualizar valores
        tipoPersonaExistente.setNombreTipoPersona(nombreNormalizado);

        return tipoPersonaRepository.save(tipoPersonaExistente);
    }

    @Override
    public void desactivarTipoDocumento(int id) {
        TipoPersona tipoPersona = tipoPersonaRepository.findById(id).orElse(null);
        if (tipoPersona != null){
            tipoPersona.setActivo(false);
            tipoPersonaRepository.save(tipoPersona);
        }
    }


}
