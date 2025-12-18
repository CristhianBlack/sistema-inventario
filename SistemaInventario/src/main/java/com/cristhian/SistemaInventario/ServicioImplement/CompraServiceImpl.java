package com.cristhian.SistemaInventario.ServicioImplement;

import com.cristhian.SistemaInventario.DTO.CompraDTO;
import com.cristhian.SistemaInventario.DTO.DetalleCompraDTO;
import com.cristhian.SistemaInventario.Excepciones.RecursoNoEncontradoException;
import com.cristhian.SistemaInventario.Modelo.*;
import com.cristhian.SistemaInventario.Repositorio.*;
import com.cristhian.SistemaInventario.Service.ICompraService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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


    public CompraServiceImpl(CompraRepository compraRepository, ProveedorRepository proveedorRepository,
                             DetalleCompraRepository detalleCompraRepository,ProductoRepository productoRepository,
                             MovimientoInventarioRepository movimientoInventarioRepository) {
        this.compraRepository = compraRepository;
        this.proveedorRepository = proveedorRepository;
        this.detalleCompraRepository = detalleCompraRepository;
        this.productoRepository = productoRepository;
        this.movimientoInventarioRepository = movimientoInventarioRepository;
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

        // Guardar compra para obtener ID
        Compra compraGuardada = compraRepository.save(compra);

        // Guardado de detalles
        if (compraDTO.getDetalles() != null && !compraDTO.getDetalles().isEmpty()) {

            for (DetalleCompraDTO d : compraDTO.getDetalles()) {

                Producto producto = productoRepository.findById(d.getIdProducto())
                        .orElseThrow(() -> new RecursoNoEncontradoException(
                                "Producto no encontrado: " + d.getIdProducto()
                        ));

                DetalleCompra detalle = new DetalleCompra();
                detalle.setCantidad(d.getCantidad());
                detalle.setPrecioUnitario(d.getPrecioUnitario());
                detalle.setSubTotal(d.getSubTotal());
                detalle.setProducto(producto);
                detalle.setCompra(compraGuardada);

                detalleCompraRepository.save(detalle);

                // LLamamos al metdo que hace el registro automatico del movimiento de inventario.
                registrarMovimiento(detalle, compraGuardada);
            }
        }

        return compraGuardada;
    }


    @Override
    public Compra actualizarCompra(int id, CompraDTO compraDTO) {
        // Validar si existe la copra con ese ID
        Compra compraExistente = compraRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("La compra no existe"));

        // --- Buscar relaciones ---
        Proveedor proveedor = proveedorRepository.findById(compraDTO.getIdProveedor())
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe el proveedor seleccionado."));

        compraExistente.setTotal(compraDTO.getTotal());

        // Asignar relaciones
        compraExistente.setProveedor(proveedor);

        //Guardar Cambios
        return compraRepository.save(compraExistente);
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

        movimientoInventarioRepository.save(movimiento);

        //2. Buscamos el producto si existe.
        Producto producto = productoRepository.findById(detalle.getProducto().getIdProducto())
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        //3. Actualizamos nuestro stock y precio de compra de la tabla producto y el precio.
        producto.setStock(detalle.getCantidad());
        producto.setPrecioCompra(detalle.getPrecioUnitario());

        productoRepository.save(producto);
    }
}
