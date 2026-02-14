package com.cristhian.SistemaInventario.ServicioImplement;

import com.cristhian.SistemaInventario.Modelo.CuentaContable;
import com.cristhian.SistemaInventario.Repositorio.CuentaContableRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Servicio encargado de la gestión de cuentas contables.
 *
 * Esta clase actúa como capa intermedia entre el controlador
 * y el repositorio de cuentas contables.
 *
 * No contiene lógica de negocio compleja, únicamente delega
 * la consulta al repositorio.
 */
@Service
@Transactional
public class CuentaContableService {

    /**
     * Repositorio de cuentas contables.
     * Se inyecta por constructor para favorecer la inmutabilidad
     * y facilitar pruebas unitarias.
     */
    private final CuentaContableRepository cuentaContableRepository;

    /**
     * Constructor con inyección de dependencias.
     *
     * @param cuentaContableRepository repositorio de cuentas contables
     */
    public CuentaContableService(CuentaContableRepository cuentaContableRepository) {
        this.cuentaContableRepository = cuentaContableRepository;
    }

    /**
     * Obtiene el listado completo de cuentas contables.
     *
     * Este método consulta directamente al repositorio
     * y retorna todas las cuentas registradas.
     *
     * @return lista de cuentas contables
     */
    public List<CuentaContable> listarCuentas() {
        System.out.println("CUENTAS: " + cuentaContableRepository.listarTodas());
        return cuentaContableRepository.listarTodas();
    }
}
