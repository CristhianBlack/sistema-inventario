/*package com.cristhian.SistemaInventario.ServicioImplement;

import com.cristhian.SistemaInventario.DTO.*;
import com.cristhian.SistemaInventario.Enums.EstadoCompra;
import com.cristhian.SistemaInventario.Enums.EstadoPago;
import com.cristhian.SistemaInventario.Enums.OrigenMovimiento;
import com.cristhian.SistemaInventario.Enums.TipoMovimiento;
import com.cristhian.SistemaInventario.Excepciones.RecursoNoEncontradoException;
import com.cristhian.SistemaInventario.Modelo.*;
import com.cristhian.SistemaInventario.Repositorio.*;
import com.cristhian.SistemaInventario.Service.ICompraService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
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
        compra.setEstado(EstadoCompra.CONFIRMADA);

        // Inicializar totales en CERO (IMPORTANTE)
        compra.setSubTotalCompra(BigDecimal.ZERO);
        compra.setTotalImpuestos(BigDecimal.ZERO);
        compra.setTotalCompra(BigDecimal.ZERO);

        // Guardar compra para obtener ID
         compra = compraRepository.save(compra);

        System.out.println("Estado venta: {}"+ compra.getEstado());

        guardarDetalleCompra(compraDTO, compra);

        if (compraDTO.getPagos() != null && !compraDTO.getPagos().isEmpty()) {
            guardarCompraPago(compraDTO, compra);
        }

        compra.setEstado(calcularEstadoCompra(compra));

        return compraRepository.save(compra);
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


                BigDecimal precioUnitario = detalleCompra.getPrecioUnitario();
                BigDecimal cantidad = BigDecimal.valueOf(detalleCompra.getCantidad());
                //BigDecimal descuento = de.getDescuento() != null ? detalleVeta.getDescuento() : BigDecimal.ZERO;


                BigDecimal subTotalLinea = precioUnitario
                        .multiply(cantidad);

                BigDecimal porcentaje = producto.getImpuesto() != null
                        ? producto.getImpuesto().getPorcentaje()
                        : BigDecimal.ZERO;

                BigDecimal impuestoLinea = subTotalLinea.multiply(porcentaje);

                BigDecimal totalLinea = subTotalLinea.add(impuestoLinea);



                //  Acumulamos totales de la compra
                subTotalCompra = subTotalCompra.add(subTotalLinea);
                totalImpuestos = totalImpuestos.add(impuestoLinea);
                totalCompra = totalCompra.add(totalLinea);

                DetalleCompra detalle = new DetalleCompra();
                detalle.setCantidad(detalleCompra.getCantidad());
                detalle.setPrecioUnitario(precioUnitario);
                detalle.setSubtotalLinea(subTotalLinea);
                detalle.setImpuestoLinea(impuestoLinea);
                detalle.setTotalLinea(totalLinea);
                detalle.setCompra(compra);
                detalle.setProducto(producto);


                detalleCompraRepository.save(detalle);

                registrarMovimiento(detalle, compra);
            }
            // ACTUALIZAMOS LA COMPRA
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
                /*}

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
}*/

package com.cristhian.SistemaInventario.ServicioImplement;

import com.cristhian.SistemaInventario.DTO.*;
import com.cristhian.SistemaInventario.Enums.*;
import com.cristhian.SistemaInventario.Excepciones.RecursoNoEncontradoException;
import com.cristhian.SistemaInventario.Modelo.*;
import com.cristhian.SistemaInventario.Repositorio.*;
import com.cristhian.SistemaInventario.Security.UsuarioAutenticadoProvider;
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
/**
 * Servicio encargado de gestionar el ciclo completo de una compra:
 * - Registro de la compra
 * - Detalles de productos
 * - Actualización de inventario
 * - Registro de pagos
 * - Generación y reversión de asientos contables
 */
@Service
@Transactional
public class CompraServiceImpl implements ICompraService {
    private static final Logger logger =
            LoggerFactory.getLogger(CiudadServiceImpl.class);

    // ============================
    // REPOSITORIOS
    // ============================

    private final CompraRepository compraRepository;
    private final ProveedorRepository proveedorRepository;
    private final DetalleCompraRepository detalleCompraRepository;
    private final ProductoRepository productoRepository;
    private final MovimientoInventarioRepository movimientoInventarioRepository;
    private final CompraPagoRepository compraPagoRepository;
    private final AsientoContableRepository asientoContableRepository;

    // ============================
    // SERVICIOS
    // ============================

    private final ProductoServiceImpl productoService;
    private final CompraContableServiceImpl compraContableService;
    private final ValidacionContableService validacionContableService;
    private final UsuarioAutenticadoProvider usuarioProvider;

    /**
     * Constructor con inyección de dependencias
     */
    public CompraServiceImpl(
            CompraRepository compraRepository,
            ProveedorRepository proveedorRepository,
            DetalleCompraRepository detalleCompraRepository,
            ProductoRepository productoRepository,
            MovimientoInventarioRepository movimientoInventarioRepository,
            CompraPagoRepository compraPagoRepository,
            ProductoServiceImpl productoService,
            CompraContableServiceImpl compraContableService,
            AsientoContableRepository asientoContableRepository,
            ValidacionContableService validacionContableService,
            UsuarioAutenticadoProvider usuarioProvider) {

        this.compraRepository = compraRepository;
        this.proveedorRepository = proveedorRepository;
        this.detalleCompraRepository = detalleCompraRepository;
        this.productoRepository = productoRepository;
        this.movimientoInventarioRepository = movimientoInventarioRepository;
        this.compraPagoRepository = compraPagoRepository;
        this.productoService = productoService;
        this.compraContableService = compraContableService;
        this.asientoContableRepository = asientoContableRepository;
        this.validacionContableService = validacionContableService;
        this.usuarioProvider = usuarioProvider;
    }

    /**
     * Lista todas las compras activas ordenadas por fecha descendente
     */
    @Override
    public List<Compra> listarComprasActivas() {
        return compraRepository.findByActivoTrueOrderByFechaCompraDesc();
    }

    /**
     * Busca una compra por su ID
     */
    @Override
    public Optional<Compra> BuscarCompraId(int id) {
        return compraRepository.findById(id);
    }

    /**
     * Registra una nueva compra completa:
     * - Valida apertura contable
     * - Guarda compra
     * - Guarda detalles
     * - Actualiza inventario
     * - Registra pagos
     * - Genera asiento contable
     */
    @Override
    public Compra guardarCompra(CompraDTO compraDTO) {

        // Usuario autenticado
        Usuario usuario = usuarioProvider.obtenerUsuarioAutenticado();

        // Validar existencia de asiento de apertura
        validacionContableService.validarApertura(TipoAsiento.NORMAL);

        System.out.println("ID PROVEEDOR RECIBIDO: " + compraDTO.getIdProveedor());
        // Buscar proveedor
        Proveedor proveedor = proveedorRepository.findByPersonaIdPersona(compraDTO.getIdProveedor())
                .orElseThrow(() -> new RecursoNoEncontradoException("Proveedor no encontrado"));

        // Crear entidad compra
        Compra compra = new Compra(compraDTO);
        compra.setProveedor(proveedor);
        compra.setEstado(EstadoCompra.PENDIENTE);
        compra.setUsuario(usuario);

        // Inicializar totales
        compra.setSubTotalCompra(BigDecimal.ZERO);
        compra.setTotalImpuestos(BigDecimal.ZERO);
        compra.setTotalCompra(BigDecimal.ZERO);

        // Guardar compra para obtener ID
        compra = compraRepository.save(compra);

        // Guardar detalle de productos
        guardarDetalleCompra(compraDTO, compra);

        // Guardar pagos si existen
        if (compraDTO.getPagos() != null && !compraDTO.getPagos().isEmpty()) {
            guardarCompraPago(compraDTO, compra);
        }

        // Calcular total pagado
        /*BigDecimal totalPagado = compra.getCompraPagos().stream()
                .filter(p -> p.getEstadoPago() == EstadoPago.CONFIRMADO)
                .map(CompraPago::getMontoCompra)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        boolean esCredito = totalPagado.compareTo(compra.getTotalCompra()) < 0;*/


        // Actualizar estado final de la compra
        compra.setEstado(calcularEstadoCompra(compra));

        return compraRepository.save(compra);
    }

    /**
     * Guarda los detalles de la compra y actualiza inventario
     */
    public void guardarDetalleCompra(CompraDTO compraDTO, Compra compra) {

        if (compraDTO.getDetalles() == null || compraDTO.getDetalles().isEmpty()) {
            return;
        }

        BigDecimal subTotalCompra = BigDecimal.ZERO;
        BigDecimal totalImpuestos = BigDecimal.ZERO;
        BigDecimal totalCompra = BigDecimal.ZERO;

        for (DetalleCompraDTO detalleDTO : compraDTO.getDetalles()) {

            // Buscar producto
            Producto producto = productoRepository.findById(detalleDTO.getIdProducto())
                    .orElseThrow(() ->
                            new RecursoNoEncontradoException(
                                    "Producto no encontrado: " + detalleDTO.getIdProducto()));

            BigDecimal precioUnitario = detalleDTO.getPrecioUnitario();
            BigDecimal cantidad = BigDecimal.valueOf(detalleDTO.getCantidad());

            // Cálculo de valores
            BigDecimal subTotalLinea = precioUnitario.multiply(cantidad);

            BigDecimal porcentajeImpuesto = producto.getImpuesto() != null
                    ? producto.getImpuesto().getPorcentaje()
                    : BigDecimal.ZERO;

            BigDecimal impuestoLinea = subTotalLinea.multiply(porcentajeImpuesto);
            BigDecimal totalLinea = subTotalLinea.add(impuestoLinea);

            // Acumulación
            subTotalCompra = subTotalCompra.add(subTotalLinea);
            totalImpuestos = totalImpuestos.add(impuestoLinea);
            totalCompra = totalCompra.add(totalLinea);

            // Guardar detalle
            DetalleCompra detalle = new DetalleCompra();
            detalle.setCantidad(detalleDTO.getCantidad());
            detalle.setPrecioUnitario(precioUnitario);
            detalle.setSubtotalLinea(subTotalLinea);
            detalle.setImpuestoLinea(impuestoLinea);
            detalle.setTotalLinea(totalLinea);
            detalle.setCompra(compra);
            detalle.setProducto(producto);


            detalleCompraRepository.save(detalle);

            // Aplicar entrada de inventario
            productoService.aplicarCompra(producto, detalleDTO.getCantidad(), precioUnitario);

            // Actualizar estado de stock
            if (producto.getStock() == detalleDTO.getCantidad()) {
                if (detalleDTO.getCantidad() <= producto.getStockMinimo()) {
                    producto.setEstadoStock(EstadoStock.BAJO_MINIMO);
                } else {
                    producto.setEstadoStock(EstadoStock.NORMAL);
                }
            }

            productoRepository.save(producto);

            // Registrar movimiento de inventario
            registrarMovimiento(detalle, compra);
        }

        // Actualizar totales de la compra
        compra.setSubTotalCompra(subTotalCompra);
        compra.setTotalImpuestos(totalImpuestos);
        compra.setTotalCompra(totalCompra);

        compraRepository.save(compra);
    }

    /**
     * Guarda los pagos asociados a la compra
     */
    public void guardarCompraPago(CompraDTO compraDTO, Compra compra) {

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

    /**
     * Calcula el estado de la compra según pagos confirmados
     */
    private EstadoCompra calcularEstadoCompra(Compra compra) {

        BigDecimal totalPagado = compra.getCompraPagos().stream()
                .filter(p -> p.getEstadoPago() == EstadoPago.CONFIRMADO)
                .map(CompraPago::getMontoCompra)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalCompra = compra.getTotalCompra();

        if (totalPagado.compareTo(BigDecimal.ZERO) == 0) {
            return compra.getEstado();
        }

        if (totalPagado.compareTo(totalCompra) < 0) {
            return EstadoCompra.PARCIAL;
        }

        return EstadoCompra.PAGADA;
    }

    /*ESte metodo nos permite confirmar la venta actializnado el estado de venta */
    public void confirmarCompra(int idCompra) {

        //consultamos si existe la venta
        Compra compra = compraRepository.findById(idCompra)
                .orElseThrow(() -> new RecursoNoEncontradoException("Compra no encontrada"));

        //validamos si el estado es diferente de pendiente mandamos un mensaje donde no se
        // puede confirmar la venta
        if (compra.getEstado() != EstadoCompra.PENDIENTE) {
            throw new IllegalStateException("La compra ya fue confirmada o no está en estado válido");
        }

        //Actualizamos nuestro estado venta como confirmada
        compra.setEstado(EstadoCompra.CONFIRMADA);

        // Generar asiento contable de la compra
        compraContableService.registrarAsientoCompra(compra);
        //guardamos nuestro nuevo estado venta.
        compraRepository.save(compra);
    }

    /**
     * Cancela una compra y revierte su asiento contable
     */
    public void cancelarCompra(int id) {

        Compra compra = compraRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Compra no encontrada"));

        if (compra.getEstado() == EstadoCompra.PAGADA
                || compra.getEstado() == EstadoCompra.CONFIRMADA) {
            throw new IllegalStateException(
                    "No se puede cancelar una compra confirmada o pagada");
        }

        // Cambiar estado
        compra.setEstado(EstadoCompra.CANCELADA);
        compraRepository.save(compra);

        // Buscar asiento contable
        AsientoContable asientoCompra = asientoContableRepository
                .findByCompra(compra)
                .orElseThrow(() ->
                        new IllegalStateException("No existe asiento contable para esta compra")
                );

        // Reversar asiento
        //compraContableService.reversarAsientoCompra(asientoCompra);
    }

    /**
     * Eliminación lógica de una compra
     */
    @Override
    public void eliminarCompra(int id) {
        Compra compra = compraRepository.findById(id).orElse(null);
        if (compra != null) {
            compra.setActivo(false);
            compraRepository.save(compra);
        }
    }

    /**
     * Registra el movimiento de inventario generado por la compra
     */
    public void registrarMovimiento(DetalleCompra detalle, Compra compra) {

        MovimientoInventario movimiento = new MovimientoInventario();
        movimiento.setTipoMovimiento(TipoMovimiento.ENTRADA);
        movimiento.setOrigenMovimiento(OrigenMovimiento.COMPRA);
        movimiento.setCantidad(detalle.getCantidad());
        movimiento.setObservacion(
                "Ingreso el producto por compra # " + compra.getIdCompra());
        movimiento.setProducto(detalle.getProducto());
        movimiento.setProveedor(compra.getProveedor());
        movimiento.setFechaMovimiento(LocalDateTime.now());

        movimientoInventarioRepository.save(movimiento);
    }
}

