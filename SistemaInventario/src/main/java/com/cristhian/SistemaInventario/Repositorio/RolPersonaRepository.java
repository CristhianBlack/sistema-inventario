package com.cristhian.SistemaInventario.Repositorio;

import com.cristhian.SistemaInventario.Enums.NombreRol;
import com.cristhian.SistemaInventario.Modelo.RolPersona;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RolPersonaRepository extends JpaRepository<RolPersona, Integer> {



    Optional<RolPersona> findByNombreRol(NombreRol nombreRol);

    //Devuelve los roles Activos
    List<RolPersona> findByActivoTrue();

    boolean existsByNombreRol(NombreRol nombreRol);
}

