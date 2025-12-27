package com.cristhian.SistemaInventario.Repositorio;

import com.cristhian.SistemaInventario.Modelo.VentaPago;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VentaPagoRepository extends JpaRepository<VentaPago, Integer> {

    List<VentaPago> findByVentaIdVenta(Long idVenta);
}
