package com.cristhian.SistemaInventario.Repositorio;

import com.cristhian.SistemaInventario.Modelo.Ciudad;
import com.cristhian.SistemaInventario.Modelo.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Integer> {

    long count();
    boolean existsByNombreProductoIgnoreCase(String nombreProducto);

    List<Producto> findByActivoTrue();

    //Producto findByNombreProductoIgnoreCase(String nombreProducto);
    Optional<Producto> findByNombreProductoIgnoreCase(String nombreProducto);

    @Query("""
  SELECT COALESCE(SUM(p.stock * p.costoPromedio), 0)
  FROM Producto p
""")
    BigDecimal valorInventario();
}

