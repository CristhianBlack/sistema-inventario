package com.cristhian.SistemaInventario.Repositorio;

import com.cristhian.SistemaInventario.Modelo.UnidadMedida;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UnidadMedidaRepository extends JpaRepository<UnidadMedida, Integer> {

    @Query("""
       SELECT COUNT(u) > 0
       FROM UnidadMedida u
       WHERE TRIM(LOWER(u.nombreMedida)) = TRIM(LOWER(:nombreMedida))
       AND u.idUnidadMedida <> :id
       """)
    boolean existsOtherWithSameName(@Param("id") int id, @Param("nombreMedida") String nombreMedida);

    boolean existsByNombreMedidaIgnoreCase(String nombreMedida);

    List<UnidadMedida> findByActivoTrue();
}