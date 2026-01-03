package com.cristhian.SistemaInventario.ServicioImplement;

import com.cristhian.SistemaInventario.DTO.DevolucionDTO;
import com.cristhian.SistemaInventario.Excepciones.RecursoNoEncontradoException;
import com.cristhian.SistemaInventario.Modelo.*;
import com.cristhian.SistemaInventario.Repositorio.*;
import com.cristhian.SistemaInventario.Service.IDevolcuonService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class DevolucionServiceImpl implements IDevolcuonService {

    private final DevolucionRepository devolucionRepository;
    private final ProductoRepository productoRepository;
    private final MovimientoInventarioRepository movimientoInventarioRepository;
    private final VentaRepository ventaRepository;
    private final DetalleVentaRepository detalleVentaRepository;
    private final PersonaRepository personaRepository;

    public DevolucionServiceImpl(DevolucionRepository devolucionRepository, ProductoRepository productoRepository,
                                 MovimientoInventarioRepository movimientoInventarioRepository,
                                 VentaRepository ventaRepository,
                                 DetalleVentaRepository detalleVentaRepository,
                                 PersonaRepository personaRepository) {
        this.devolucionRepository = devolucionRepository;
        this.productoRepository = productoRepository;
        this.movimientoInventarioRepository = movimientoInventarioRepository;
        this.ventaRepository = ventaRepository;
        this.detalleVentaRepository = detalleVentaRepository;
        this.personaRepository = personaRepository;
    }

    @Override
    @Transactional
    public Devolucion crearDevolucion(DevolucionDTO dto) {

        // =========================
        // 1️⃣ Validaciones básicas
        // =========================
        if (dto.getCantidad() <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor a cero");
        }

        if (dto.getTipoDevolucion() == TipoDevolucion.CAMBIO &&
                dto.getIdProductoCambio() == null) {
            throw new IllegalArgumentException(
                    "Debe especificar productoCambio cuando el tipo es CAMBIO"
            );
        }

        // =========================
        // 2️⃣ Obtener entidades base
        // =========================
        Producto producto = productoRepository.findById(dto.getIdProducto())
                .orElseThrow(() ->
                        new RecursoNoEncontradoException(
                                "Producto no encontrado: " + dto.getIdProducto()
                        ));

        Venta venta = ventaRepository.findById(dto.getIdVenta())
                .orElseThrow(() ->
                        new RecursoNoEncontradoException(
                                "Venta no encontrada: " + dto.getIdVenta()
                        ));

        Producto productoCambio = null;
        if (dto.getIdProductoCambio() != null) {
            productoCambio = productoRepository.findById(dto.getIdProductoCambio())
                    .orElseThrow(() ->
                            new RecursoNoEncontradoException(
                                    "Producto de cambio no encontrado"
                            ));
        }

        Persona persona = venta.getPersona();

        // =========================
        // 3️⃣ Crear y guardar devolución
        // =========================
        Devolucion devolucion = new Devolucion();
        devolucion.setMotivo(dto.getMotivo());
        devolucion.setCantidad(dto.getCantidad());
        devolucion.setTipoDevolucion(dto.getTipoDevolucion());
        devolucion.setFechaDevolucion(LocalDateTime.now());
        devolucion.setProducto(producto);
        devolucion.setProductoCambio(productoCambio);
        devolucion.setVenta(venta);
        devolucion.setActivo(true);
        // =========================
        // 6️⃣ Calcular valor devuelto
        // =========================
        BigDecimal valorDevuelto = calcularValorDevuelto(devolucion);
        devolucion.setValorDevuelto(valorDevuelto);

        Devolucion devGuardada = devolucionRepository.save(devolucion);

        // =========================
        // 4️⃣ Inventario → ENTRADA
        // =========================
        producto.setStock(producto.getStock() + devolucion.getCantidad());
        productoRepository.save(producto);

        MovimientoInventario entrada = new MovimientoInventario();
        entrada.setProducto(producto);
        entrada.setCantidad(devolucion.getCantidad());
        entrada.setTipoMovimiento(TipoMovimiento.ENTRADA);
        entrada.setOrigenMovimiento(OrigenMovimiento.DEVOLUCION);
        entrada.setObservacion("Devolución - " + devolucion.getTipoDevolucion());
        entrada.setFechaMovimiento(devolucion.getFechaDevolucion());
        movimientoInventarioRepository.save(entrada);

        // =========================
        // 5️⃣ Inventario → SALIDA (CAMBIO)
        // =========================
        if (devolucion.getTipoDevolucion() == TipoDevolucion.CAMBIO) {

            if (productoCambio.getStock() < devolucion.getCantidad()) {
                throw new IllegalStateException(
                        "Stock insuficiente para el producto de cambio"
                );
            }

            productoCambio.setStock(
                    productoCambio.getStock() - devolucion.getCantidad()
            );
            productoRepository.save(productoCambio);

            MovimientoInventario salida = new MovimientoInventario();
            salida.setProducto(productoCambio);
            salida.setCantidad(devolucion.getCantidad());
            salida.setTipoMovimiento(TipoMovimiento.SALIDA);
            salida.setObservacion("Entrega por cambio de devolución");
            salida.setFechaMovimiento(devolucion.getFechaDevolucion());
            movimientoInventarioRepository.save(salida);
        }


        System.out.println("*************************** Valor devuelto "+valorDevuelto);
        // =========================
        // 7️⃣ Sumar saldo a favor
        // =========================
        persona.setSaldoFavor(
                persona.getSaldoFavor().add(valorDevuelto)
        );
        personaRepository.save(persona);

        // =========================
        // 8️⃣ Recalcular estado de la venta
        // =========================
        recalcularEstadoVenta(venta);

        return devGuardada;
    }


    @Override
    public List<Devolucion> listarDevoluciones() {
        return  devolucionRepository.findAll();
    }

    @Override
    public Optional<Devolucion> buscarDevolucionPorId(int id) {
        return devolucionRepository.findById(id);
    }

    @Override
    public void eliminarDevolucion(int id) {
        Devolucion devolucion = devolucionRepository.findById(id).orElse(null);
        if (devolucion != null) {
            devolucion.setActivo(false); // solo la marcamos como inactiva
            devolucionRepository.save(devolucion);
        }
    }

    private void recalcularEstadoVenta(Venta venta) {

        BigDecimal totalVenta = venta.getTotalVenta();

        // Total devuelto
        BigDecimal totalDevuelto = venta.getDevoluciones().stream()
                .map(dev -> dev.getValorDevuelto())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalPagado = venta.getVentaPagos().stream()
                .filter(p -> p.getEstadoPago() == EstadoPago.CONFIRMADO)
                .map(VentaPago::getMonto)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal nuevoTotal = totalVenta.subtract(totalDevuelto);

        venta.setTotalVenta(nuevoTotal);

        if (totalDevuelto.compareTo(totalVenta) >= 0) {
            venta.setEstado(EstadoVenta.DEVUELTA_TOTAL);
        } else {
            venta.setEstado(EstadoVenta.DEVUELTA_PARCIAL);
        }

        ventaRepository.save(venta);
    }

    private BigDecimal calcularValorDevuelto(Devolucion devolucion) {

        DetalleVenta detalle = detalleVentaRepository
                .findByVentaAndProducto(devolucion.getVenta(), devolucion.getProducto())
                .orElseThrow(() -> new RuntimeException("Detalle de venta no encontrado"));

        if (devolucion.getCantidad() <= 0) {
            throw new IllegalArgumentException("La cantidad a devolver debe ser mayor a cero");
        }

        if (devolucion.getCantidad() > detalle.getCantidad()) {
            throw new IllegalArgumentException("No puede devolver más unidades de las vendidas");
        }

        // 🔹 Total pagado por esa línea (INCLUYE IVA)
        BigDecimal totalLinea = detalle.getTotalLinea();

        // 🔹 Valor unitario real (con IVA)
        BigDecimal valorUnitarioConIva = totalLinea.divide(
                BigDecimal.valueOf(detalle.getCantidad()),
                2,
                RoundingMode.HALF_UP
        );

        return valorUnitarioConIva.multiply(
                BigDecimal.valueOf(devolucion.getCantidad())
        );
    }



}
