package com.cristhian.SistemaInventario.ServicioImplement;

import com.cristhian.SistemaInventario.DTO.AsientoContableDTO;
import com.cristhian.SistemaInventario.DTO.CompraDTO;
import com.cristhian.SistemaInventario.DTO.MovimientoContableDTO;
import com.cristhian.SistemaInventario.Enums.EstadoCompra;
import com.cristhian.SistemaInventario.Enums.NombreFormaPago;
import com.cristhian.SistemaInventario.Modelo.*;
import com.cristhian.SistemaInventario.Repositorio.AsientoContableRepository;
import com.cristhian.SistemaInventario.Repositorio.CuentaContableRepository;
import com.cristhian.SistemaInventario.Repositorio.UsuarioRepository;
import com.cristhian.SistemaInventario.Security.UsuarioAutenticadoProvider;
import com.cristhian.SistemaInventario.Service.IAsientoContableService;
import com.cristhian.SistemaInventario.Service.ICompraContableService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
/**
 * Servicio encargado de gestionar los asientos contables
 * relacionados con las compras, pagos y reversos.
 */
@Service
@Transactional
public class CompraContableServiceImpl {

    /*private final CuentaContableRepository cuentaContableRepository;
    private final IAsientoContableService asientoContableService;

    public CompraContableServiceImpl(CuentaContableRepository cuentaContableRepository,
                                     IAsientoContableService asientoContableService
                                     ) {

        this.cuentaContableRepository = cuentaContableRepository;
        this.asientoContableService = asientoContableService;
    }

    // 🔹 Obtener cuentas
    private CuentaContable cuentaInventarios() {
        return cuentaContableRepository.findByCodigo("1435")
                .orElseThrow(() -> new RuntimeException("Cuenta Inventarios no existe"));
    }

    private CuentaContable cuentaIvaDescontable() {
        return cuentaContableRepository.findByCodigo("2408")
                .orElseThrow(() -> new RuntimeException("Cuenta IVA no existe"));
    }

    private CuentaContable cuentaProveedores() {
        return cuentaContableRepository.findByCodigo("2205")
                .orElseThrow(() -> new RuntimeException("Cuenta Proveedores no existe"));
    }

    private CuentaContable cuentaCaja() {
        return cuentaContableRepository.findByCodigo("1105")
                .orElseThrow(() -> new RuntimeException("Cuenta Caja no existe"));
    }

    // 🔹 Determinar si la compra es crédito
    private boolean esCompraCredito(Compra compra) {
        return compra.getEstado() == EstadoCompra.PENDIENTE;
    }

    // 🔹 Registrar asiento
    @Transactional
    public void registrarAsientoCompra(Compra compra, boolean esCredito) {

        AsientoContableDTO dto = new AsientoContableDTO();
        dto.setFecha(compra.getFechaCompra());
        dto.setDescripcion("Compra #" + compra.getIdCompra());

        List<MovimientoContableDTO> movimientos = new ArrayList<>();

        // DEBE Inventarios
        movimientos.add(new MovimientoContableDTO(
                "1435",
                compra.getSubTotalCompra(),
                BigDecimal.ZERO
        ));

        // DEBE IVA
        if (compra.getTotalImpuestos().compareTo(BigDecimal.ZERO) > 0) {
            movimientos.add(new MovimientoContableDTO(
                    "2408",
                    compra.getTotalImpuestos(),
                    BigDecimal.ZERO
            ));
        }

        // HABER Proveedor o Caja
        movimientos.add(new MovimientoContableDTO(
                esCredito ? "2205" : "1105",
                BigDecimal.ZERO,
                compra.getTotalCompra()
        ));

        dto.setMovimientos(movimientos);

        // 🔥 AQUÍ recién se registra el asiento
        asientoContableService.registrarAsiento(dto, compra, null);
    }


    public void registrarAsientoPagoCompra(Compra compra, CompraPago pago) {

        AsientoContableDTO dto = new AsientoContableDTO();
        dto.setFecha(pago.getFechaPago());
        dto.setDescripcion("Pago compra #" + compra.getIdCompra());

        dto.setMovimientos(List.of(
                new MovimientoContableDTO("2205", pago.getMontoCompra(), BigDecimal.ZERO),
                new MovimientoContableDTO("1105", BigDecimal.ZERO, pago.getMontoCompra())
        ));

        asientoContableService.registrarAsiento(dto, compra, pago);
    }

    public void reversarAsientoCompra(AsientoContable asientoOriginal) {

        AsientoContableDTO dto = new AsientoContableDTO();
        dto.setFecha(LocalDateTime.now());
        dto.setDescripcion("Reversión de " + asientoOriginal.getDescripcion());

        List<MovimientoContableDTO> movimientos = new ArrayList<>();

        for (MovimientoContable mov : asientoOriginal.getMovimientos()) {
            movimientos.add(new MovimientoContableDTO(
                    mov.getCuenta().getCodigo(),
                    mov.getHaber(), // invertido
                    mov.getDebe()
            ));
        }

        dto.setMovimientos(movimientos);

        asientoContableService.registrarAsiento(dto, asientoOriginal.getCompra(), null);
    }

    public void reversarAsientoPago(AsientoContable asientoPago) {

        if (asientoPago == null || asientoPago.getMovimientos().isEmpty()) {
            throw new IllegalStateException("Asiento de pago inválido para reversar");
        }

        AsientoContableDTO dto = new AsientoContableDTO();
        dto.setFecha(LocalDateTime.now());
        dto.setDescripcion("Reversión pago - " + asientoPago.getDescripcion());

        List<MovimientoContableDTO> movimientos = new ArrayList<>();

        for (MovimientoContable mov : asientoPago.getMovimientos()) {

            movimientos.add(new MovimientoContableDTO(
                    mov.getCuenta().getCodigo(),
                    mov.getHaber(), // 🔁 Invertimos
                    mov.getDebe()
            ));
        }

        dto.setMovimientos(movimientos);

        // 🔥 ÚNICO punto donde se guarda el asiento
        asientoContableService.registrarAsiento(dto, asientoPago.getCompra(),asientoPago.getPago());
    }

    //Validamos que La suma del DEBE debe ser igual a la suma del HABER
    private void validarAsientoBalanceado(List<MovimientoContable> movimientos) {

        BigDecimal totalDebe = BigDecimal.ZERO;
        BigDecimal totalHaber = BigDecimal.ZERO;

        for (MovimientoContable mov : movimientos) {

            if (mov.getDebe() != null) {
                totalDebe = totalDebe.add(mov.getDebe());
            }

            if (mov.getHaber() != null) {
                totalHaber = totalHaber.add(mov.getHaber());
            }
        }

        if (totalDebe.compareTo(totalHaber) != 0) {
            throw new IllegalStateException(
                    "Asiento descuadrado. Debe: " + totalDebe + " Haber: " + totalHaber
            );
        }
    }*/

    // Repositorio para consultar las cuentas contables
    private final CuentaContableRepository cuentaContableRepository;

    // Servicio responsable de registrar los asientos contables
    private final IAsientoContableService asientoContableService;

    /**
     * Constructor con inyección de dependencias
     */
    public CompraContableServiceImpl(CuentaContableRepository cuentaContableRepository,
                                     IAsientoContableService asientoContableService) {

        this.cuentaContableRepository = cuentaContableRepository;
        this.asientoContableService = asientoContableService;
    }

    /* ======================================================
       MÉTODOS PRIVADOS PARA OBTENER CUENTAS CONTABLES
       ====================================================== */

    /**
     * Obtiene la cuenta contable de Inventarios
     */
    private CuentaContable cuentaInventarios() {
        return cuentaContableRepository.findByCodigo("1435")
                .orElseThrow(() -> new RuntimeException("Cuenta Inventarios no existe"));
    }

    /**
     * Obtiene la cuenta contable de IVA descontable
     */
    private CuentaContable cuentaIvaDescontable() {
        return cuentaContableRepository.findByCodigo("2408")
                .orElseThrow(() -> new RuntimeException("Cuenta IVA no existe"));
    }

    /**
     * Obtiene la cuenta contable de Proveedores
     */
    private CuentaContable cuentaProveedores() {
        return cuentaContableRepository.findByCodigo("2205")
                .orElseThrow(() -> new RuntimeException("Cuenta Proveedores no existe"));
    }

    /**
     * Obtiene la cuenta contable de Caja
     */
    private CuentaContable cuentaCaja() {
        return cuentaContableRepository.findByCodigo("1105")
                .orElseThrow(() -> new RuntimeException("Cuenta Caja no existe"));
    }

    /* ======================================================
       VALIDACIONES DE NEGOCIO
       ====================================================== */

    /**
     * Determina si una compra es a crédito según su estado
     */
    private boolean esCompraCredito(Compra compra) {
        return compra.getEstado() == EstadoCompra.PENDIENTE;
    }

    /* ======================================================
       REGISTRO DE ASIENTOS CONTABLES
       ====================================================== */

    /**
     * Registra el asiento contable de una compra,
     * ya sea a crédito o de contado.
     */
    @Transactional
    public void registrarAsientoCompra(Compra compra) {

        // Creación del DTO del asiento
        AsientoContableDTO dto = new AsientoContableDTO();
        dto.setFecha(compra.getFechaCompra());
        dto.setDescripcion("Compra # " + compra.getIdCompra());

        // Lista de movimientos contables
        List<MovimientoContableDTO> movimientos = new ArrayList<>();

        // DEBE: Inventarios por el subtotal de la compra
        movimientos.add(new MovimientoContableDTO(
                "1435",
                compra.getSubTotalCompra(),
                BigDecimal.ZERO
        ));

        // DEBE: IVA descontable si existe impuesto
        if (compra.getTotalImpuestos().compareTo(BigDecimal.ZERO) > 0) {
            movimientos.add(new MovimientoContableDTO(
                    "2408",
                    compra.getTotalImpuestos(),
                    BigDecimal.ZERO
            ));
        }

        // HABER: Proveedores (crédito) o Caja (contado)
        movimientos.add(new MovimientoContableDTO(
                 "2205",
                BigDecimal.ZERO,
                compra.getTotalCompra()
        ));

        // Asignación de movimientos al asiento
        dto.setMovimientos(movimientos);

        // Registro definitivo del asiento contable
        asientoContableService.registrarAsiento(dto, compra, null, null, null);
    }

    /**
     * Registra el asiento contable correspondiente
     * al pago de una compra a crédito.
     */
    public void registrarAsientoPagoCompra(Compra compra, CompraPago pago) {

        AsientoContableDTO dto = new AsientoContableDTO();
        dto.setFecha(pago.getFechaPago());
        dto.setDescripcion("Pago referencia a la compra # " + compra.getIdCompra());

        String cuentaDestino;

        if(pago.getFormaPago().getNombreFormaPago() == NombreFormaPago.EFECTIVO){
            cuentaDestino = "1105";
        }else{
            cuentaDestino = "1110";
        }

        // Movimiento contable del pago
        dto.setMovimientos(List.of(

                new MovimientoContableDTO("2205", pago.getMontoCompra(), BigDecimal.ZERO),
                new MovimientoContableDTO(cuentaDestino, BigDecimal.ZERO, pago.getMontoCompra())
        ));

        // Registro del asiento de pago
        asientoContableService.registrarAsiento(dto, compra, pago, null, null);
    }

    /**
     * Genera un asiento contable de reversión
     * a partir de un asiento original.
     */
    public void reversarAsientoCompra(AsientoContable asientoOriginal) {

        AsientoContableDTO dto = new AsientoContableDTO();
        dto.setFecha(LocalDateTime.now());
        dto.setDescripcion("Reversión de " + asientoOriginal.getDescripcion());

        List<MovimientoContableDTO> movimientos = new ArrayList<>();

        // Inversión de los valores DEBE y HABER
        for (MovimientoContable mov : asientoOriginal.getMovimientos()) {
            movimientos.add(new MovimientoContableDTO(
                    mov.getCuenta().getCodigo(),
                    mov.getHaber(),
                    mov.getDebe()
            ));
        }

        dto.setMovimientos(movimientos);

        // Registro del asiento de reversión
        asientoContableService.registrarAsiento(dto, asientoOriginal.getCompra(), null,null,null);
    }

    /**
     * Revierte un asiento contable correspondiente
     * a un pago de compra.
     */
    public void reversarAsientoPago(AsientoContable asientoPago) {

        // Validación básica del asiento
        if (asientoPago == null || asientoPago.getMovimientos().isEmpty()) {
            throw new IllegalStateException("Asiento de pago inválido para reversar");
        }

        AsientoContableDTO dto = new AsientoContableDTO();
        dto.setFecha(LocalDateTime.now());
        dto.setDescripcion("Reversión pago - " + asientoPago.getDescripcion());

        List<MovimientoContableDTO> movimientos = new ArrayList<>();

        // Inversión de cada movimiento contable
        for (MovimientoContable mov : asientoPago.getMovimientos()) {
            movimientos.add(new MovimientoContableDTO(
                    mov.getCuenta().getCodigo(),
                    mov.getHaber(),
                    mov.getDebe()
            ));
        }

        dto.setMovimientos(movimientos);

        // Registro del asiento de reversión del pago
        asientoContableService.registrarAsiento(
                dto,
                asientoPago.getCompra(),
                asientoPago.getPago(),
                null,
                null
        );
    }

}
