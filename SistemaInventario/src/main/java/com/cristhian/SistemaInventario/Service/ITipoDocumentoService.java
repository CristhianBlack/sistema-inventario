package com.cristhian.SistemaInventario.Service;

import com.cristhian.SistemaInventario.DTO.TipoDocumentoDTO;
import com.cristhian.SistemaInventario.Modelo.TipoDocumento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ITipoDocumentoService {

    List<TipoDocumento> listarTipoDocumentoActivo();
    Optional<TipoDocumento> buscarTipoDucmentoId(int id);
    public TipoDocumento guardarTipoDocumento(TipoDocumentoDTO tipoDocumentoDTO);

    public TipoDocumento actualizarTipoDocumento(int id, TipoDocumentoDTO tipoDocumentoDTO);
    public void desactivarTipoDcomuento(int id);

}
