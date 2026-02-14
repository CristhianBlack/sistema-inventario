package com.cristhian.SistemaInventario.Repositorio;

import com.cristhian.SistemaInventario.Enums.TipoAsiento;
import com.cristhian.SistemaInventario.Modelo.AsientoContable;
import com.cristhian.SistemaInventario.Modelo.Compra;
import com.cristhian.SistemaInventario.Modelo.CompraPago;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AsientoContableRepository extends JpaRepository<AsientoContable, Integer> {

    long count();

    Optional<AsientoContable> findByCompra(Compra compra);
    Optional<AsientoContable> findByPago(CompraPago compraPago);

    boolean existsByTipo(TipoAsiento tipo);
}
