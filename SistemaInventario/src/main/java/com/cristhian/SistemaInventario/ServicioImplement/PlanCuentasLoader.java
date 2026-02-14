package com.cristhian.SistemaInventario.ServicioImplement;

import com.cristhian.SistemaInventario.Enums.TipoCuenta;
import com.cristhian.SistemaInventario.Modelo.CuentaContable;
import com.cristhian.SistemaInventario.Repositorio.CuentaContableRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * Componente encargado de cargar automáticamente el Plan de Cuentas
 * al iniciar la aplicación.
 *
 * Implementa CommandLineRunner para ejecutarse una sola vez
 * cuando Spring Boot termina de arrancar.
 */
@Component
@RequiredArgsConstructor
public class PlanCuentasLoader implements CommandLineRunner {

    // Repositorio para persistir y consultar cuentas contables
    private final CuentaContableRepository repo;

    /**
     * Método que se ejecuta automáticamente al iniciar la aplicación.
     * Aquí se definen las cuentas contables base del sistema.
     */
    @Override
    public void run(String... args) {

        // -----------------------------
        // CUENTAS DE ACTIVO
        // -----------------------------
        crear("1105", "Caja", TipoCuenta.ACTIVO);
        crear("1110", "Bancos", TipoCuenta.ACTIVO);
        crear("1435", "Inventarios", TipoCuenta.ACTIVO);
        crear("2408", "IVA Crédito Fiscal", TipoCuenta.ACTIVO);
        crear("1305","Clientes", TipoCuenta.ACTIVO);
        crear("4135", "Ingreso por ventas", TipoCuenta.INGRESO);
        crear("240805", "Iva Generado", TipoCuenta.PASIVO);
        crear("6135", "Costo de Venta", TipoCuenta.GASTO);

        // -----------------------------
        // CUENTAS DE PASIVO
        // -----------------------------
        crear("2205", "Proveedores", TipoCuenta.PASIVO);

        // -----------------------------
        // CUENTAS DE PATRIMONIO
        // (Cuenta obligatoria del sistema)
        // -----------------------------
        crear("3105", "Capital Social", TipoCuenta.PATRIMONIO);
    }

    /**
     * Método auxiliar para crear una cuenta contable
     * solo si no existe previamente por su código.
     *
     * @param codigo Código contable único
     * @param nombre Nombre descriptivo de la cuenta
     * @param tipo   Tipo de cuenta (ACTIVO, PASIVO, PATRIMONIO)
     */
    private void crear(String codigo, String nombre, TipoCuenta tipo) {

        // Validar que la cuenta no exista ya en la base de datos
        if (!repo.existsByCodigo(codigo)) {

            // Crear nueva cuenta contable
            CuentaContable cuenta = new CuentaContable();
            cuenta.setCodigo(codigo);
            cuenta.setNombre(nombre);
            cuenta.setTipo(tipo);

            // Guardar la cuenta en la base de datos
            repo.save(cuenta);
        }
    }
}
