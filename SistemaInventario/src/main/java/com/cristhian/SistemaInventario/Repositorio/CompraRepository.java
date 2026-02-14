package com.cristhian.SistemaInventario.Repositorio;

import com.cristhian.SistemaInventario.Modelo.Ciudad;
import com.cristhian.SistemaInventario.Modelo.Compra;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface CompraRepository extends JpaRepository<Compra, Integer> {

    long count();
    List<Compra> findByActivoTrueOrderByFechaCompraDesc();

    @Query("""
  SELECT COALESCE(SUM(c.totalCompra), 0)
  FROM Compra c
  WHERE MONTH(c.fechaCompra) = MONTH(CURRENT_DATE)
    AND YEAR(c.fechaCompra) = YEAR(CURRENT_DATE)
    AND c.estado IN ('CONFIRMADA', 'PAGADA', 'PARCIAL')
""")
    BigDecimal totalComprasMes();
}