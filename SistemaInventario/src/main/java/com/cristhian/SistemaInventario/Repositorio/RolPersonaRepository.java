package com.cristhian.SistemaInventario.Repositorio;

import com.cristhian.SistemaInventario.Modelo.Ciudad;
import com.cristhian.SistemaInventario.Modelo.RolPersona;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RolPersonaRepository extends JpaRepository<RolPersona, Integer> {

    Optional<RolPersona> findByNombreRol(String nombreRol);

    //Devuelve los roles Activos
    List<RolPersona> findByActivoTrue();
}
