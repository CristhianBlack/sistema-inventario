package com.cristhian.SistemaInventario.Repositorio;

import com.cristhian.SistemaInventario.Modelo.CompraPago;
import com.cristhian.SistemaInventario.Modelo.VentaPago;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CompraPagoRepository extends JpaRepository<CompraPago, Integer> {

    List<CompraPago> findByCompraIdCompra(Long idVenta);
}
