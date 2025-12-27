package com.cristhian.SistemaInventario.ServicioImplement;

import com.cristhian.SistemaInventario.DTO.VentaPagoDTO;
import com.cristhian.SistemaInventario.Excepciones.RecursoNoEncontradoException;
import com.cristhian.SistemaInventario.Modelo.*;
import com.cristhian.SistemaInventario.Repositorio.*;
import com.cristhian.SistemaInventario.Service.IVentaPagoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class VentaPagoService implements IVentaPagoService {

    private static final Logger log = LoggerFactory.getLogger(VentaPagoService.class);

        private final VentaRepository ventaRepository;
        private final VentaPagoRepository ventaPagoRepository;
        private final MovimientoInventarioRepository movimientoInventarioRepository;
        private final ProductoRepository productoRepository;
        private final FormaPagoRepository formaPagoRepository;

        public VentaPagoService(VentaRepository ventaRepository,
                                VentaPagoRepository ventaPagoRepository,
                                MovimientoInventarioRepository movimientoInventarioRepository,
                                ProductoRepository productoRepository,
                                FormaPagoRepository formaPagoRepository) {
            this.ventaRepository = ventaRepository;
            this.ventaPagoRepository = ventaPagoRepository;
            this.movimientoInventarioRepository = movimientoInventarioRepository;
            this.productoRepository = productoRepository;
            this.formaPagoRepository = formaPagoRepository;
        }

        @Override
        public void registrarPago(int idVenta, VentaPagoDTO pagoDTO) {

            Venta venta = ventaRepository.findById(idVenta)
                    .orElseThrow(() -> new RecursoNoEncontradoException("Venta no encontrada"));

            if (venta.getEstado() == EstadoVenta.PAGADA) {
                throw new IllegalStateException("La venta ya está pagada");
            }

            if (pagoDTO.getMonto().compareTo(BigDecimal.ZERO) <= 0) {
                throw new IllegalArgumentException("El monto debe ser mayor a cero");
            }

            BigDecimal totalPagado = venta.getVentaPagos().stream()
                    .filter(p -> p.getEstadoPago() == EstadoPago.CONFIRMADO)
                    .map(VentaPago::getMonto)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            BigDecimal saldoPendiente = venta.getTotalVenta().subtract(totalPagado);

            if (pagoDTO.getMonto().compareTo(saldoPendiente) > 0) {
                throw new IllegalArgumentException("El pago supera el saldo pendiente");
            }


            Integer numeroCuotas = pagoDTO.getNumeroCuotas();

            // VALIDACIÓN CLAVE
            if (numeroCuotas == null || numeroCuotas < 1) {
                numeroCuotas = 1; // pago de contado
            }

            BigDecimal valorCuota = pagoDTO.getMonto()
                    .divide(BigDecimal.valueOf(numeroCuotas), RoundingMode.HALF_UP);

            VentaPago pago = new VentaPago();
            pago.setVenta(venta);
            pago.setMonto(pagoDTO.getMonto());
            pago.setFechaPago(LocalDate.now());
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

            ventaPagoRepository.save(pago);

            // MUY IMPORTANTE
            venta.getVentaPagos().add(pago);

            recalcularEstadoVenta(venta);
        }

        @Override
        public void confirmarPago(int idPago) {

            VentaPago pago = ventaPagoRepository.findById(idPago)
                    .orElseThrow(() -> new RecursoNoEncontradoException("Pago no encontrado"));

            if (pago.getEstadoPago() != EstadoPago.PENDIENTE_CONFIRMACION) {
                throw new IllegalStateException("El pago no está pendiente de confirmación");
            }

            pago.setEstadoPago(EstadoPago.CONFIRMADO);
            ventaPagoRepository.save(pago);

            recalcularEstadoVenta(pago.getVenta());

            // 🔥 SI YA QUEDÓ PAGADA → DESCONTAR STOCK
            /*if (pago.getVenta().getEstado() == EstadoVenta.CONFIRMADA) {

                for (DetalleVenta d : pago.getVenta().getDetalleVentas()) {
                    registrarMovimiento(d, pago.getVenta());
                }
            }*/
        }

        @Override
        public void rechazarPago(int idPago) {

            VentaPago pago = ventaPagoRepository.findById(idPago)
                    .orElseThrow(() -> new RecursoNoEncontradoException("Pago no encontrado"));

            pago.setEstadoPago(EstadoPago.RECHAZADO);
            ventaPagoRepository.save(pago);

            recalcularEstadoVenta(pago.getVenta());
        }

    public void recalcularEstadoVenta(Venta venta) {

        BigDecimal totalPagado = venta.getVentaPagos().stream()
                .filter(p -> p.getEstadoPago() == EstadoPago.CONFIRMADO)
                .map(VentaPago::getMonto)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalVenta = venta.getTotalVenta();

        if (totalPagado.compareTo(BigDecimal.ZERO) == 0) {
            venta.setEstado(EstadoVenta.PENDIENTE);
        }
        else if (totalPagado.compareTo(totalVenta) < 0) {
            venta.setEstado(EstadoVenta.PARCIAL);
        }
        else {
            venta.setEstado(EstadoVenta.PAGADA);
        }

        ventaRepository.save(venta);
    }

    public List<VentaPagoDTO> listarPorVenta(Long idVenta) {
        return ventaPagoRepository.findByVentaIdVenta(idVenta)
                .stream()
                .map(VentaPagoDTO::new)
                .toList();
    }

    // Este metodo permite guardar el movimineto del inventario desde la clase venta de manera automatica.
   /* public void registrarMovimiento(DetalleVenta detalle, Venta venta){

        //1. Buscamos el producto si existe.
        Producto producto = productoRepository.findById(detalle.getProducto().getIdProducto())
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        if (venta.getEstado() != EstadoVenta.PAGADA) {

            throw new IllegalStateException(
                    "No se puede descontar inventario si la venta no está pagada"
            );
        }

        // 1️⃣ Validación fuerte
        if (producto.getStockReservado() < detalle.getCantidad()) {
            throw new IllegalStateException(
                    "La cantidad reservada es insuficiente"
            );
        }
            //2. Hacemos el registro de la tabla movimiento inventario
            MovimientoInventario movimiento = new MovimientoInventario();

            movimiento.setTipoMovimiento(TipoMovimiento.SALIDA);
            movimiento.setOrigenMovimiento(OrigenMovimiento.VENTA);
            movimiento.setCantidad(-detalle.getCantidad());
            movimiento.setObservacion("Salida del producto por venta # " + venta.getIdVenta());
            movimiento.setProducto(producto);
            movimiento.setProveedor(null);

            movimientoInventarioRepository.save(movimiento);

            int descuentoStock = producto.getStock() - detalle.getCantidad();

          // 3. Conversión reserva → stock real
        producto.setStock(producto.getStock() - detalle.getCantidad());
        producto.setStockReservado(producto.getStockReservado() - detalle.getCantidad());

            productoRepository.save(producto);

    }*/



}


