package com.cristhian.SistemaInventario.ServicioImplement;

import com.cristhian.SistemaInventario.DTO.AsientoContableDTO;
import com.cristhian.SistemaInventario.DTO.MovimientoContableDTO;
import com.cristhian.SistemaInventario.Enums.TipoAsiento;
import com.cristhian.SistemaInventario.Modelo.*;
import com.cristhian.SistemaInventario.Repositorio.AsientoContableRepository;
import com.cristhian.SistemaInventario.Repositorio.CuentaContableRepository;
import com.cristhian.SistemaInventario.Repositorio.UsuarioRepository;
import com.cristhian.SistemaInventario.Security.UsuarioAutenticadoProvider;
import com.cristhian.SistemaInventario.Service.IAsientoContableService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class AsientoContableServiceImpl implements IAsientoContableService {

    // Repositorio para la persistencia de asientos contables
    private final AsientoContableRepository asientoContableRepository;

    // Repositorio para la consulta de cuentas contables
    private final CuentaContableRepository cuentaContableRepository;

    // Proveedor del usuario autenticado desde el contexto de seguridad
    private final UsuarioAutenticadoProvider usuarioProvider;

    // Repositorio de usuarios (inyección para coherencia con el contexto de seguridad)
    private final UsuarioRepository usuarioRepository;

    public AsientoContableServiceImpl(AsientoContableRepository asientoContableRepository,
                                      CuentaContableRepository cuentaContableRepository,
                                      UsuarioAutenticadoProvider usuarioProvider,
                                      UsuarioRepository usuarioRepository) {
        this.asientoContableRepository = asientoContableRepository;
        this.cuentaContableRepository = cuentaContableRepository;
        this.usuarioProvider = usuarioProvider;
        this.usuarioRepository = usuarioRepository;
    }

    /**
     * Registra un asiento contable normal asociado opcionalmente a una compra y/o pago.
     *
     * Reglas de negocio:
     * - El asiento debe tener al menos un movimiento contable
     * - El total del DEBE debe ser igual al total del HABER
     * - El asiento queda asociado al usuario autenticado
     *
     * @param dto    datos del asiento contable
     * @param compra compra asociada al asiento (puede ser null)
     * @param pago   pago asociado al asiento (puede ser null)
     */
    @Override
    public void registrarAsiento(AsientoContableDTO dto, Compra compra, CompraPago pago, Venta venta, VentaPago ventaPago) {

        // Obtiene el usuario autenticado desde el contexto de seguridad
        Usuario usuarioAuth = usuarioProvider.obtenerUsuarioAutenticado();

        // Validaciones defensivas del contexto de usuario
        if (usuarioAuth == null) {
            throw new RuntimeException("USUARIO AUTENTICADO ES NULL");
        }

        if (usuarioAuth.getIdUsuario() == null) {
            throw new RuntimeException("ID USUARIO ES NULL");
        }

        System.out.println("USUARIO OK → ID: " + usuarioAuth.getIdUsuario());

        // El asiento contable debe contener al menos un movimiento
        if (dto.getMovimientos() == null || dto.getMovimientos().isEmpty()) {
            throw new IllegalArgumentException("El asiento debe tener movimientos contables");
        }

        // Creación del asiento contable
        AsientoContable asiento = new AsientoContable();
        asiento.setFecha(dto.getFecha());
        asiento.setDescripcion(dto.getDescripcion());
        // asiento.setTotal(dto.getTotal()); // Total no utilizado actualmente
        asiento.setUsuario(usuarioAuth);
        asiento.setCompra(compra);
        asiento.setPago(pago);
        asiento.setVenta(venta);
        asiento.setVentaPago(ventaPago);
        asiento.setTipo(TipoAsiento.NORMAL);

        // Lista de movimientos asociados al asiento
        List<MovimientoContable> movimientos = new ArrayList<>();

        // Acumuladores para validación contable
        BigDecimal totalDebe = BigDecimal.ZERO;
        BigDecimal totalHaber = BigDecimal.ZERO;

        // Construcción de los movimientos contables
        for (MovimientoContableDTO movDTO : dto.getMovimientos()) {

            // Obtiene la cuenta contable por su código
            CuentaContable cuenta = cuentaContableRepository
                    .findByCodigo(movDTO.getCodigoCuenta())
                    .orElseThrow(() ->
                            new RuntimeException("Cuenta no existe: " + movDTO.getCodigoCuenta())
                    );

            MovimientoContable mov = new MovimientoContable();
            mov.setAsiento(asiento);
            mov.setCuenta(cuenta);

            // Normalización de valores nulos
            BigDecimal debe = movDTO.getDebe() != null ? movDTO.getDebe() : BigDecimal.ZERO;
            BigDecimal haber = movDTO.getHaber() != null ? movDTO.getHaber() : BigDecimal.ZERO;

            mov.setDebe(debe);
            mov.setHaber(haber);

            // Acumulación de totales para validación
            totalDebe = totalDebe.add(debe);
            totalHaber = totalHaber.add(haber);

            movimientos.add(mov);
        }

        // Validación contable: el asiento debe estar balanceado
        if (totalDebe.compareTo(totalHaber) != 0) {
            throw new IllegalStateException(
                    "Asiento desbalanceado. Debe=" + totalDebe + " Haber=" + totalHaber
            );
        }

        // Asociación de movimientos y persistencia del asiento
        asiento.setMovimientos(movimientos);
        asientoContableRepository.save(asiento);
    }
}
