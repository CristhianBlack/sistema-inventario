package com.cristhian.SistemaInventario.Repositorio;

import com.cristhian.SistemaInventario.Modelo.Impuesto;
import com.cristhian.SistemaInventario.Modelo.NombreFormaPago;
import com.cristhian.SistemaInventario.Modelo.TipoImpuesto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ImpuestoRepository extends JpaRepository<Impuesto, Integer> {

    public List<Impuesto> findByActivoTrue();

    boolean existsByTipoImpuesto(TipoImpuesto tipoImpuesto);
}
