package com.cristhian.SistemaInventario.Repositorio;

import com.cristhian.SistemaInventario.DTO.BalanceLineaDTO;
import com.cristhian.SistemaInventario.DTO.LibroDiarioDTO;
import com.cristhian.SistemaInventario.DTO.MayorGeneralDTO;
import com.cristhian.SistemaInventario.Modelo.MovimientoContable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface MovimientoContableRepository extends JpaRepository<MovimientoContable, Integer> {

    //Es te query es para exportar libro diario en excel y pdf
    @Query("""
    SELECT new com.cristhian.SistemaInventario.DTO.LibroDiarioDTO(
        a.fecha,
        a.idAsiento,
        a.descripcion,
        c.codigo,
        c.nombre,
        m.debe,
        m.haber
    )
    FROM MovimientoContable m
    JOIN m.asiento a
    JOIN m.cuenta c
    WHERE (:desde IS NULL OR a.fecha >= :desde)
      AND (:hasta IS NULL OR a.fecha <= :hasta)
    ORDER BY a.fecha DESC, a.idAsiento DESC
""")
    List<LibroDiarioDTO> obtenerLibroDiario(
            @Param("desde") LocalDateTime desde,
            @Param("hasta") LocalDateTime hasta
    );

    //Este Query es para mostrar en la parntalla de la vista el libro diario paginado
    @Query(
            value = """
        SELECT new com.cristhian.SistemaInventario.DTO.LibroDiarioDTO(
            a.fecha,
            a.idAsiento,
            a.descripcion,
            c.codigo,
            c.nombre,
            m.debe,
            m.haber
        )
        FROM MovimientoContable m
        JOIN m.asiento a
        JOIN m.cuenta c
        WHERE (:desde IS NULL OR a.fecha >= :desde)
          AND (:hasta IS NULL OR a.fecha <= :hasta)
        ORDER BY a.fecha DESC, a.idAsiento DESC
    """,
            countQuery = """
        SELECT COUNT(m)
        FROM MovimientoContable m
        JOIN m.asiento a
        WHERE (:desde IS NULL OR a.fecha >= :desde)
          AND (:hasta IS NULL OR a.fecha <= :hasta)
    """
    )
    Page<LibroDiarioDTO> obtenerLibroDiarioPaginado(
            @Param("desde") LocalDateTime desde,
            @Param("hasta") LocalDateTime hasta,
            Pageable pageable
    );

    /*@Query("""
SELECT m
FROM MovimientoContable m
JOIN FETCH m.cuenta c
JOIN FETCH m.asiento a
WHERE (:desde IS NULL OR a.fecha >= :desde)
AND   (:hasta IS NULL OR a.fecha <= :hasta)
ORDER BY 
    c.codigo,
    a.fecha,
    a.idAsiento,
    m.idMovimiento
""")
    List<MovimientoContable> obtenerMayorGeneral(
            LocalDate desde,
            LocalDate hasta
    );*/

    @Query("""
SELECT new com.cristhian.SistemaInventario.DTO.BalanceLineaDTO(
    c.tipo,
    c.codigo,
    c.nombre,
    c.idCuenta,
    CASE
        WHEN c.tipo IN (
            com.cristhian.SistemaInventario.Enums.TipoCuenta.ACTIVO,
            com.cristhian.SistemaInventario.Enums.TipoCuenta.GASTO
        )
        THEN COALESCE(SUM(m.debe),0) - COALESCE(SUM(m.haber),0)

        ELSE
        COALESCE(SUM(m.haber),0) - COALESCE(SUM(m.debe),0)
    END
)
FROM MovimientoContable m
JOIN m.cuenta c
GROUP BY c.idCuenta, c.tipo, c.codigo, c.nombre
HAVING
    CASE
        WHEN c.tipo IN (
            com.cristhian.SistemaInventario.Enums.TipoCuenta.ACTIVO,
            com.cristhian.SistemaInventario.Enums.TipoCuenta.GASTO
        )
        THEN COALESCE(SUM(m.debe),0) - COALESCE(SUM(m.haber),0)
        ELSE COALESCE(SUM(m.haber),0) - COALESCE(SUM(m.debe),0)
    END <> 0
""")
    List<BalanceLineaDTO> obtenerBalance();

    /*@Query("""
SELECT COALESCE(SUM(m.debe),0) - COALESCE(SUM(m.haber),0)
FROM MovimientoContable m
JOIN m.cuenta c
WHERE c.idCuenta = :idCuenta
AND (
   m.asiento.fecha < :fecha
   OR (m.asiento.fecha = :fecha AND m.idMovimiento < :idMovimiento)
)
""")
    BigDecimal calcularSaldoAntes(
            @Param("idCuenta") Long idCuenta,
            @Param("fecha") LocalDate fecha,
            @Param("idMovimiento") Long idMovimiento
    );*/

   /* @Query("""
SELECT new com.cristhian.SistemaInventario.DTO.MayorGeneralDTO(
    a.fecha,
    a.descripcion,
    m.debe,
    m.haber
)
FROM MovimientoContable m
JOIN m.asiento a
JOIN m.cuenta c
WHERE c.idCuenta = :idCuenta
ORDER BY a.fecha, m.idMovimiento
""")
    Page<MayorGeneralDTO> obtenerMayorPaginado(
            @Param("idCuenta") Long idCuenta,
            Pageable pageable
    );*/

    @Query("""
SELECT new com.cristhian.SistemaInventario.DTO.MayorGeneralDTO(
    c.idCuenta,
    c.codigo,
    c.nombre,
    a.fecha,
    a.descripcion,
    m.debe,
    m.haber
)
FROM MovimientoContable m
JOIN m.asiento a
JOIN m.cuenta c
WHERE c.idCuenta = :idCuenta
  AND (:desde IS NULL OR a.fecha >= :desde)
  AND (:hasta IS NULL OR a.fecha <= :hasta)
ORDER BY a.fecha, m.idMovimiento
""")
    Page<MayorGeneralDTO> obtenerMayorPaginado(
            @Param("idCuenta") Long idCuenta,
            @Param("desde") LocalDateTime desde,
            @Param("hasta") LocalDateTime hasta,
            Pageable pageable
    );





}
