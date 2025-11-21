package com.cristhian.SistemaInventario.ServicioImplement;

import com.cristhian.SistemaInventario.DTO.TipoDocumentoDTO;
import com.cristhian.SistemaInventario.Excepciones.DuplicadoException;
import com.cristhian.SistemaInventario.Excepciones.RecursoNoEncontradoException;
import com.cristhian.SistemaInventario.Mensaje.Mensaje;
import com.cristhian.SistemaInventario.Modelo.Categoria;
import com.cristhian.SistemaInventario.Modelo.Ciudad;
import com.cristhian.SistemaInventario.Modelo.TipoDocumento;
import com.cristhian.SistemaInventario.Repositorio.TipoDocumentoRepository;
import com.cristhian.SistemaInventario.Service.ITipoDocumentoService;
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
public class TipoDocumentoImpl implements ITipoDocumentoService {

    private final TipoDocumentoRepository tipoDocumentoRepository;


    public TipoDocumentoImpl(TipoDocumentoRepository tipoDocumentoRepository) {
        this.tipoDocumentoRepository = tipoDocumentoRepository;
    }

    // -----------------------------------------------------------
    // 1. lISTAMOS LOS TIPOS DE DOCUMENTOS ACTIVOS
    // -----------------------------------------------------------
    @Override
    public List<TipoDocumento> listarTipoDocumentoActivo() {
        return tipoDocumentoRepository.findByActivoTrue();
    }

    // -----------------------------------------------------------
    // 2. BUSCAMOS EL TIPO DE DOCUEMNTO POR ID
    // -----------------------------------------------------------
    @Override
    public Optional<TipoDocumento> buscarTipoDucmentoId(int id) {
        return tipoDocumentoRepository.findById(id);
    }

    // -----------------------------------------------------------
    // 3. CREAMOS EL TIPO DE DOCUMENTO
    // -----------------------------------------------------------
    @Override
    public TipoDocumento guardarTipoDocumento(TipoDocumentoDTO dto) {

        String nombreNormalizado = dto.getNombreTipoDocumento().trim().toUpperCase();

        Optional<TipoDocumento> existente =
                tipoDocumentoRepository.findByNombreTipoDocumentoIgnoreCase(nombreNormalizado);

        if (existente.isPresent()) {
            TipoDocumento tipo = existente.get();

            if (!tipo.isActivo()) {
                tipo.setActivo(true); // si tienes campo activo
                return tipoDocumentoRepository.save(tipo);
            }

            throw new DuplicadoException("Ya existe un tipo de documento con ese nombre");
        }

        // Crear nueva entidad
        TipoDocumento nuevo = new TipoDocumento(dto);
        return tipoDocumentoRepository.save(nuevo);
    }

    // -----------------------------------------------------------
    // 4. ACTUALIZAMOS EL TIPO DOCUMENTO
    // -----------------------------------------------------------
    public TipoDocumento actualizarTipoDocumento(int id, TipoDocumentoDTO dto) {

        // Validar si existe el tipo de documento con ese ID
        TipoDocumento existente = tipoDocumentoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("El tipo de documento no existe"));

        String nombreNormalizado = dto.getNombreTipoDocumento().trim();

        // Validar si existe otro registro con el mismo nombre
        Optional<TipoDocumento> duplicado = tipoDocumentoRepository
                .findByNombreTipoDocumentoIgnoreCase(nombreNormalizado);

        if (duplicado.isPresent() && duplicado.get().getIdTipoDocumento() != id) {
            throw new DuplicadoException("Ya existe otro tipo de documento con ese nombre");
        }

        // Actualizar datos
        existente.setNombreTipoDocumento(dto.getNombreTipoDocumento());
        existente.setSigla(dto.getSigla());

        return tipoDocumentoRepository.save(existente);
    }


    // -----------------------------------------------------------
    // 5. DESACTIVAMOS EL TIPO DOCUMENTO
    // -----------------------------------------------------------
    @Override
    public void desactivarTipoDcomuento(int id) {
        TipoDocumento tipoDocumento = tipoDocumentoRepository.findById(id).orElse(null);
        if (tipoDocumento != null){
            tipoDocumento.setActivo(false);
            tipoDocumentoRepository.save(tipoDocumento);
        }
    }





}






