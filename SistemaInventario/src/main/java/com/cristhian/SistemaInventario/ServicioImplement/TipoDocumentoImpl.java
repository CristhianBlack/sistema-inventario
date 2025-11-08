package com.cristhian.SistemaInventario.ServicioImplement;

import com.cristhian.SistemaInventario.Modelo.TipoDocumento;
import com.cristhian.SistemaInventario.Repositorio.TipoDocumentoRepository;
import com.cristhian.SistemaInventario.Service.ITipoDocumentoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class TipoDocumentoImpl implements ITipoDocumentoService {

    private final TipoDocumentoRepository tipoDocumentoRepository;


    public TipoDocumentoImpl(TipoDocumentoRepository tipoDocumentoRepository) {
        this.tipoDocumentoRepository = tipoDocumentoRepository;
    }

    @Override
    public List<TipoDocumento> listarTipoDocumentoActivo() {
        return tipoDocumentoRepository.findByActivoTrue();
    }

    @Override
    public Optional<TipoDocumento> buscarTipoDucmentoId(int id) {
        return tipoDocumentoRepository.findById(id);
    }

    @Override
    public TipoDocumento guardarTipoDocumento(TipoDocumento tipoDocumento) {
        return tipoDocumentoRepository.save(tipoDocumento);
    }

    @Override
    public void desactivarTipoDcomuento(int id) {
        TipoDocumento tipoDocumento = tipoDocumentoRepository.findById(id).orElse(null);
        if (tipoDocumento != null){
            tipoDocumento.setActivo(false);
            tipoDocumentoRepository.save(tipoDocumento);
        }
    }

    @Override
    public boolean existePorNombreTipoDocumento(String nombreTipoDocumento) {
        return tipoDocumentoRepository.existsByNombreTipoDocumento(nombreTipoDocumento);
    }
}






