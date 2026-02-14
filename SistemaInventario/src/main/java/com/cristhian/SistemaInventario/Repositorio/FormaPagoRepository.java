package com.cristhian.SistemaInventario.Repositorio;


import com.cristhian.SistemaInventario.Modelo.FormaPago;
import com.cristhian.SistemaInventario.Enums.NombreFormaPago;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface FormaPagoRepository extends JpaRepository<FormaPago, Integer> {

    List<FormaPago> findByActivoTrue();

    boolean existsByNombreFormaPago(NombreFormaPago nombreFormaPago);

}
