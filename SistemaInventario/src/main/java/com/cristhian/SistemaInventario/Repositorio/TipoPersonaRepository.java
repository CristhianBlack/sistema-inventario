package com.cristhian.SistemaInventario.Repositorio;

import com.cristhian.SistemaInventario.Enums.NombreTipoPersona;
import com.cristhian.SistemaInventario.Modelo.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TipoPersonaRepository extends JpaRepository<TipoPersona, Integer> {

    // boolean existsByNombreTipoPersona(String nombreTipoPersona);
    //Optional<TipoPersona> findByNombreTipoPersonaIgnoreCase(String nombreTipoPersona );

    List<TipoPersona> findByActivoTrue();

    boolean existsByNombreTipoPersona(NombreTipoPersona nombreTipoPersona);

    Optional<TipoPersona> findByNombreTipoPersona(NombreTipoPersona nombreTipoPersona);

}

