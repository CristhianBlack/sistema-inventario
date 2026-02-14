package com.cristhian.SistemaInventario.Repositorio;

import com.cristhian.SistemaInventario.Enums.NombreTipoDocumento;
import com.cristhian.SistemaInventario.Modelo.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TipoDocumentoRepository extends JpaRepository<TipoDocumento, Integer> {

    //boolean existsByNombreTipoDocumento(String nombreTipoDocumento);

    //Optional<TipoDocumento> findByNombreTipoDocumentoIgnoreCase(String nombreTipoDocumento );

    List<TipoDocumento> findByActivoTrue();

    Optional<TipoDocumento> findByNombreTipoDocumento(NombreTipoDocumento nombreTipoDocumento);

    boolean existsByNombreTipoDocumento(NombreTipoDocumento nombreTipoDocumento);
}

