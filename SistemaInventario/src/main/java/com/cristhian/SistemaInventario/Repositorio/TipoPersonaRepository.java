package com.cristhian.SistemaInventario.Repositorio;

import com.cristhian.SistemaInventario.Modelo.TipoDocumento;
import com.cristhian.SistemaInventario.Modelo.TipoPersona;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TipoPersonaRepository extends JpaRepository<TipoPersona, Integer> {

    boolean existsByNombreTipoPersona(String nombreTipoPersona);
    Optional<TipoPersona> findByNombreTipoPersonaIgnoreCase(String nombreTipoPersona );

    List<TipoPersona> findByActivoTrue();

}
