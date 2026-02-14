package com.cristhian.SistemaInventario.ServicioImplement;

import com.cristhian.SistemaInventario.DTO.AperturaCuentaDTO;
import com.cristhian.SistemaInventario.Enums.TipoAsiento;
import com.cristhian.SistemaInventario.Enums.TipoCuenta;
import com.cristhian.SistemaInventario.Excepciones.AperturaNoExisteException;
import com.cristhian.SistemaInventario.Excepciones.AperturaYaExisteException;
import com.cristhian.SistemaInventario.Modelo.*;
import com.cristhian.SistemaInventario.Repositorio.AsientoContableRepository;
import com.cristhian.SistemaInventario.Repositorio.CuentaContableRepository;
import com.cristhian.SistemaInventario.Repositorio.MovimientoContableRepository;
import com.cristhian.SistemaInventario.Repositorio.UsuarioRepository;
import com.cristhian.SistemaInventario.Security.UsuarioAutenticadoProvider;
import jakarta.persistence.Entity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

@Service
@Transactional
public class AperturaContableService {

    // Repositorio para la gestión de asientos contables
    private final AsientoContableRepository asientoRepo;

    // Repositorio para los movimientos contables (DEBE / HABER)
    private final MovimientoContableRepository movimientoRepo;

    // Repositorio de cuentas contables
    private final CuentaContableRepository cuentaRepo;

    // Proveedor del usuario autenticado desde el contexto de seguridad
    private final UsuarioAutenticadoProvider usuarioProvider;

    // Repositorio de usuarios para validación en base de datos
    private final UsuarioRepository usuarioRepository;

    public AperturaContableService(
            AsientoContableRepository asientoRepo,
            MovimientoContableRepository movimientoRepo,
            CuentaContableRepository cuentaRepo,
            UsuarioAutenticadoProvider usuarioProvider,
            UsuarioRepository usuarioRepository
    ) {
        this.asientoRepo = asientoRepo;
        this.movimientoRepo = movimientoRepo;
        this.cuentaRepo = cuentaRepo;
        this.usuarioProvider = usuarioProvider;
        this.usuarioRepository = usuarioRepository;
    }

    /**
     * Crea el asiento contable de apertura del sistema.
     *
     * Reglas de negocio:
     * - Solo puede existir un único asiento de tipo APERTURA
     * - La cuenta seleccionada debe ser de tipo ACTIVO
     * - El monto de apertura se contrapartida contra la cuenta principal de PATRIMONIO
     *
     * @param dto datos necesarios para la apertura contable
     */
    public void crearAsientoApertura(AperturaCuentaDTO dto) {

        // Obtiene el usuario autenticado desde el contexto de seguridad
        Usuario usuarioAuth = usuarioProvider.obtenerUsuarioAutenticado();

        // Valida que el usuario autenticado exista en la base de datos
        Usuario usuario = usuarioRepository.findById(usuarioAuth.getIdUsuario())
                .orElseThrow(() -> new RuntimeException("Usuario no existe en BD"));

        // Validaciones defensivas para asegurar integridad del contexto
        if (usuario == null) {
            throw new RuntimeException("USUARIO AUTENTICADO ES NULL");
        }

        if (usuario.getIdUsuario() == null) {
            throw new RuntimeException("ID USUARIO ES NULL");
        }

        System.out.println("USUARIO OK → ID: " + usuario.getIdUsuario());

        // Validación de unicidad: solo puede existir un asiento contable de apertura
        if (asientoRepo.existsByTipo(TipoAsiento.APERTURA)) {
            throw new AperturaYaExisteException("Ya existe un asiento de apertura");
        }

        // Obtiene y valida la cuenta contable seleccionada para la apertura
        CuentaContable cuenta = cuentaRepo.findById(dto.getIdCuenta())
                .orElseThrow(() -> new AperturaNoExisteException("La cuenta no existe"));

        // La cuenta de apertura debe ser obligatoriamente de tipo ACTIVO
        if (cuenta.getTipo() != TipoCuenta.ACTIVO) {
            throw new AperturaNoExisteException("La cuenta de apertura debe ser ACTIVO");
        }

        // Creación del asiento contable de apertura
        AsientoContable asiento = new AsientoContable();
        asiento.setFecha(dto.getFecha().atStartOfDay());
        asiento.setDescripcion(dto.getDescripcion());
        asiento.setTipo(TipoAsiento.APERTURA);
        asiento.setUsuario(usuario);
        asientoRepo.save(asiento);

        // Registro del movimiento DEBE: cuenta de ACTIVO
        MovimientoContable debe = new MovimientoContable();
        debe.setAsiento(asiento);
        debe.setCuenta(cuenta);
        debe.setDebe(dto.getMonto());
        debe.setHaber(BigDecimal.ZERO);
        movimientoRepo.save(debe);

        // Obtiene la cuenta principal de PATRIMONIO
        CuentaContable patrimonio = cuentaRepo.findCuentaPatrimonioPrincipal();

        // Registro del movimiento HABER: cuenta de PATRIMONIO
        MovimientoContable haber = new MovimientoContable();
        haber.setAsiento(asiento);
        haber.setCuenta(patrimonio);
        haber.setDebe(BigDecimal.ZERO);
        haber.setHaber(dto.getMonto());
        movimientoRepo.save(haber);
    }
}
