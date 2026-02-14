package com.cristhian.SistemaInventario.Repositorio;

import com.cristhian.SistemaInventario.Modelo.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoriaRepository extends JpaRepository<Categoria, Integer> {

    boolean existsByNombreCategoriaIgnoreCase(String nombreCategoria);
    List<Categoria> findByActivoTrue();
    Optional<Categoria> findByNombreCategoriaIgnoreCase(String nombreCategoria);
}
