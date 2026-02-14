package com.cristhian.SistemaInventario.Repositorio;

import com.cristhian.SistemaInventario.Modelo.Compra;
import com.cristhian.SistemaInventario.Modelo.Venta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface VentaRepository extends JpaRepository<Venta, Integer> {

    List<Venta> findAllByOrderByFechaVentaDesc();

    @Query("""
  SELECT COALESCE(SUM(v.totalVenta), 0)
  FROM Venta v
  WHERE MONTH(v.fechaVenta) = MONTH(CURRENT_DATE)
    AND YEAR(v.fechaVenta) = YEAR(CURRENT_DATE)
    AND v.estado IN ('CONFIRMADA', 'PAGADA', 'PARCIAL')
""")
    BigDecimal totalVentasMes();
}
