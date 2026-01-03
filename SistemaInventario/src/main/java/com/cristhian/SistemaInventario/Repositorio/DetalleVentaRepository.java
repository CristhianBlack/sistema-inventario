package com.cristhian.SistemaInventario.Repositorio;

import com.cristhian.SistemaInventario.Modelo.DetalleVenta;
import com.cristhian.SistemaInventario.Modelo.Producto;
import com.cristhian.SistemaInventario.Modelo.Venta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DetalleVentaRepository extends JpaRepository<DetalleVenta, Integer> {

    Optional<DetalleVenta> findByVentaAndProducto(Venta venta, Producto producto);
}
