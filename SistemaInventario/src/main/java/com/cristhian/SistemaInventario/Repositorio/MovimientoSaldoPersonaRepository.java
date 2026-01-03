package com.cristhian.SistemaInventario.Repositorio;

import com.cristhian.SistemaInventario.Modelo.MovimientoSaldoPersona;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MovimientoSaldoPersonaRepository extends JpaRepository<MovimientoSaldoPersona, Integer> {
}
