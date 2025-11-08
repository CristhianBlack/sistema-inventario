package com.cristhian.SistemaInventario.Repositorio;

import com.cristhian.SistemaInventario.Modelo.TipoPersona;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TipoPersonaRepository extends JpaRepository<TipoPersona, Integer> {

    boolean existsByNombreTipoPersona(String nombreTipoPersona);

    List<TipoPersona> findByActivoTrue();

}
