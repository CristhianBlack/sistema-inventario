package com.cristhian.SistemaInventario.ServicioImplement;

import com.cristhian.SistemaInventario.DTO.AsientoContableDTO;
import com.cristhian.SistemaInventario.DTO.MovimientoContableDTO;
import com.cristhian.SistemaInventario.Enums.EstadoCompra;
import com.cristhian.SistemaInventario.Enums.EstadoVenta;
import com.cristhian.SistemaInventario.Modelo.*;
import com.cristhian.SistemaInventario.Repositorio.CuentaContableRepository;
import com.cristhian.SistemaInventario.Service.IAsientoContableService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
@Service
@Transactional
public class VentaContableServiceImpl {

    // Repositorio para consultar las cuentas contables
    private final CuentaContableRepository cuentaContableRepository;

    // Servicio responsable de registrar los asientos contables
    private final IAsientoContableService asientoContableService;

    /**
     * Constructor con inyección de dependencias
     */
    public VentaContableServiceImpl(CuentaContableRepository cuentaContableRepository,
                                     IAsientoContableService asientoContableService) {

        this.cuentaContableRepository = cuentaContableRepository;
        this.asientoContableService = asientoContableService;
    }

    /* ======================================================
       MÉTODOS PRIVADOS PARA OBTENER CUENTAS CONTABLES
       ====================================================== */

    /**
     * Obtiene la cuenta contable de Clientes
     */
    private CuentaContable cuentaClientes() {
        return cuentaContableRepository.findByCodigo("1305")
                .orElseThrow(() -> new RuntimeException("Cuenta Clientes no existe"));
    }

    /**
     * Obtiene la cuenta contable de Ingresos
     */
    private CuentaContable cuentaIngresos() {
        return cuentaContableRepository.findByCodigo("4135")
                .orElseThrow(() -> new RuntimeException("Cuenta Ingresos no existe"));
    }

    /**
     * Obtiene la cuenta contable de Iva generado
     */
    private CuentaContable cuentaIvaGenerado() {
        return cuentaContableRepository.findByCodigo("2408")
                .orElseThrow(() -> new RuntimeException("Cuenta Iva generado no existe"));
    }


    /* ======================================================
       VALIDACIONES DE NEGOCIO
       ====================================================== */

    /**
     * Determina si una compra es a crédito según su estado
     */
    private boolean esVentaCredito(Venta venta) {
        return venta.getEstado() == EstadoVenta.PENDIENTE;
    }

    /* ======================================================
       REGISTRO DE ASIENTOS CONTABLES
       ====================================================== */

    /**
     * Registra el asiento contable de una venta,
     * ya sea a crédito o de contado.
     */
    @Transactional
    public void registrarAsientoVenta(Venta venta) {

        BigDecimal total = venta.getTotalVenta();
        BigDecimal iva = venta.getTotalImpuestos();
        BigDecimal base = total.subtract(iva);

        // Creación del DTO del asiento
        AsientoContableDTO dto = new AsientoContableDTO();
        dto.setFecha(venta.getFechaVenta());
        dto.setDescripcion("Venta #" + venta.getIdVenta());

        // Lista de movimientos contables
        List<MovimientoContableDTO> movimientos = new ArrayList<>();

        // DEBE → Clientes
        movimientos.add(new MovimientoContableDTO(
                "1305",
                total,
                BigDecimal.ZERO
        ));

        // HABER → Ingresos (por el total)
        movimientos.add(new MovimientoContableDTO(
                "4135",
                BigDecimal.ZERO,
                base
        ));

        // HABER → IVA generado (si existe)
        if (venta.getTotalImpuestos().compareTo(BigDecimal.ZERO) > 0) {
            movimientos.add(new MovimientoContableDTO(
                    "240805",
                    BigDecimal.ZERO,
                    iva
            ));
        }
        // Asignación de movimientos al asiento
        dto.setMovimientos(movimientos);

        // Registro definitivo del asiento contable
        asientoContableService.registrarAsiento(dto, null, null, venta, null);
    }

    /**
     * Registra el asiento contable correspondiente
     * al pago de una venta a crédito.
     */
    public void registrarAsientoPagoVenta(Venta venta, VentaPago pago) {

        AsientoContableDTO dto = new AsientoContableDTO();
        dto.setFecha(pago.getFechaPago());
        dto.setDescripcion("Pago referencia a la venta #" + venta.getIdVenta());

        String cuentaDestino;

        switch (pago.getFormaPago().getNombreFormaPago()) {
            case EFECTIVO -> cuentaDestino = "1105";
            case TRANSFERENCIA, NEQUI,DAVIPLATA, TARJETA_DEBITO, TARJETA_CREDITO -> cuentaDestino = "1110";
            default -> throw new RuntimeException("Forma de pago no configurada contablemente");
        }
        // Movimiento contable del pago
        dto.setMovimientos(List.of(
                new MovimientoContableDTO(cuentaDestino, pago.getMonto(), BigDecimal.ZERO),
        // Haber Clientes
        new MovimientoContableDTO("1305", BigDecimal.ZERO, pago.getMonto())));




        // Registro del asiento de pago
        asientoContableService.registrarAsiento(dto,null,null, venta, pago);
    }

    /**
     * Genera un asiento contable de reversión
     * a partir de un asiento original.
     */
    public void reversarAsientoVenta(AsientoContable asientoOriginal) {

        AsientoContableDTO dto = new AsientoContableDTO();
        dto.setFecha(LocalDateTime.now());
        dto.setDescripcion("Reversión de venta - " + asientoOriginal.getDescripcion());

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
        asientoContableService.registrarAsiento(dto,null,null, asientoOriginal.getVenta(), null);
    }

    /**
     * Revierte un asiento contable correspondiente
     * a un pago de venta.
     */
    public void reversarAsientoPago(AsientoContable asientoPago) {

        // Validación básica del asiento
        if (asientoPago == null || asientoPago.getMovimientos().isEmpty()) {
            throw new IllegalStateException("Asiento de pago inválido para reversar");
        }

        AsientoContableDTO dto = new AsientoContableDTO();
        dto.setFecha(LocalDateTime.now());
        dto.setDescripcion("Reversión pago venta - " + asientoPago.getDescripcion());

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
                null,
                null,
                asientoPago.getVenta(),
                asientoPago.getVentaPago()
        );
    }

    /* ======================================================
       VALIDACIÓN CONTABLE
       ====================================================== */

    /**
     * Valida que el asiento esté balanceado,
     * es decir, que el total del DEBE sea igual al HABER.
     */
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
    }

    private BigDecimal calcularCostoTotal(Venta venta) {

        BigDecimal costoTotal = BigDecimal.ZERO;

        for (DetalleVenta detalle : venta.getDetalleVentas()) {

            BigDecimal costoUnitario =
                    detalle.getProducto().getCostoPromedio() != null
                            ? detalle.getProducto().getCostoPromedio()
                            : BigDecimal.ZERO;
            BigDecimal cantidad = BigDecimal.valueOf(detalle.getCantidad());

            BigDecimal costoLinea = costoUnitario.multiply(cantidad);

            costoTotal = costoTotal.add(costoLinea);
        }

        return costoTotal;
    }

    public void registrarCostoVenta(Venta venta) {

        BigDecimal costoTotal = venta.getDetalleVentas().stream()
                .map(d -> d.getCostoUnitarioPromedio().multiply(BigDecimal.valueOf(d.getCantidad())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        List<MovimientoContableDTO> movimientos = new ArrayList<>();

        // Debe Costo de Venta
        movimientos.add(new MovimientoContableDTO(
                "6135",
                costoTotal,
                BigDecimal.ZERO
        ));

        // Haber Inventario
        movimientos.add(new MovimientoContableDTO(
                "1435",
                BigDecimal.ZERO,
                costoTotal
        ));

        // DTO
        AsientoContableDTO dto = new AsientoContableDTO();
        dto.setDescripcion("Costo Venta #" + venta.getIdVenta());
        dto.setFecha(LocalDateTime.now());
        dto.setMovimientos(movimientos);

        // Llamar al método real
        asientoContableService.registrarAsiento(                dto,
                null,       // Compra
                null,       // CompraPago
                venta,      // Venta
                null        // VentaPago
        );
    }


}

