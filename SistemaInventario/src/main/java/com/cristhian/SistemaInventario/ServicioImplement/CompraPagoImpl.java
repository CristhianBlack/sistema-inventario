package com.cristhian.SistemaInventario.ServicioImplement;

import com.cristhian.SistemaInventario.DTO.CompraPagoDTO;
import com.cristhian.SistemaInventario.DTO.VentaPagoDTO;
import com.cristhian.SistemaInventario.Excepciones.RecursoNoEncontradoException;
import com.cristhian.SistemaInventario.Modelo.*;
import com.cristhian.SistemaInventario.Repositorio.CompraPagoRepository;
import com.cristhian.SistemaInventario.Repositorio.CompraRepository;
import com.cristhian.SistemaInventario.Repositorio.FormaPagoRepository;
import com.cristhian.SistemaInventario.Service.ICompraPagoService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class CompraPagoImpl implements ICompraPagoService {

    private final CompraPagoRepository compraPagoRepository;
    private final CompraRepository compraRepository;
    private final FormaPagoRepository formaPagoRepository;

    public CompraPagoImpl(CompraPagoRepository compraPagoRepository, CompraRepository compraRepository,
                          FormaPagoRepository formaPagoRepository) {
        this.compraPagoRepository = compraPagoRepository;
        this.compraRepository = compraRepository;
        this.formaPagoRepository = formaPagoRepository;
    }

    @Override
    public void registrarPago(int id, CompraPagoDTO pagoDTO) {

            Compra compra = compraRepository.findById(id)
                    .orElseThrow(() -> new RecursoNoEncontradoException("Compra no encontrada"));

            if (compra.getEstado() == EstadoCompra.PAGADA) {
                throw new IllegalStateException("La Compra ya está pagada");
            }

            if (pagoDTO.getMontoCompra().compareTo(BigDecimal.ZERO) <= 0) {
                throw new IllegalArgumentException("El monto debe ser mayor a cero");
            }

            BigDecimal totalPagado = compra.getCompraPagos().stream()
                    .filter(p -> p.getEstadoPago() == EstadoPago.CONFIRMADO)
                    .map(CompraPago::getMontoCompra)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            BigDecimal saldoPendiente = compra.getTotalCompra().subtract(totalPagado);

            if (pagoDTO.getMontoCompra().compareTo(saldoPendiente) > 0) {
                throw new IllegalArgumentException("El pago supera el saldo pendiente");
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

            pago.setEstadoPago(EstadoPago.PENDIENTE_CONFIRMACION);

            if (pagoDTO.getIdFormaPago() != null) {
                FormaPago formaPago = formaPagoRepository
                        .findById(pagoDTO.getIdFormaPago())
                        .orElseThrow(() -> new RuntimeException("Forma de pago no encontrada"));

                pago.setFormaPago(formaPago);
            }

            compraPagoRepository.save(pago);

            // MUY IMPORTANTE
            compra.getCompraPagos().add(pago);

            recalcularEstadoVenta(compra);

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

        recalcularEstadoVenta(pago.getCompra());
    }

    @Override
    public void rechazarPago(int id) {
        CompraPago pago = compraPagoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Pago no encontrado"));

        pago.setEstadoPago(EstadoPago.RECHAZADO);
        compraPagoRepository.save(pago);

        recalcularEstadoVenta(pago.getCompra());
    }

    @Override
    public void recalcularEstadoVenta(Compra compra) {
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
    public List<CompraPagoDTO> listarPorVenta(Long id) {
        return compraPagoRepository.findByCompraIdCompra(id)
                .stream()
                .map(CompraPagoDTO::new)
                .toList();
    }
}
