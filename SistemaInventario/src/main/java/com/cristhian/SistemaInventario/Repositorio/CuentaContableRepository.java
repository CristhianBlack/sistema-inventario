package com.cristhian.SistemaInventario.Repositorio;

import com.cristhian.SistemaInventario.Modelo.CuentaContable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface CuentaContableRepository extends JpaRepository<CuentaContable, Long> {

    Optional<CuentaContable> findByCodigo(String codigo);

    boolean existsByCodigo(String codigo);

    @Query("""
        SELECT c
        FROM CuentaContable c
        ORDER BY c.codigo
    """)
    List<CuentaContable> listarTodas();

    @Query("""
SELECT c
FROM CuentaContable c
WHERE c.tipo = com.cristhian.SistemaInventario.Enums.TipoCuenta.PATRIMONIO
""")
    CuentaContable findCuentaPatrimonioPrincipal();
}
