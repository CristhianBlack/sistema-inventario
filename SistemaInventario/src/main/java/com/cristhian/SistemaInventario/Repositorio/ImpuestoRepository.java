package com.cristhian.SistemaInventario.Repositorio;

import com.cristhian.SistemaInventario.Modelo.Impuesto;
import com.cristhian.SistemaInventario.Enums.TipoImpuesto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ImpuestoRepository extends JpaRepository<Impuesto, Integer> {

    public List<Impuesto> findByActivoTrue();

    boolean existsByTipoImpuesto(TipoImpuesto tipoImpuesto);
}
