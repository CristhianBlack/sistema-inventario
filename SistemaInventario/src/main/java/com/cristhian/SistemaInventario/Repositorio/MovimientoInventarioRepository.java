package com.cristhian.SistemaInventario.Repositorio;

import com.cristhian.SistemaInventario.Modelo.MovimientoInventario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface MovimientoInventarioRepository extends JpaRepository<MovimientoInventario, Integer> {

    @Query("""
SELECT m 
FROM MovimientoInventario m
WHERE m.producto.idProducto = :idProducto
ORDER BY m.fechaMovimiento ASC, m.id ASC
""")
    List<MovimientoInventario> listarPorProductoOrdenado(@Param("idProducto") int idProducto);

    @Query("""
SELECT m
FROM MovimientoInventario m
WHERE m.producto.idProducto = :idProducto
AND m.fechaMovimiento BETWEEN :desde AND :hasta
ORDER BY m.fechaMovimiento ASC, m.id ASC
""")
    List<MovimientoInventario> buscarPorProductoYFechas(
            @Param("idProducto") int idProducto,
            @Param("desde") LocalDate desde,
            @Param("hasta") LocalDate hasta
    );

    @Query("""
SELECT m
FROM MovimientoInventario m
WHERE m.fechaMovimiento BETWEEN :desde AND :hasta
ORDER BY m.fechaMovimiento ASC, m.id ASC
""")
    List<MovimientoInventario> buscarPorFechas(

            @Param("desde") LocalDate desde,
            @Param("hasta") LocalDate hasta
    );


}
