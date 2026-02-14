package com.cristhian.SistemaInventario.ServicioImplement;

import com.cristhian.SistemaInventario.DTO.DetalleVentaDTO;
import com.cristhian.SistemaInventario.DTO.VentaDTO;
import com.cristhian.SistemaInventario.DTO.VentaPagoDTO;
import com.cristhian.SistemaInventario.Enums.*;
import com.cristhian.SistemaInventario.Excepciones.RecursoNoEncontradoException;
import com.cristhian.SistemaInventario.Modelo.*;
import com.cristhian.SistemaInventario.Repositorio.*;
import com.cristhian.SistemaInventario.Security.UsuarioAutenticadoProvider;
import com.cristhian.SistemaInventario.Service.IVentaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Servicio encargado de gestionar el ciclo completo de una venta:
 * - Registro de la venta
 * - Detalles de productos
 * - Actualización de inventario
 * - Registro de pagos
 * - Generación y reversión de asientos contables
 */
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
    private final MovimientoSaldoPersonaRepository movimientoSaldoPersonaRepository;

    private final ValidacionContableService validacionContableService;
    private final UsuarioAutenticadoProvider usuarioProvider;

    private final VentaContableServiceImpl ventaContableService;

    public VentaServiceImpl(VentaRepository ventaRepository, PersonaRepository personaRepository,
                            ProductoRepository productoRepository, DetalleVentaRepository detalleVentaRepository,
                            VentaPagoRepository ventaPagoRepository,
                            MovimientoInventarioRepository movimientoInventarioRepository,
                            MovimientoSaldoPersonaRepository movimientoSaldoPersonaRepository,
                            ValidacionContableService validacionContableService,
                            UsuarioAutenticadoProvider usuarioProvider,
                            VentaContableServiceImpl ventaContableService) {
        this.ventaRepository = ventaRepository;
        this.personaRepository = personaRepository;
        this.productoRepository = productoRepository;
        this.detalleVentaRepository = detalleVentaRepository;
        this.ventaPagoRepository = ventaPagoRepository;
        this.movimientoInventarioRepository = movimientoInventarioRepository;
        this.movimientoSaldoPersonaRepository = movimientoSaldoPersonaRepository;
        this.validacionContableService = validacionContableService;
        this.usuarioProvider = usuarioProvider;
        this.ventaContableService = ventaContableService;
    }

    @Override
    public List<Venta> listarVentas() {
        return ventaRepository.findAllByOrderByFechaVentaDesc();
    }

    @Override
    public Optional<Venta> buscarVentaPorId(int id) {
        return ventaRepository.findById(id);
    }

    /**
     * Registra una nueva venta completa:
     * - Valida apertura contable
     * - Guarda compra
     * - Guarda detalles
     * - Actualiza inventario
     * - Registra pagos
     * - Genera asiento contable
     */
    @Transactional
    @Override
    public Venta guardarVenta(VentaDTO ventaDTO) {

        // Usuario autenticado
        Usuario usuario = usuarioProvider.obtenerUsuarioAutenticado();

        // Validar existencia de asiento de apertura
        validacionContableService.validarApertura(TipoAsiento.NORMAL);

        // Buscamos la persona el id
         Persona persona = personaRepository.findById(ventaDTO.getIdPersona())
                 .orElseThrow(()-> new RecursoNoEncontradoException("Persona no encontrada."));
         //Crramos la venta
         Venta venta = new Venta(ventaDTO);
         venta.setPersona(persona);
         venta.setEstado(EstadoVenta.PENDIENTE);
         venta.setUsuario(usuario);

        System.out.println("Estado venta: {}"+ venta.getEstado());

        // Inicializamos los totales en cero por que aun no se han calculado
        venta.setSubTotalVenta(BigDecimal.ZERO);
        venta.setTotalImpuestos(BigDecimal.ZERO);
        venta.setTotalVenta(BigDecimal.ZERO);
        venta.setSaldoAplicado(BigDecimal.ZERO);
        venta.setTotalPagar(BigDecimal.ZERO);

        //actualizarEstadoPorPago(venta);

         //guardamos la venta para obtener ID
        Venta ventaGuardada = ventaRepository.save(venta);

        //Llamamos al metodo guardar detalle venta donde se almacenaran los registros del detalle
        guardarDetalleVenta(ventaDTO, ventaGuardada);

        /* Después de calcular total de la venta llamamos el metodo aplicar
        el saldo a favor si este tiene uno dispnible
         */
        BigDecimal saldoAplicado = BigDecimal.ZERO;

        if (Boolean.TRUE.equals(ventaDTO.getUsarSaldoFavor())) {
            saldoAplicado = aplicarSaldoFavor(ventaGuardada);
        }
        // actualizamos nuestro saldo aplicado
        ventaGuardada.setSaldoAplicado(saldoAplicado);
        //Actualizamos nuestro total a pagar descontando el valor de la venta con el saldo aplicado que se tiene
        ventaGuardada.setTotalPagar(
                ventaGuardada.getTotalVenta().subtract(saldoAplicado)
        );

        /* creamos una condicion donde evaluamos que los pagos no esten vacios y que tienen que ser diferentes
         * de nulos si se cumple la condicion llamamos al metodo guardar venta pago para rgistrar el pago */
        if (ventaDTO.getPagos() != null && !ventaDTO.getPagos().isEmpty()) {
            guardarVentaPago(ventaDTO, ventaGuardada);
        }

        //Llamamos al metodo actualizar estado por pago que nos actualiza en que estado se encuentra la venta.
        actualizarEstadoPorPago(ventaGuardada);

        // Calcular total pagado
        /*BigDecimal totalPagado = venta.getVentaPagos().stream()
                .filter(p -> p.getEstadoPago() == EstadoPago.CONFIRMADO)
                .map(VentaPago::getMonto)
                .reduce(BigDecimal.ZERO, BigDecimal::add);*/

        venta.setEstado(calcularEstadoVenta(ventaGuardada));

        ventaRepository.save(ventaGuardada);

        //Retornamos nuestra variable venta guardada que es la que almacena el guardado de nuestra venta.
        return ventaGuardada;
    }

    // Guardamos nuestro detalle de venta en la base de datos
    public void guardarDetalleVenta(VentaDTO ventaDTO, Venta venta){
        /*Guardado de detalles de venta primero evaluamos si nuestro detalles no esten vacios y no sean nulos
        * si se cumple la condicion entramos al if */
        if(ventaDTO.getDetalles() != null && !ventaDTO.getDetalles().isEmpty()) {
            //Inicializamos nuestra variables  totales en cero.
            BigDecimal subTotalVenta = BigDecimal.ZERO;
            BigDecimal totalImpuestos = BigDecimal.ZERO;
            BigDecimal totalVenta = BigDecimal.ZERO;

            /*Recorremos un for con los detalles de la venta que se van a ir agregando para eso
              creamos una variable de  DetalleVentaDTO  donde iremos alamcenando lo que venga de la
              lista ventaDTO.getDetalles()*/
            for(DetalleVentaDTO detalleVeta : ventaDTO.getDetalles()) {
                /*Creamos una variable de tipo producto donde almacenaremos lo que traiga el metodo findBYId del
                repositorio donde buscamos si existe ese producto en la BD.*/
                Producto producto = productoRepository.findById(detalleVeta.getIdProducto())
                        .orElseThrow(() -> new RecursoNoEncontradoException(
                                "Producto no encontrado: " + detalleVeta.getIdProducto()));

                /*Creamos nuevas variables donde alamcenaremos los valores que llegan de la BD como desde el
                frontEnd por ejemplo precion unitario viene de la bd, la cantidad y el descuento llegan desde el front
                */
                BigDecimal precioUnitario = producto.getPrecioVenta();
                BigDecimal cantidad = BigDecimal.valueOf(detalleVeta.getCantidad());
                BigDecimal descuento = detalleVeta.getDescuento() != null ? detalleVeta.getDescuento() : BigDecimal.ZERO;

                //Caluclamos el subtotal  y lo almacenamos en la variable.
                BigDecimal subTotalLinea = precioUnitario
                        .multiply(cantidad)
                        .subtract(descuento);

                //Caluclamos el porcentaje  y lo almacenamos en la variable.
                BigDecimal porcentajeImpuesto = producto.getImpuesto() != null
                        ? producto.getImpuesto().getPorcentaje()
                        : BigDecimal.ZERO;
                //Caluclamos el impuesto de linea  y lo almacenamos en la variable.
                BigDecimal impuestoLinea = subTotalLinea.multiply(porcentajeImpuesto);
                //Caluclamos el total linea y lo almacenamos en la variable.
                BigDecimal totalLinea = subTotalLinea.add(impuestoLinea);

                System.out.println("Descuento: " + descuento);

                // Acumulamos totales de la venta
                subTotalVenta = subTotalVenta.add(subTotalLinea);
                totalImpuestos = totalImpuestos.add(impuestoLinea);
                totalVenta = totalVenta.add(totalLinea);

                /*Creamos un nuevo objeto de la entidad DetalleVenta y asignamos los valores actualizados para guardar
                en la BD.*/
                DetalleVenta detalle = new DetalleVenta();
                detalle.setCantidad(detalleVeta.getCantidad());
                detalle.setPrecioUnitario(producto.getPrecioVenta());
                detalle.setDescuento(descuento);
                detalle.setSubtotalLinea(subTotalLinea);
                detalle.setImpuestoLinea(impuestoLinea);
                detalle.setTotalLinea(totalLinea);
                detalle.setVenta(venta);
                detalle.setProducto(producto);
                detalle.setImpuesto(producto.getImpuesto());
                detalle.setCostoUnitarioPromedio(producto.getCostoPromedio());
                /*guardamos nuestro detalle en la BD  llamando al repositorio y al metodo save donde le pasamos
                como parametro el objeto detalle*/
                detalleVentaRepository.save(detalle);
            }
            // Actualizamos los totales de la venta
            venta.setSubTotalVenta(subTotalVenta);
            venta.setTotalImpuestos(totalImpuestos);
            venta.setTotalVenta(totalVenta);
            // guardamos nuestros totales en la BD.
            ventaRepository.save(venta);
        }
    }

    //Metodo que nos permite guardar en la BD los datos de la tabla venta Pago
    public void guardarVentaPago(VentaDTO ventaDTO, Venta venta){

        //Validamos que la lista de pagos este vacia o este nula si se cumple no retornamos nada.
        if (ventaDTO.getPagos() == null || ventaDTO.getPagos().isEmpty()) {
            return;
        }
        //validamos si el estado de la venta es pagada lazamos un mensaje
        if (venta.getEstado() == EstadoVenta.PAGADA) {
            throw new IllegalStateException("La venta ya está pagada");
        }
        //validamos si el estado de la venta es pagada lazamos un mensaje
        if (venta.getEstado() == EstadoVenta.CANCELADA) {
            throw new IllegalStateException("La venta está cancelada");
        }

        /*Recorremos un for con los pagos de la venta que se van a ir agregando para eso
              creamos una variable de VentaPagoDTO  donde iremos alamcenando lo que venga de la
              lista ventaDTO.getPagos()*/
        for (VentaPagoDTO pagoDTO : ventaDTO.getPagos()) {

            //validamos si el estado es nulo mandamo un mensaje
            if (pagoDTO.getEstadoPago() == null) {
                throw new IllegalArgumentException("El estado del pago es obligatorio");
            }
            //validamos que el monto no sea menor cero.
            if (pagoDTO.getMonto().compareTo(BigDecimal.ZERO) <= 0) {
                throw new IllegalArgumentException("El monto del pago debe ser mayor a 0");
            }

            // Creamos las reglas de negocio
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

            /*Creamos un nuevo objeto de la entidad VentaPago y asignamos los valores actualizados para guardar
                en la BD.*/
            VentaPago pago = new VentaPago();
            pago.setVenta(venta);
            pago.setEstadoPago(pagoDTO.getEstadoPago());
            pago.setMonto(pagoDTO.getMonto());
            pago.setFechaPago(LocalDateTime.now());
            /*guardamos nuestro ventapago en la BD  llamando al repositorio y al metodo save donde le pasamos
                como parametro el objeto pago*/
            ventaPagoRepository.save(pago);
            //agregamos en nuestra lista de pagos llamada VentaPagos los valores que vienen en el objeto pago.
            venta.getVentaPagos().add(pago);
        }
        /*Actualizamos nuestro estado en la tabla venta llamando al metodo calcular estado de venta y
         pasando como praemtro el objeto venta*/
        venta.setEstado(calcularEstadoVenta(venta));
        /*guardamos nuestro venta en la BD  para actualizar el estado de la venta.*/
        ventaRepository.save(venta);
    }

    /*Este metodo nos ayuda a calcular en que momento se encuentra nuestro estado de venta
    * dependiendo del totalpagado*/
    private EstadoVenta calcularEstadoVenta(Venta venta) {

        //Asignmos nuestro listado de pagos en la variable totalpagado filtrando
        // por el estado que se enctre confirmado
        BigDecimal totalPagado = venta.getVentaPagos().stream()
                .filter(p -> p.getEstadoPago() == EstadoPago.CONFIRMADO)
                .map(VentaPago::getMonto)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        //Creamos una variable totalventa donde le pasamos el valor total a pagar si tiene saldo pendiente
        BigDecimal totalVenta =(venta.getTotalPagar());

        //Validamos si el total pagado es igual a cero por defecto el estado seria pendiente
        if (totalPagado.compareTo(BigDecimal.ZERO) == 0) {
            return EstadoVenta.PENDIENTE;
        }
        //Validamos si el total pagado es menor al totalventa el estado seria parcial
        if (totalPagado.compareTo(totalVenta) < 0) {
            return EstadoVenta.PARCIAL;
        }
        //En caso contrario el estado seria pagada
        return EstadoVenta.PAGADA;
    }

    //Este metodo nos permite cancelar una venta siempre y cuando no este pagada o confirmada
    @Override
    public void cancelarVenta(int idVenta) {
        //Consutlamos la venta y la almacenamos en una variable llamada venta
        Venta venta = ventaRepository.findById(idVenta)
                .orElseThrow(() -> new RecursoNoEncontradoException("Venta no encontrada"));

        /*Validamos si el estado es igual a pagado o estado confirmada no se puede cancelar la venta*/
        if (venta.getEstado() == EstadoVenta.PAGADA || venta.getEstado() == EstadoVenta.CONFIRMADA) {
            throw new IllegalStateException("No se puede cancelar una venta confirmada o pagada");
        }
        //Si no se cumpla condicion de arriba actualizamos nuestro estado como cancelada
        venta.setEstado(EstadoVenta.CANCELADA);
        //guardamos nuestro nuevo estado en la tabla venta.
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


    /*ESte metodo nos permite confirmar la venta actializnado el estado de venta */
    public void confirmarVenta(int idVenta) {

        //consultamos si existe la venta
        Venta venta = ventaRepository.findById(idVenta)
                .orElseThrow(() -> new RecursoNoEncontradoException("Venta no encontrada"));

        //validamos si el estado es diferente de pendiente mandamos un mensaje donde no se
        // puede confirmar la venta
        if (venta.getEstado() != EstadoVenta.PENDIENTE) {
            throw new IllegalStateException("La venta ya fue confirmada o no está en estado válido");
        }

        //creamos un for y dentro creamos un varavbel de detalle venta  donde alacenaremos
        // los datos obteneidos en nuestra lista detalleventas.
        for (DetalleVenta d : venta.getDetalleVentas()) {
            //Llamamos al metodo registrar movimniento que sera el encargado de realizar el
            // descuento del invetario
            registrarMovimiento(d, venta);
        }
        //Actualizamos nuestro estado venta como confirmada
        venta.setEstado(EstadoVenta.CONFIRMADA);

        ventaContableService.registrarAsientoVenta(venta);
        ventaContableService.registrarCostoVenta(venta);
        //guardamos nuestro nuevo estado venta.
        ventaRepository.save(venta);
    }

    // Este metodo nos permite actualizar el estado venta por pago
    private void actualizarEstadoPorPago(Venta venta) {
        //Creamos una variable llamada estadopago donde alcenara los valores que llegan desde el metodo
        // calcularEstadoVenta
        EstadoVenta estadoPago = calcularEstadoVenta(venta);

        /*con esta variable estado pago hacemos una validacion donde comparamos
        si el estado pago es igual a parcial entonces mantenga el estado igual o sea parcial en caso cotrario
        cambie el estado a pagada*/
        if (estadoPago == EstadoVenta.PARCIAL) {
            venta.setEstado(EstadoVenta.PARCIAL);
        }
        else if (estadoPago == EstadoVenta.PAGADA) {
            venta.setEstado(EstadoVenta.PAGADA);
        }
    }

    //ESte metodo permite realizar el registro del  movimiento de inventario y descontar el stock
    // de la tabla producto
    public void registrarMovimiento(DetalleVenta detalle, Venta venta) {

        //logs informativos
        log.info("=== INICIO registrarMovimiento ===");

        log.info("Venta ID: {}, Estado: {}", venta.getIdVenta(), venta.getEstado());
        log.info("Detalle -> Producto ID: {}, Cantidad: {}",
                detalle.getProducto().getIdProducto(), detalle.getCantidad());

        //Consultamos que exista nuestra producto
        Producto producto = productoRepository.findById(detalle.getProducto().getIdProducto())
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        log.info("Producto encontrado -> ID: {}, Stock: {}, StockReservado: {}",
                producto.getIdProducto(), producto.getStock());

        //Validamos que la venta sea diferente a pendiente
        if (venta.getEstado() != EstadoVenta.PENDIENTE) {
            log.error(" Venta no está PAGADA");
            throw new IllegalStateException("La venta no esta en estado pendiente.");
        }
        //Validamos que el stock no sea inferior a la cantidad ingresada
        if (producto.getStock() < detalle.getCantidad()) {
            log.error(" Stock reservado insuficiente");
            throw new IllegalStateException(
                    "La cantidad reservada es insuficiente"
            );
        }

        /*Creamos un nuevo objeto de la entidad movimientoinventario y asignamos los valores actualizados para guardar
                en la BD.*/
        MovimientoInventario movimiento = new MovimientoInventario();

        movimiento.setTipoMovimiento(TipoMovimiento.SALIDA);
        movimiento.setOrigenMovimiento(OrigenMovimiento.VENTA);
        movimiento.setCantidad(detalle.getCantidad());
        movimiento.setObservacion("Salida del producto por venta # " + venta.getIdVenta());
        movimiento.setProducto(producto);
        movimiento.setProveedor(null);
        movimiento.setFechaMovimiento(LocalDateTime.now());

        //  LOG CLAVE (ANTES DEL SAVE)
        log.info("Movimiento a guardar:");
        log.info("  Tipo: {}", movimiento.getTipoMovimiento());
        log.info("  Origen: {}", movimiento.getOrigenMovimiento());
        log.info("  Cantidad: {}", movimiento.getCantidad());
        log.info("  Producto ID: {}",
                movimiento.getProducto() != null ? movimiento.getProducto().getIdProducto() : "NULL");
        log.info("  Proveedor: {}", movimiento.getProveedor());

        //guardamos nuestro movimiento inventario en la BD
        movimientoInventarioRepository.save(movimiento);

        log.info("✅ Movimiento guardado correctamente");

        //desde esta linea realizamos el descuento del stock desde la tabla producto
        producto.setStock(producto.getStock() - detalle.getCantidad());
        //guardamos nuestro productostock actualizado.
        productoRepository.save(producto);

        log.info("=== FIN registrarMovimiento ===");
    }

    //Este metodo nos permite aplicar los saldos a favor de los clientes cuando se realiza una devolucion
    private BigDecimal aplicarSaldoFavor(Venta venta) {

        //Creamos una variable llamada persona donde almacenaremos el valor que viene de la
        // entidad venta persona
        Persona persona = venta.getPersona();
        //Creamos una variable llamada saldo disponible donde almacenaremos el valor que viene de la
        // entidad persona saldo a favor
        BigDecimal saldoDisponible = persona.getSaldoFavor();

        //Validamos is el saldo disponible es igual o menor a cero entonces regresa cero
        if (saldoDisponible == null || saldoDisponible.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }
        //Creamos una variable llamada totalVenta donde almacenaremos el valor que viene de la
        // entidad venta totalVenta
        BigDecimal totalVenta = venta.getTotalVenta();
        //Creamos una variable llamada saldousado donde almacenaremos el valor del saldo disponible
        // teniendo encunta de que no se exceda el valor del saldo disponible con el total de la venta.
        BigDecimal saldoUsado = saldoDisponible.min(totalVenta);

        // Descontar saldo
        persona.setSaldoFavor(
                saldoDisponible.subtract(saldoUsado)
        );

        //actualizamos nuestra tabla persona el sado a favor
        personaRepository.save(persona);
        // Registrar movimiento saldo persona
        MovimientoSaldoPersona mov = new MovimientoSaldoPersona();
        mov.setPersona(persona);
        mov.setMonto(saldoUsado);
        mov.setTipoMovimiento(TipoMovimientoSaldo.SALIDA);
        mov.setConcepto("Aplicación del saldo a favor en la venta #" + venta.getIdVenta());
        mov.setFechaMovimiento(LocalDateTime.now());

        //guardamos el movimiento del saldo persona
        movimientoSaldoPersonaRepository.save(mov);

        //retornamos el saldo usado.
        return saldoUsado;
    }



}
