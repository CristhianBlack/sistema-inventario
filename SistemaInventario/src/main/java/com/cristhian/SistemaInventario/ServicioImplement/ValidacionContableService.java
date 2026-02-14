package com.cristhian.SistemaInventario.ServicioImplement;

import com.cristhian.SistemaInventario.Enums.TipoAsiento;
import com.cristhian.SistemaInventario.Excepciones.AperturaNoExisteException;
import com.cristhian.SistemaInventario.Repositorio.AsientoContableRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Servicio encargado de realizar validaciones contables generales
 * antes de permitir el registro de determinados asientos.
 *
 * Actualmente valida que exista el asiento de APERTURA,
 * el cual es obligatorio antes de registrar otros movimientos contables.
 */
@Service
@Transactional
public class ValidacionContableService {

    /**
     * Repositorio de asientos contables.
     * Se utiliza para verificar la existencia de asientos previos en la base de datos.
     */
    private final AsientoContableRepository asientoRepo;

    /**
     * Constructor con inyección de dependencias.
     *
     * @param asientoRepo repositorio de asientos contables
     */
    public ValidacionContableService(AsientoContableRepository asientoRepo) {
        this.asientoRepo = asientoRepo;
    }

    /**
     * Valida que exista un asiento de APERTURA antes de permitir
     * el registro de cualquier otro tipo de asiento contable.
     *
     * Regla contable:
     * - Si no existe un asiento de apertura
     * - y el tipo de asiento que se intenta registrar NO es APERTURA
     * entonces se lanza una excepción.
     *
     * @param tipo tipo de asiento que se intenta registrar
     * @throws AperturaNoExisteException si no existe el asiento de apertura
     */
    public void validarApertura(TipoAsiento tipo) {

        // Verifica si existe un asiento contable de tipo APERTURA
        boolean existeApertura =
                asientoRepo.existsByTipo(TipoAsiento.APERTURA);

        // Si no existe apertura y se intenta registrar otro tipo de asiento, se bloquea la operación
        if (!existeApertura && tipo != TipoAsiento.APERTURA) {
            throw new AperturaNoExisteException(
                    "Debe crear el asiento de apertura antes de registrar compras."
            );
        }
    }
}
