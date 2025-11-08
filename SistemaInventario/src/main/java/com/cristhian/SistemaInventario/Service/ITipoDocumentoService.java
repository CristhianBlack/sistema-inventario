package com.cristhian.SistemaInventario.Service;

import com.cristhian.SistemaInventario.Modelo.TipoDocumento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ITipoDocumentoService {

    List<TipoDocumento> listarTipoDocumentoActivo();
    Optional<TipoDocumento> buscarTipoDucmentoId(int id);
    public TipoDocumento guardarTipoDocumento(TipoDocumento tipoDocumento);
    public void desactivarTipoDcomuento(int id);
    public boolean existePorNombreTipoDocumento(String nombreTipoDocumento);
}
