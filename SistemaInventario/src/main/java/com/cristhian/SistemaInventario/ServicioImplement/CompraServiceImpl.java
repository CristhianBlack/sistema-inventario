package com.cristhian.SistemaInventario.ServicioImplement;

import com.cristhian.SistemaInventario.DTO.*;
import com.cristhian.SistemaInventario.Excepciones.RecursoNoEncontradoException;
import com.cristhian.SistemaInventario.Modelo.*;
import com.cristhian.SistemaInventario.Repositorio.*;
import com.cristhian.SistemaInventario.Service.ICompraService;
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
public class CompraServiceImpl implements ICompraService {

    private static final Logger logger = LoggerFactory.getLogger(CiudadServiceImpl.class);

    private final CompraRepository compraRepository;
    private final ProveedorRepository proveedorRepository;
    private final DetalleCompraRepository detalleCompraRepository;
    private final ProductoRepository productoRepository;
    private final MovimientoInventarioRepository movimientoInventarioRepository;
    private final CompraPagoRepository compraPagoRepository;


    public CompraServiceImpl(CompraRepository compraRepository, ProveedorRepository proveedorRepository,
                             DetalleCompraRepository detalleCompraRepository,ProductoRepository productoRepository,
                             MovimientoInventarioRepository movimientoInventarioRepository,
                             CompraPagoRepository compraPagoRepository) {
        this.compraRepository = compraRepository;
        this.proveedorRepository = proveedorRepository;
        this.detalleCompraRepository = detalleCompraRepository;
        this.productoRepository = productoRepository;
        this.movimientoInventarioRepository = movimientoInventarioRepository;
        this.compraPagoRepository = compraPagoRepository;
    }

    @Override
    public List<Compra> listarComprasActivas() {
        return compraRepository.findByActivoTrue();
    }

    @Override
    public Optional<Compra> BuscarCompraId(int id) {
        return compraRepository.findById(id);
    }

    @Override
    public Compra guardarCompra(CompraDTO compraDTO) {

        // Buscar proveedor
        Proveedor proveedor = proveedorRepository.findById(compraDTO.getIdProveedor())
                .orElseThrow(() -> new RecursoNoEncontradoException("Proveedor no encontrado"));

        // Crear compra
        Compra compra = new Compra(compraDTO);
        compra.setProveedor(proveedor);
        compra.setEstado(EstadoCompra.PENDIENTE);

        System.out.println("Estado venta: {}"+ compra.getEstado());

        compra.setSubTotalCompra(BigDecimal.ZERO);
        compra.setTotalImpuestos(BigDecimal.ZERO);
        compra.setTotalCompra(BigDecimal.ZERO);

        actualizarEstadoPorPago(compra);

        // Guardar compra para obtener ID
        Compra compraGuardada = compraRepository.save(compra);

        guardarDetalleCompra(compraDTO, compraGuardada);

        if (compraDTO.getPagos() != null && !compraDTO.getPagos().isEmpty()) {
            guardarCompraPago(compraDTO, compraGuardada);
        }

        return compraGuardada;
    }

    public void guardarDetalleCompra(CompraDTO compraDTO, Compra compra){
        // Guardado de detalles
        if(compraDTO.getDetalles() != null && !compraDTO.getDetalles().isEmpty()) {

            BigDecimal subTotalCompra = BigDecimal.ZERO;
            BigDecimal totalImpuestos = BigDecimal.ZERO;
            BigDecimal totalCompra = BigDecimal.ZERO;

            for(DetalleCompraDTO detalleCompra : compraDTO.getDetalles()) {
                Producto producto = productoRepository.findById(detalleCompra.getIdProducto())
                        .orElseThrow(() -> new RecursoNoEncontradoException(
                                "Producto no encontrado: " + detalleCompra.getIdProducto()));


                BigDecimal precioUnitario = (producto.getPrecioCompra());
                BigDecimal cantidad = BigDecimal.valueOf(detalleCompra.getCantidad());
                //BigDecimal descuento = de.getDescuento() != null ? detalleVeta.getDescuento() : BigDecimal.ZERO;


                BigDecimal subTotalLinea = precioUnitario
                        .multiply(cantidad);

                BigDecimal porcentaje = producto.getImpuesto().getPorcentaje(); // 0.19

                BigDecimal impuestoLinea = subTotalLinea.multiply(porcentaje);

                BigDecimal totalLinea = subTotalLinea.add(impuestoLinea);



                //  Acumulamos totales de la compra
                subTotalCompra = subTotalCompra.add(subTotalLinea);
                totalImpuestos = totalImpuestos.add(impuestoLinea);
                totalCompra = totalCompra.add(totalLinea);

                DetalleCompra detalle = new DetalleCompra();
                detalle.setCantidad(detalleCompra.getCantidad());
                detalle.setPrecioUnitario(producto.getPrecioCompra());
                detalle.setSubtotalLinea(subTotalLinea);
                detalle.setImpuestoLinea(impuestoLinea);
                detalle.setTotalLinea(totalLinea);
                detalle.setCompra(compra);
                detalle.setProducto(producto);


                detalleCompraRepository.save(detalle);

                registrarMovimiento(detalle, compra);
            }
            // ACTUALIZAMOS LA VENTA
            compra.setSubTotalCompra(subTotalCompra);
            compra.setTotalImpuestos(totalImpuestos);
            compra.setTotalCompra(totalCompra);

            compraRepository.save(compra);
        }
    }

    public void guardarCompraPago(CompraDTO compraDTO, Compra compra){

        if (compraDTO.getPagos() == null || compraDTO.getPagos().isEmpty()) {
            return;
        }

        if (compra.getEstado() == EstadoCompra.PAGADA) {
            throw new IllegalStateException("La compra ya está pagada");
        }

        if (compra.getEstado() == EstadoCompra.CANCELADA) {
            throw new IllegalStateException("La compra está cancelada");
        }

        for (CompraPagoDTO pagoDTO : compraDTO.getPagos()) {

            if (pagoDTO.getEstadoPago() == null) {
                throw new IllegalArgumentException("El estado del pago es obligatorio");
            }
            if (pagoDTO.getMontoCompra().compareTo(BigDecimal.ZERO) <= 0) {
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

            CompraPago pago = new CompraPago();
            pago.setCompra(compra);
            pago.setEstadoPago(pagoDTO.getEstadoPago());
            pago.setMontoCompra(pagoDTO.getMontoCompra());
            pago.setFechaPago(LocalDateTime.now());

            compraPagoRepository.save(pago);
            compra.getCompraPagos().add(pago);
        }

        compra.setEstado(calcularEstadoCompra(compra));
        compraRepository.save(compra);
    }

    private EstadoCompra calcularEstadoCompra(Compra compra) {

        BigDecimal totalPagado = compra.getCompraPagos().stream()
                .filter(p -> p.getEstadoPago() == EstadoPago.CONFIRMADO)
                .map(CompraPago::getMontoCompra)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalVenta =(compra.getTotalCompra());


        if (totalPagado.compareTo(BigDecimal.ZERO) == 0) {
            return compra.getEstado();
        }

        if (totalPagado.compareTo(totalVenta) < 0) {
            return EstadoCompra.PARCIAL;
        }

        return EstadoCompra.PAGADA;
    }

    public void cancelarCompra(int id) {
        Compra compra = compraRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Compra no encontrada"));

        if (compra.getEstado() == EstadoCompra.PAGADA || compra.getEstado() == EstadoCompra.CONFIRMADA) {
            throw new IllegalStateException("No se puede cancelar una compra confirmada o pagada");
        }

        compra.setEstado(EstadoCompra.CANCELADA);
        compraRepository.save(compra);
    }

    private void actualizarEstadoPorPago(Compra compra) {

        EstadoCompra estadoPago = calcularEstadoCompra(compra);

        if (estadoPago == EstadoCompra.PARCIAL) {
            compra.setEstado(EstadoCompra.PARCIAL);
        }
        else if (estadoPago == EstadoCompra.PAGADA) {
            compra.setEstado(EstadoCompra.PAGADA);
        }
    }

    @Override
    public void eliminarCompra(int id) {
        Compra compra = compraRepository.findById(id).orElse(null);
        if (compra != null) {
            compra.setActivo(false); // solo la marcamos como inactiva
            compraRepository.save(compra);
        }
    }

    // Este metodo permite guardar el movimineto del inventario desde la clase compra de manera automatica.
    public void registrarMovimiento(DetalleCompra detalle, Compra compra){

        //1. Hacemos el registro de la tabla movimiento inventario
        MovimientoInventario movimiento = new MovimientoInventario();

        movimiento.setTipoMovimiento(TipoMovimiento.ENTRADA);
        movimiento.setOrigenMovimiento(OrigenMovimiento.COMPRA);
        movimiento.setCantidad(detalle.getCantidad());
        movimiento.setObservacion("Ingreso el producto por compra # "+ compra.getIdCompra());
        movimiento.setProducto(detalle.getProducto());
        movimiento.setProveedor(compra.getProveedor());
        movimiento.setFechaMovimiento(LocalDateTime.now());

        movimientoInventarioRepository.save(movimiento);

        //2. Buscamos el producto si existe.
        Producto producto = productoRepository.findById(detalle.getProducto().getIdProducto())
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        //3. Actualizamos nuestro stock y precio de compra de la tabla producto y el precio.
        producto.setStock(producto.getStock() + detalle.getCantidad());
        producto.setPrecioCompra(detalle.getPrecioUnitario());

        productoRepository.save(producto);
    }
}
