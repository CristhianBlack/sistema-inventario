package com.cristhian.SistemaInventario.Repositorio;

import com.cristhian.SistemaInventario.DTO.MovimientoInventarioDTO;
import com.cristhian.SistemaInventario.Modelo.MovimientoInventario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MovimientoInventarioRepository extends JpaRepository<MovimientoInventario, Integer> {

    long count();

    @Query("""
SELECT m 
FROM MovimientoInventario m
WHERE m.producto.idProducto = :idProducto
ORDER BY m.fechaMovimiento ASC, m.IdMovimientoInventario ASC
""")
    List<MovimientoInventario> listarPorProductoOrdenado(@Param("idProducto") int idProducto);

    /*@Query("""
SELECT m
FROM MovimientoInventario m
WHERE m.producto.idProducto = :idProducto
AND m.fechaMovimiento BETWEEN :desde AND :hasta
ORDER BY m.fechaMovimiento DESC, m.id DESC
""")
    List<MovimientoInventario> buscarPorProductoYFechas(
            @Param("idProducto") int idProducto,
            @Param("desde") LocalDateTime desde,
            @Param("hasta") LocalDateTime hasta
    );*/

    @Query("""
SELECT m
FROM MovimientoInventario m
WHERE m.producto.idProducto = :idProducto
AND m.fechaMovimiento >= :desde
AND m.fechaMovimiento < :hasta
ORDER BY m.fechaMovimiento ASC, m.IdMovimientoInventario ASC
""")
    List<MovimientoInventario> buscarPorProductoYFechas(
            @Param("idProducto") int idProducto,
            @Param("desde") LocalDateTime desde,
            @Param("hasta") LocalDateTime hasta
    );


    @Query("""
SELECT m
FROM MovimientoInventario m
LEFT JOIN FETCH m.producto p
LEFT JOIN FETCH m.proveedor pr
LEFT JOIN FETCH pr.persona per
WHERE m.fechaMovimiento BETWEEN :desde AND :hasta
ORDER BY m.fechaMovimiento ASC, m.IdMovimientoInventario ASC
""")
    List<MovimientoInventario> buscarPorFechas(
            @Param("desde") LocalDateTime desde,
            @Param("hasta") LocalDateTime hasta
    );


}
