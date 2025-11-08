package com.cristhian.SistemaInventario.Repositorio;

import com.cristhian.SistemaInventario.Modelo.Ciudad;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CiudadRepository extends JpaRepository<Ciudad, Integer> {
    Optional<Ciudad> findByCiudad(String ciudad);
    boolean existsByCiudadIgnoreCase(String ciudad);
    Optional<Ciudad> findByCiudadIgnoreCase(String ciudad);

    //Devuelve las ciudades Activas
    List<Ciudad> findByActivoTrue();
}
