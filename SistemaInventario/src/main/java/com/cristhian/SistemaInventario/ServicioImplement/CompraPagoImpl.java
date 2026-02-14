package com.cristhian.SistemaInventario.ServicioImplement;

import com.cristhian.SistemaInventario.DTO.CompraPagoDTO;
import com.cristhian.SistemaInventario.Enums.EstadoCompra;
import com.cristhian.SistemaInventario.Enums.EstadoPago;
import com.cristhian.SistemaInventario.Enums.TipoAsiento;
import com.cristhian.SistemaInventario.Excepciones.RecursoNoEncontradoException;
import com.cristhian.SistemaInventario.Excepciones.ValidacionException;
import com.cristhian.SistemaInventario.Modelo.*;
import com.cristhian.SistemaInventario.Repositorio.AsientoContableRepository;
import com.cristhian.SistemaInventario.Repositorio.CompraPagoRepository;
import com.cristhian.SistemaInventario.Repositorio.CompraRepository;
import com.cristhian.SistemaInventario.Repositorio.FormaPagoRepository;
import com.cristhian.SistemaInventario.Service.ICompraPagoService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
/**
 * Servicio encargado de gestionar los pagos de compras,
 * su impacto contable y la actualización del estado de la compra.
 */
@Service
@Transactional
public class CompraPagoImpl implements ICompraPagoService {

    /*private final CompraPagoRepository compraPagoRepository;
    private final CompraRepository compraRepository;
    private final FormaPagoRepository formaPagoRepository;

    private final CompraContableServiceImpl compraContableService;
    private final AsientoContableRepository asientoContableRepository;
    private final ValidacionContableService validacionContableService;

    public CompraPagoImpl(CompraPagoRepository compraPagoRepository, CompraRepository compraRepository,
                          FormaPagoRepository formaPagoRepository,
                          CompraContableServiceImpl compraContableService,
                          AsientoContableRepository asientoContableRepository,
                          ValidacionContableService validacionContableService) {
        this.compraPagoRepository = compraPagoRepository;
        this.compraRepository = compraRepository;
        this.formaPagoRepository = formaPagoRepository;
        this.compraContableService = compraContableService;
        this.asientoContableRepository = asientoContableRepository;
        this.validacionContableService = validacionContableService;
    }

    @Override
    public void registrarPago(int id, CompraPagoDTO pagoDTO) {

        // llamamos al metodo validar apertura para veriifcar que exista primero el asiento de apertura
        validacionContableService.validarApertura(TipoAsiento.NORMAL);

            Compra compra = compraRepository.findById(id)
                    .orElseThrow(() -> new RecursoNoEncontradoException("Compra no encontrada"));

            if (compra.getEstado() == EstadoCompra.PAGADA) {
                throw new ValidacionException("La Compra ya está pagada");
            }

            if (pagoDTO.getMontoCompra().compareTo(BigDecimal.ZERO) <= 0) {
                throw new ValidacionException("El monto debe ser mayor a cero");
            }

            BigDecimal totalPagado = compra.getCompraPagos().stream()
                    .filter(p -> p.getEstadoPago() == EstadoPago.CONFIRMADO)
                    .map(CompraPago::getMontoCompra)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            BigDecimal saldoPendiente = compra.getTotalCompra().subtract(totalPagado);

            if (pagoDTO.getMontoCompra().compareTo(saldoPendiente) > 0) {
                throw new ValidacionException("El pago supera el saldo pendiente");
            }


            Integer numeroCuotas = pagoDTO.getNumeroCuotas();

            // VALIDACIÓN CLAVE
            if (numeroCuotas == null || numeroCuotas < 1) {
                numeroCuotas = 1; // pago de contado
            }

            BigDecimal valorCuota = pagoDTO.getMontoCompra()
                    .divide(BigDecimal.valueOf(numeroCuotas), RoundingMode.HALF_UP);

            CompraPago pago = new CompraPago();
            pago.setCompra(compra);
            pago.setMontoCompra(pagoDTO.getMontoCompra());
            pago.setFechaPago(LocalDateTime.now());
            pago.setNumeroCuotas(numeroCuotas);

            // 🔐 El backend decide el estado inicial
            /*pago.setEstadoPago(
                    pagoDTO.getEstadoPago() != null
                            ? pagoDTO.getEstadoPago()
                            : EstadoPago.PENDIENTE_CONFIRMACION
            );*/

            /*pago.setEstadoPago(EstadoPago.PENDIENTE_CONFIRMACION);

            if (pagoDTO.getIdFormaPago() != null) {
                FormaPago formaPago = formaPagoRepository
                        .findById(pagoDTO.getIdFormaPago())
                        .orElseThrow(() -> new RecursoNoEncontradoException("Forma de pago no encontrada"));

                pago.setFormaPago(formaPago);
            }

           compraPagoRepository.save(pago);

            //Registramos el pago de la compra en el asientopagocompra
            compraContableService.registrarAsientoPagoCompra(compra, pago);

            // MUY IMPORTANTE
            compra.getCompraPagos().add(pago);

            recalcularEstadoCompra(compra);

    }

    @Override
    public void confirmarPago(int id) {
        CompraPago pago = compraPagoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Pago no encontrado"));

        if (pago.getEstadoPago() != EstadoPago.PENDIENTE_CONFIRMACION) {
            throw new IllegalStateException("El pago no está pendiente de confirmación");
        }

        pago.setEstadoPago(EstadoPago.CONFIRMADO);
        compraPagoRepository.save(pago);

        recalcularEstadoCompra(pago.getCompra());
    }

    @Override
    @Transactional
    public void rechazarPago(int id) {

        CompraPago pago = compraPagoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Pago no encontrado"));

        if (pago.getEstadoPago() == EstadoPago.RECHAZADO) {
            throw new IllegalStateException("El pago ya fue rechazado");
        }

        // 1️⃣ Cambiar estado
        pago.setEstadoPago(EstadoPago.RECHAZADO);
        compraPagoRepository.save(pago);

        // 2️⃣ Revertir asiento contable del pago
        AsientoContable asientoPago = asientoContableRepository
                .findByPago(pago)
                .orElseThrow(() ->
                        new RecursoNoEncontradoException("No existe asiento contable para este pago")
                );

        compraContableService.reversarAsientoPago(asientoPago);

        // 3️⃣ Recalcular estado de la compra
        recalcularEstadoCompra(pago.getCompra());
    }


    @Override
    public void recalcularEstadoCompra(Compra compra) {
        BigDecimal totalPagado = compra.getCompraPagos().stream()
                .filter(p -> p.getEstadoPago() == EstadoPago.CONFIRMADO)
                .map(CompraPago::getMontoCompra)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalVenta = compra.getTotalCompra();

        if (totalPagado.compareTo(BigDecimal.ZERO) == 0) {
            compra.setEstado(EstadoCompra.PENDIENTE);
        }
        else if (totalPagado.compareTo(totalVenta) < 0) {
            compra.setEstado(EstadoCompra.PARCIAL);
        }
        else {
            compra.setEstado(EstadoCompra.PAGADA);
        }

        compraRepository.save(compra);

    }

    @Override
    public List<CompraPagoDTO> listarPorCompra(Long id) {
        return compraPagoRepository.findByCompraIdCompra(id)
                .stream()
                .map(CompraPagoDTO::new)
                .toList();
    }*/

    // Repositorio de pagos de compra
    private final CompraPagoRepository compraPagoRepository;

    // Repositorio de compras
    private final CompraRepository compraRepository;

    // Repositorio de formas de pago
    private final FormaPagoRepository formaPagoRepository;

    // Servicio contable asociado a compras
    private final CompraContableServiceImpl compraContableService;

    // Repositorio de asientos contables
    private final AsientoContableRepository asientoContableRepository;

    // Servicio de validaciones contables generales
    private final ValidacionContableService validacionContableService;


    /**
     * Constructor con inyección de dependencias
     */
    public CompraPagoImpl(CompraPagoRepository compraPagoRepository,
                          CompraRepository compraRepository,
                          FormaPagoRepository formaPagoRepository,
                          CompraContableServiceImpl compraContableService,
                          AsientoContableRepository asientoContableRepository,
                          ValidacionContableService validacionContableService) {

        this.compraPagoRepository = compraPagoRepository;
        this.compraRepository = compraRepository;
        this.formaPagoRepository = formaPagoRepository;
        this.compraContableService = compraContableService;
        this.asientoContableRepository = asientoContableRepository;
        this.validacionContableService = validacionContableService;
    }

    /**
     * Registra un pago para una compra específica.
     * Valida reglas de negocio y genera el asiento contable correspondiente.
     */
    @Override
    public void registrarPago(int id, CompraPagoDTO pagoDTO) {

        // Validación: debe existir asiento de apertura contable
        validacionContableService.validarApertura(TipoAsiento.NORMAL);

        // Obtención de la compra
        Compra compra = compraRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Compra no encontrada"));

        // Validación: la compra no debe estar totalmente pagada
        if (compra.getEstado() == EstadoCompra.PAGADA) {
            throw new ValidacionException("La Compra ya está pagada");
        }

        // Validación: monto mayor a cero
        if (pagoDTO.getMontoCompra().compareTo(BigDecimal.ZERO) <= 0) {
            throw new ValidacionException("El monto debe ser mayor a cero");
        }

        // Cálculo del total ya pagado (solo pagos confirmados)
        BigDecimal totalPagado = compra.getCompraPagos().stream()
                .filter(p -> p.getEstadoPago() == EstadoPago.CONFIRMADO)
                .map(CompraPago::getMontoCompra)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Cálculo del saldo pendiente
        /*BigDecimal saldoPendiente = compra.getTotalCompra().subtract(totalPagado);

        // Validación: el pago no puede superar el saldo pendiente
        if (pagoDTO.getMontoCompra().compareTo(saldoPendiente) > 0) {
            throw new ValidacionException("El pago supera el saldo pendiente");
        }*/

        // Número de cuotas
        Integer numeroCuotas = pagoDTO.getNumeroCuotas();

        // Validación: si no se especifican cuotas, se asume pago de contado
        if (numeroCuotas == null || numeroCuotas < 1) {
            numeroCuotas = 1;
        }

        // Cálculo del valor de cada cuota
        BigDecimal valorCuota = pagoDTO.getMontoCompra()
                .divide(BigDecimal.valueOf(numeroCuotas), RoundingMode.HALF_UP);

        // Creación de la entidad de pago
        CompraPago pago = new CompraPago();
        pago.setCompra(compra);
        pago.setMontoCompra(pagoDTO.getMontoCompra());
        pago.setFechaPago(LocalDateTime.now());
        pago.setNumeroCuotas(numeroCuotas);

        // El backend define el estado inicial del pago
        pago.setEstadoPago(EstadoPago.PENDIENTE_CONFIRMACION);

        // Asociación de la forma de pago si fue enviada
        if (pagoDTO.getIdFormaPago() != null) {
            FormaPago formaPago = formaPagoRepository
                    .findById(pagoDTO.getIdFormaPago())
                    .orElseThrow(() -> new RecursoNoEncontradoException("Forma de pago no encontrada"));

            pago.setFormaPago(formaPago);
        }

        // Persistencia del pago
        compraPagoRepository.save(pago);

        // Asociación del pago a la compra
        compra.getCompraPagos().add(pago);

        // Recalcular el estado de la compra
        recalcularEstadoCompra(compra);
    }

    /**
     * Confirma un pago previamente registrado.
     */
    @Override
    public void confirmarPago(int id) {

        CompraPago pago = compraPagoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Pago no encontrado"));

        // Validación: solo pagos pendientes pueden confirmarse
        if (pago.getEstadoPago() != EstadoPago.PENDIENTE_CONFIRMACION) {
            throw new IllegalStateException("El pago no está pendiente de confirmación");
        }

        // Actualización de estado
        pago.setEstadoPago(EstadoPago.CONFIRMADO);
        compraPagoRepository.save(pago);

        // Registro del asiento contable del pago
        compraContableService.registrarAsientoPagoCompra(pago.getCompra(), pago );

        // Recalcular estado de la compra
        recalcularEstadoCompra(pago.getCompra());
    }

    /**
     * Rechaza un pago y revierte su asiento contable.
     */
    @Override
    @Transactional
    public void rechazarPago(int id) {

        CompraPago pago = compraPagoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Pago no encontrado"));

        // Validación: no puede rechazarse dos veces
        if (pago.getEstadoPago() == EstadoPago.RECHAZADO) {
            throw new IllegalStateException("El pago ya fue rechazado");
        }

        // 1️⃣ Cambiar estado del pago
        pago.setEstadoPago(EstadoPago.RECHAZADO);
        compraPagoRepository.save(pago);

        // 2️⃣ Obtener el asiento contable asociado al pago
        AsientoContable asientoPago = asientoContableRepository
                .findByPago(pago)
                .orElseThrow(() ->
                        new RecursoNoEncontradoException("No existe asiento contable para este pago")
                );

        // Reversión del asiento contable
        compraContableService.reversarAsientoPago(asientoPago);

        // 3️⃣ Recalcular estado de la compra
        recalcularEstadoCompra(pago.getCompra());
    }

    /**
     * Recalcula el estado de la compra según los pagos confirmados.
     */
    @Override
    public void recalcularEstadoCompra(Compra compra) {

        BigDecimal totalPagado = compra.getCompraPagos().stream()
                .filter(p -> p.getEstadoPago() == EstadoPago.CONFIRMADO)
                .map(CompraPago::getMontoCompra)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalCompra = compra.getTotalCompra();

        // Determinación del estado de la compra
        if (totalPagado.compareTo(BigDecimal.ZERO) == 0) {
            compra.setEstado(EstadoCompra.PENDIENTE);
        }
        else if (totalPagado.compareTo(totalCompra) < 0) {
            compra.setEstado(EstadoCompra.PARCIAL);
        }
        else {
            compra.setEstado(EstadoCompra.PAGADA);
        }

        // Persistencia del nuevo estado
        compraRepository.save(compra);
    }

    /**
     * Lista los pagos asociados a una compra específica.
     */
    @Override
    public List<CompraPagoDTO> listarPorCompra(Long id) {
        return compraPagoRepository.findByCompraIdCompra(id)
                .stream()
                .map(CompraPagoDTO::new)
                .toList();
    }
}