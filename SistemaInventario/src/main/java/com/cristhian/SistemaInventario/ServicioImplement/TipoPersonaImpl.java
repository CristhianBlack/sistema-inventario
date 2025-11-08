package com.cristhian.SistemaInventario.ServicioImplement;

import com.cristhian.SistemaInventario.Modelo.TipoPersona;
import com.cristhian.SistemaInventario.Repositorio.TipoPersonaRepository;
import com.cristhian.SistemaInventario.Service.ITipoPersonaService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    public TipoPersona guardarTipoPersona(TipoPersona tipoPersona) {
        return tipoPersonaRepository.save(tipoPersona);
    }

    @Override
    public void desactivarTipoDocumento(int id) {
        TipoPersona tipoPersona = tipoPersonaRepository.findById(id).orElse(null);
        if (tipoPersona != null){
            tipoPersona.setActivo(false);
            tipoPersonaRepository.save(tipoPersona);
        }
    }

    @Override
    public boolean existePorNombreTipoPersona(String nombreTipoPersona) {
        return tipoPersonaRepository.existsByNombreTipoPersona(nombreTipoPersona);
    }
}
