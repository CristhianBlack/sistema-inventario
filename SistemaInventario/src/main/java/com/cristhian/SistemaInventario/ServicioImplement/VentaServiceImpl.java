package com.cristhian.SistemaInventario.ServicioImplement;

import com.cristhian.SistemaInventario.DTO.DetalleVentaDTO;
import com.cristhian.SistemaInventario.DTO.VentaDTO;
import com.cristhian.SistemaInventario.DTO.VentaPagoDTO;
import com.cristhian.SistemaInventario.Excepciones.RecursoNoEncontradoException;
import com.cristhian.SistemaInventario.Modelo.*;
import com.cristhian.SistemaInventario.Repositorio.*;
import com.cristhian.SistemaInventario.Service.IVentaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class VentaServiceImpl implements IVentaService {

    private static final Logger log = LoggerFactory.getLogger(VentaPagoService.class);

    private final VentaRepository ventaRepository;
    private final PersonaRepository personaRepository;
    private final ProductoRepository productoRepository;
    private final DetalleVentaRepository detalleVentaRepository;

    private final VentaPagoRepository ventaPagoRepository;
    private final MovimientoInventarioRepository movimientoInventarioRepository;

    public VentaServiceImpl(VentaRepository ventaRepository, PersonaRepository personaRepository,
                            ProductoRepository productoRepository, DetalleVentaRepository detalleVentaRepository,
                            VentaPagoRepository ventaPagoRepository,
                            MovimientoInventarioRepository movimientoInventarioRepository) {
        this.ventaRepository = ventaRepository;
        this.personaRepository = personaRepository;
        this.productoRepository = productoRepository;
        this.detalleVentaRepository = detalleVentaRepository;
        this.ventaPagoRepository = ventaPagoRepository;
        this.movimientoInventarioRepository = movimientoInventarioRepository;
    }

    @Override
    public List<Venta> listarVentas() {
        return ventaRepository.findAll();
    }

    @Override
    public Optional<Venta> buscarVentaPorId(int id) {
        return ventaRepository.findById(id);
    }

    @Transactional
    @Override
    public Venta guardarVenta(VentaDTO ventaDTO) {
        // Buscamos la persona
         Persona persona = personaRepository.findById(ventaDTO.getIdPersona())
                 .orElseThrow(()-> new RecursoNoEncontradoException("Persona no encontrada."));
         //Crramos la venta
         Venta venta = new Venta(ventaDTO);
         venta.setPersona(persona);
         venta.setEstado(EstadoVenta.PENDIENTE);

        System.out.println("Estado venta: {}"+ venta.getEstado());

        venta.setSubTotalVenta(BigDecimal.ZERO);
        venta.setTotalImpuestos(BigDecimal.ZERO);
        venta.setTotalVenta(BigDecimal.ZERO);

        actualizarEstadoPorPago(venta);

         //guardamos la venta para obtener ID
        Venta ventaGuardada = ventaRepository.save(venta);

        guardarDetalleVenta(ventaDTO, ventaGuardada);

        /*if (venta.getEstado() != null && venta.getEstado() == EstadoVenta.PENDIENTE) {
            guardarDetalleVenta(ventaDTO, ventaGuardada);
            guardarVentaPago(ventaDTO, ventaGuardada);
        }*/
        if (ventaDTO.getPagos() != null && !ventaDTO.getPagos().isEmpty()) {
            guardarVentaPago(ventaDTO, ventaGuardada);
        }
        /*for (DetalleVenta d : ventaGuardada.getDetalleVentas()) {
            reservarStock(d);
        }*/

        return ventaGuardada;
    }

    // Guardamos nuestro detalle de venta en la base de datos
    public void guardarDetalleVenta(VentaDTO ventaDTO, Venta venta){
        // Guardado de detalles
        if(ventaDTO.getDetalles() != null && !ventaDTO.getDetalles().isEmpty()) {

            BigDecimal subTotalVenta = BigDecimal.ZERO;
            BigDecimal totalImpuestos = BigDecimal.ZERO;
            BigDecimal totalVenta = BigDecimal.ZERO;

            for(DetalleVentaDTO detalleVeta : ventaDTO.getDetalles()) {
                Producto producto = productoRepository.findById(detalleVeta.getIdProducto())
                        .orElseThrow(() -> new RecursoNoEncontradoException(
                                "Producto no encontrado: " + detalleVeta.getIdProducto()));


                BigDecimal precioUnitario = BigDecimal.valueOf(producto.getPrecioVenta());
                BigDecimal cantidad = BigDecimal.valueOf(detalleVeta.getCantidad());
                BigDecimal descuento = detalleVeta.getDescuento() != null ? detalleVeta.getDescuento() : BigDecimal.ZERO;


                BigDecimal subTotalLinea = precioUnitario
                        .multiply(cantidad)
                        .subtract(descuento);

                BigDecimal porcentaje = producto.getImpuesto().getPorcentaje(); // 0.19

                BigDecimal impuestoLinea = subTotalLinea.multiply(porcentaje);

                BigDecimal totalLinea = subTotalLinea.add(impuestoLinea);

                System.out.println("Descuento: " + descuento);

                // 🔥 Acumulamos totales de la venta
                subTotalVenta = subTotalVenta.add(subTotalLinea);
                totalImpuestos = totalImpuestos.add(impuestoLinea);
                totalVenta = totalVenta.add(totalLinea);

                DetalleVenta detalle = new DetalleVenta();
                detalle.setCantidad(detalleVeta.getCantidad());
                detalle.setPrecioUnitario(BigDecimal.valueOf(producto.getPrecioVenta()));
                detalle.setDescuento(descuento);
                detalle.setSubtotalLinea(subTotalLinea);
                detalle.setImpuestoLinea(impuestoLinea);
                detalle.setTotalLinea(totalLinea);
                detalle.setVenta(venta);
                detalle.setProducto(producto);
                detalle.setImpuesto(producto.getImpuesto());

                detalleVentaRepository.save(detalle);
            }
            // ACTUALIZAMOS LA VENTA
            venta.setSubTotalVenta(subTotalVenta);
            venta.setTotalImpuestos(totalImpuestos);
            venta.setTotalVenta(totalVenta);

            ventaRepository.save(venta);
        }
    }

    public void guardarVentaPago(VentaDTO ventaDTO, Venta venta){

        if (ventaDTO.getPagos() == null || ventaDTO.getPagos().isEmpty()) {
            return;
        }

        if (venta.getEstado() == EstadoVenta.PAGADA) {
            throw new IllegalStateException("La venta ya está pagada");
        }

        if (venta.getEstado() == EstadoVenta.CANCELADA) {
            throw new IllegalStateException("La venta está cancelada");
        }

        for (VentaPagoDTO pagoDTO : ventaDTO.getPagos()) {

            if (pagoDTO.getEstadoPago() == null) {
                throw new IllegalArgumentException("El estado del pago es obligatorio");
            }
            if (pagoDTO.getMonto().compareTo(BigDecimal.ZERO) <= 0) {
                throw new IllegalArgumentException("El monto del pago debe ser mayor a 0");
            }

            // Reglas de negocio
            switch (pagoDTO.getEstadoPago()) {
                case PENDIENTE_CONFIRMACION -> {
                    // El pago se registra
                    // No afecta el total pagado
                    // No cambia el estado de la venta
                }

                case CONFIRMADO -> {
                    // El pago es válido
                    // Se tendrá en cuenta para calcular el total pagado
                }

                case EN_CUOTAS -> {
                    /*if (pagoDTO.getNumeroCuotas() <= 0) {
                        throw new IllegalArgumentException(
                                "Debe especificar un número válido de cuotas"
                        );
                    }*/
                    // Se registra el pago
                    // El estado de la venta será PARCIAL
                }

                case RECHAZADO, CANCELADO -> {
                    // El pago no es válido
                    // No afecta el total pagado
                    // No cambia el estado de la venta
                }
            }

            VentaPago pago = new VentaPago();
            pago.setVenta(venta);
            pago.setEstadoPago(pagoDTO.getEstadoPago());
            pago.setMonto(pagoDTO.getMonto());
            pago.setFechaPago(LocalDate.now());

            ventaPagoRepository.save(pago);
            venta.getVentaPagos().add(pago);
        }

        venta.setEstado(calcularEstadoVenta(venta));
        ventaRepository.save(venta);
    }

    private EstadoVenta calcularEstadoVenta(Venta venta) {

        BigDecimal totalPagado = venta.getVentaPagos().stream()
                .filter(p -> p.getEstadoPago() == EstadoPago.CONFIRMADO)
                .map(VentaPago::getMonto)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalVenta =(venta.getTotalVenta());


        if (totalPagado.compareTo(BigDecimal.ZERO) == 0) {
            return venta.getEstado();
        }

        if (totalPagado.compareTo(totalVenta) < 0) {
            return EstadoVenta.PARCIAL;
        }

        return EstadoVenta.PAGADA;
    }

    @Override
    public void cancelarVenta(int idVenta) {
        Venta venta = ventaRepository.findById(idVenta)
                .orElseThrow(() -> new RecursoNoEncontradoException("Venta no encontrada"));

        if (venta.getEstado() == EstadoVenta.PAGADA || venta.getEstado() == EstadoVenta.CONFIRMADA) {
            throw new IllegalStateException("No se puede cancelar una venta confirmada o pagada");
        }

        venta.setEstado(EstadoVenta.CANCELADA);
        ventaRepository.save(venta);
    }

   /* private void reservarStock(DetalleVenta detalle) {

        Producto producto = productoRepository.findById(
                detalle.getProducto().getIdProducto()
        ).orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        int disponible = producto.getStock() - producto.getStockReservado();

        if (disponible < detalle.getCantidad()) {
            throw new IllegalStateException("Stock insuficiente para el producto");
        }

        producto.setStockReservado(
                producto.getStockReservado() + detalle.getCantidad()
        );

        productoRepository.save(producto);
    }*/


    public void confirmarVenta(int idVenta) {

        Venta venta = ventaRepository.findById(idVenta)
                .orElseThrow(() -> new RecursoNoEncontradoException("Venta no encontrada"));

        if (venta.getEstado() != EstadoVenta.PENDIENTE) {
            throw new IllegalStateException("Solo se pueden confirmar ventas pendientes");
        }

        for (DetalleVenta d : venta.getDetalleVentas()) {
            registrarMovimiento(d, venta);
        }

        venta.setEstado(EstadoVenta.CONFIRMADA);
        ventaRepository.save(venta);
    }



    private void actualizarEstadoPorPago(Venta venta) {

        EstadoVenta estadoPago = calcularEstadoVenta(venta);

        if (estadoPago == EstadoVenta.PARCIAL) {
            venta.setEstado(EstadoVenta.PARCIAL);
        }
        else if (estadoPago == EstadoVenta.PAGADA) {
            venta.setEstado(EstadoVenta.PAGADA);
        }
    }

    public void registrarMovimiento(DetalleVenta detalle, Venta venta) {

        log.info("=== INICIO registrarMovimiento ===");

        log.info("Venta ID: {}, Estado: {}", venta.getIdVenta(), venta.getEstado());
        log.info("Detalle -> Producto ID: {}, Cantidad: {}",
                detalle.getProducto().getIdProducto(), detalle.getCantidad());

        Producto producto = productoRepository.findById(detalle.getProducto().getIdProducto())
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        log.info("Producto encontrado -> ID: {}, Stock: {}, StockReservado: {}",
                producto.getIdProducto(), producto.getStock());

        if (venta.getEstado() != EstadoVenta.PENDIENTE) {
            log.error("❌ Venta no está PAGADA");
            throw new IllegalStateException("La venta no esta en estado pendiente.");
        }

        if (producto.getStock() < detalle.getCantidad()) {
            log.error("❌ Stock reservado insuficiente");
            throw new IllegalStateException(
                    "La cantidad reservada es insuficiente"
            );
        }

        MovimientoInventario movimiento = new MovimientoInventario();

        movimiento.setTipoMovimiento(TipoMovimiento.SALIDA);
        movimiento.setOrigenMovimiento(OrigenMovimiento.VENTA);
        movimiento.setCantidad(detalle.getCantidad());
        movimiento.setObservacion("Salida del producto por venta # " + venta.getIdVenta());
        movimiento.setProducto(producto);
        movimiento.setProveedor(null);
        movimiento.setFechaMovimiento(LocalDateTime.now());

        // 🔍 LOG CLAVE (ANTES DEL SAVE)
        log.info("Movimiento a guardar:");
        log.info("  Tipo: {}", movimiento.getTipoMovimiento());
        log.info("  Origen: {}", movimiento.getOrigenMovimiento());
        log.info("  Cantidad: {}", movimiento.getCantidad());
        log.info("  Producto ID: {}",
                movimiento.getProducto() != null ? movimiento.getProducto().getIdProducto() : "NULL");
        log.info("  Proveedor: {}", movimiento.getProveedor());

        movimientoInventarioRepository.save(movimiento);

        log.info("✅ Movimiento guardado correctamente");

        producto.setStock(producto.getStock() - detalle.getCantidad());
        productoRepository.save(producto);

        log.info("=== FIN registrarMovimiento ===");
    }


}
