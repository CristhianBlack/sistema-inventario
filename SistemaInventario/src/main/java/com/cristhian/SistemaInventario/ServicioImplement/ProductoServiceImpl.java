/*package com.cristhian.SistemaInventario.ServicioImplement;

import com.cristhian.SistemaInventario.DTO.ProductoDTO;
import com.cristhian.SistemaInventario.Excepciones.DuplicadoException;
import com.cristhian.SistemaInventario.Excepciones.RecursoNoEncontradoException;
import com.cristhian.SistemaInventario.Mensaje.Mensaje;
import com.cristhian.SistemaInventario.Modelo.*;
import com.cristhian.SistemaInventario.Repositorio.*;
import com.cristhian.SistemaInventario.Service.IProductoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ProductoServiceImpl implements IProductoService {

    private static final Logger logger = LoggerFactory.getLogger(CiudadServiceImpl.class);

    private final ProductoRepository productoRepository;
    private final CategoriaRepository categoriaRepository;
    private final UnidadMedidaRepository unidadMedidaRepository;
    private final ProveedorRepository proveedorRepository;
    private final ImpuestoRepository impuestoRepository;

    public ProductoServiceImpl(ProductoRepository productoRepository,
                               CategoriaRepository categoriaRepository,
                               ProveedorRepository proveedorRepository,
                               UnidadMedidaRepository unidadMedidaRepository,
                               ImpuestoRepository impuestoRepository) {
        this.productoRepository = productoRepository;
        this.categoriaRepository = categoriaRepository;
        this.unidadMedidaRepository = unidadMedidaRepository;
        this.proveedorRepository = proveedorRepository;
        this.impuestoRepository = impuestoRepository;
    }

    // listamos todos los productos creados.
    @Override
    public List<Producto> listarProductoActivo(){
        return productoRepository.findByActivoTrue();
    }

    // Buscamos el producto por id
    @Override
    public Optional<Producto> buscarProducto(int id){
        return productoRepository.findById(id);
    }

    //este metodo lo usamos para crear el usuario validando de que este no exista aun y si existe manda un mensaje

    @Override
    public Producto guardarProducto(ProductoDTO productoDTO) {

        // --- Validar duplicado ---
        Optional<Producto> productoExistente =
                productoRepository.findByNombreProductoIgnoreCase(productoDTO.getNombreProducto().trim());

        if (productoExistente.isPresent()) {
            Producto producto = productoExistente.get();

            if (!producto.isActivo()) {
                logger.info("Producto encontrado inactivo. Reactivando...");
                producto.setActivo(true);
                return productoRepository.save(producto);
            }

            throw new DuplicadoException("Ya existe un producto con ese nombre.");
        }

        // --- Buscar relaciones ---
        Categoria categoria = categoriaRepository.findById(productoDTO.getIdCategoria())
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe esa categoría."));

        UnidadMedida unidad = unidadMedidaRepository.findById(productoDTO.getIdUnidadMedida())
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe la unidad de medida seleccionada."));

        Proveedor proveedor = proveedorRepository.findById(productoDTO.getIdProveedor())
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe el proveedor seleccionado."));

        Impuesto impuesto = impuestoRepository.findById(productoDTO.getIdImpuesto())
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe el impuesto seleccionado."));

        // --- Crear producto manualmente ---
        Producto producto = new Producto();
        producto.setCodigoProducto(productoDTO.getCodigoProducto());
        producto.setNombreProducto(productoDTO.getNombreProducto().trim());
        producto.setPrecioCompra(productoDTO.getPrecioCompra());
        producto.setPrecioVenta(productoDTO.getPrecioVenta());
        producto.setDescripcion(productoDTO.getDescripcion());
        producto.setStock(0);
        producto.setStockMinimo(0);


        // Asignar relaciones
        producto.setCategoria(categoria);
        producto.setUnidadMedida(unidad);
        producto.setProveedor(proveedor);
        producto.setImpuesto(impuesto);

        return productoRepository.save(producto);
    }


    // Este metodo lo usamos para actualizar el producto siempre y cuando este exista.
    @Override
    public Producto actualizarProducto(int id, ProductoDTO productoDTO) {

        // 1. Validar que el producto exista
        Producto productoExistente = productoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe ese producto."));

        String nuevoNombre = productoDTO.getNombreProducto().trim();

        // 2. Validar que el nombre no esté repetido en OTRO producto
        Optional<Producto> optionalProducto =
                productoRepository.findByNombreProductoIgnoreCase(nuevoNombre);

        // Validar duplicado
        if (optionalProducto.isPresent()) {
            Producto productoConNombre = optionalProducto.get();

            // Si el nombre pertenece a OTRO producto → error
            if (productoConNombre.getIdProducto() != productoExistente.getIdProducto()) {
                throw new DuplicadoException("Ya existe otro producto con ese nombre.");
            }
        }

            // 3. Validar y obtener las relaciones
            Categoria categoria = categoriaRepository.findById(productoDTO.getIdCategoria())
                    .orElseThrow(() -> new RecursoNoEncontradoException("No existe esa categoría."));

            UnidadMedida unidad = unidadMedidaRepository.findById(productoDTO.getIdUnidadMedida())
                    .orElseThrow(() -> new RecursoNoEncontradoException("No existe la unidad de medida seleccionada."));

            Proveedor proveedor = proveedorRepository.findById(productoDTO.getIdProveedor())
                    .orElseThrow(() -> new RecursoNoEncontradoException("No existe el proveedor seleccionado."));

            // 4. Actualizar datos del producto
            productoExistente.setCodigoProducto(productoDTO.getCodigoProducto());
            productoExistente.setNombreProducto(nuevoNombre);
            productoExistente.setPrecioCompra(productoDTO.getPrecioCompra());
            productoExistente.setPrecioVenta(productoDTO.getPrecioVenta());
            productoExistente.setDescripcion(productoDTO.getDescripcion());

            // 5. Asignar relaciones
            productoExistente.setCategoria(categoria);
            productoExistente.setUnidadMedida(unidad);
            productoExistente.setProveedor(proveedor);

            // 6. Guardar cambios
            return productoRepository.save(productoExistente);
        }

    @Override
    public void borrarProducto(int id){
        Producto producto = productoRepository.findById(id).orElse(null);
        if (producto != null){
            producto.setActivo(false);
            productoRepository.save(producto);
        }
    }
}*/

package com.cristhian.SistemaInventario.ServicioImplement;

import com.cristhian.SistemaInventario.DTO.ProductoDTO;
import com.cristhian.SistemaInventario.Excepciones.DuplicadoException;
import com.cristhian.SistemaInventario.Excepciones.RecursoNoEncontradoException;
import com.cristhian.SistemaInventario.Mensaje.Mensaje;
import com.cristhian.SistemaInventario.Modelo.*;
import com.cristhian.SistemaInventario.Repositorio.*;
import com.cristhian.SistemaInventario.Service.IProductoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;

/**
 * Servicio encargado de la gestión de productos.
 * Incluye creación, actualización, eliminación lógica
 * y cálculo de costos promedio por compras.
 */
@Service
@Transactional
public class ProductoServiceImpl implements IProductoService {

    /*private static final Logger logger = LoggerFactory.getLogger(CiudadServiceImpl.class);

    private final ProductoRepository productoRepository;
    private final CategoriaRepository categoriaRepository;
    private final UnidadMedidaRepository unidadMedidaRepository;
    private final ProveedorRepository proveedorRepository;
    private final ImpuestoRepository impuestoRepository;

    public ProductoServiceImpl(ProductoRepository productoRepository,
                               CategoriaRepository categoriaRepository,
                               ProveedorRepository proveedorRepository,
                               UnidadMedidaRepository unidadMedidaRepository,
                               ImpuestoRepository impuestoRepository) {
        this.productoRepository = productoRepository;
        this.categoriaRepository = categoriaRepository;
        this.unidadMedidaRepository = unidadMedidaRepository;
        this.proveedorRepository = proveedorRepository;
        this.impuestoRepository = impuestoRepository;
    }

    // listamos todos los productos creados.
    @Override
    public List<Producto> listarProductoActivo(){
        return productoRepository.findByActivoTrue();
    }

    // Buscamos el producto por id
    @Override
    public Optional<Producto> buscarProducto(int id){
        return productoRepository.findById(id);
    }

    //este metodo lo usamos para crear el usuario validando de que este no exista aun y si existe manda un mensaje

    @Override
    public Producto guardarProducto(ProductoDTO productoDTO) {

        // --- Validar duplicado ---
        Optional<Producto> productoExistente =
                productoRepository.findByNombreProductoIgnoreCase(productoDTO.getNombreProducto().trim());

        if (productoExistente.isPresent()) {
            Producto producto = productoExistente.get();

            if (!producto.isActivo()) {
                logger.info("Producto encontrado inactivo. Reactivando...");
                producto.setActivo(true);
                return productoRepository.save(producto);
            }

            throw new DuplicadoException("Ya existe un producto con ese nombre.");
        }

        // --- Buscar relaciones ---
        Categoria categoria = categoriaRepository.findById(productoDTO.getIdCategoria())
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe esa categoría."));

        UnidadMedida unidad = unidadMedidaRepository.findById(productoDTO.getIdUnidadMedida())
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe la unidad de medida seleccionada."));

        Proveedor proveedor = proveedorRepository.findById(productoDTO.getIdProveedor())
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe el proveedor seleccionado."));

        Impuesto impuesto = impuestoRepository.findById(productoDTO.getIdImpuesto())
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe el impuesto seleccionado."));

        // --- Crear producto manualmente ---
        Producto producto = new Producto();
        producto.setNombreProducto(productoDTO.getNombreProducto().trim());
        producto.setPrecioCompra(BigDecimal.ZERO);
        producto.setDescripcion(productoDTO.getDescripcion());
        producto.setStock(0);
        producto.setStockMinimo(productoDTO.getStockMinimo());
        producto.setCostoPromedio(BigDecimal.ZERO);


        // Asignar relaciones
        producto.setCategoria(categoria);
        producto.setUnidadMedida(unidad);
        producto.setProveedor(proveedor);
        producto.setImpuesto(impuesto);

        Producto productoGuardado = productoRepository.save(producto);

         if (productoGuardado.getCodigoProducto() == null) {
        String codigo = String.format("PRO%04d", productoGuardado.getIdProducto());
        productoGuardado.setCodigoProducto(codigo);
        productoRepository.save(productoGuardado);
        }

        return productoGuardado;
    }


    // Este metodo lo usamos para actualizar el producto siempre y cuando este exista.
    @Override
    public Producto actualizarProducto(int id, ProductoDTO productoDTO) {

        // 1. Validar que el producto exista
        Producto productoExistente = productoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe ese producto."));

        String nuevoNombre = productoDTO.getNombreProducto().trim();

        // 2. Validar que el nombre no esté repetido en OTRO producto
        Optional<Producto> optionalProducto =
                productoRepository.findByNombreProductoIgnoreCase(nuevoNombre);

        // Validar duplicado
        if (optionalProducto.isPresent()) {
            Producto productoConNombre = optionalProducto.get();

            // Si el nombre pertenece a OTRO producto → error
            if (productoConNombre.getIdProducto() != productoExistente.getIdProducto()) {
                throw new DuplicadoException("Ya existe otro producto con ese nombre.");
            }
        }

            // 3. Validar y obtener las relaciones
            Categoria categoria = categoriaRepository.findById(productoDTO.getIdCategoria())
                    .orElseThrow(() -> new RecursoNoEncontradoException("No existe esa categoría."));

            UnidadMedida unidad = unidadMedidaRepository.findById(productoDTO.getIdUnidadMedida())
                    .orElseThrow(() -> new RecursoNoEncontradoException("No existe la unidad de medida seleccionada."));

            Proveedor proveedor = proveedorRepository.findById(productoDTO.getIdProveedor())
                    .orElseThrow(() -> new RecursoNoEncontradoException("No existe el proveedor seleccionado."));

            // 4. Actualizar datos del producto
            productoExistente.setNombreProducto(nuevoNombre);
            productoExistente.setDescripcion(productoDTO.getDescripcion());
            productoExistente.setStockMinimo(productoDTO.getStockMinimo());

            // 5. Asignar relaciones
            productoExistente.setCategoria(categoria);
            productoExistente.setUnidadMedida(unidad);
            productoExistente.setProveedor(proveedor);

            // 6. Guardar cambios
            return productoRepository.save(productoExistente);
        }

    @Override
    public void borrarProducto(int id){
        Producto producto = productoRepository.findById(id).orElse(null);
        if (producto != null){
            producto.setActivo(false);
            productoRepository.save(producto);
        }
    }

    public void aplicarCompra(
            Producto producto,
            int cantidad,
            BigDecimal precioUnitario
    ) {
        int stockAnterior = producto.getStock();

        BigDecimal costoAnterior =
                producto.getCostoPromedio() != null
                        ? producto.getCostoPromedio()
                        : BigDecimal.ZERO;

        BigDecimal valorActual =
                costoAnterior.multiply(BigDecimal.valueOf(stockAnterior));

        BigDecimal valorCompra =
                precioUnitario.multiply(BigDecimal.valueOf(cantidad));

        int nuevoStock = stockAnterior + cantidad;

        BigDecimal nuevoCostoPromedio = valorActual
                .add(valorCompra)
                .divide(
                        BigDecimal.valueOf(nuevoStock),
                        2,
                        RoundingMode.HALF_UP
                );

        producto.setStock(nuevoStock);
        producto.setCostoPromedio(nuevoCostoPromedio);

        productoRepository.save(producto);
    }*/

    // Logger para trazabilidad y depuración
    private static final Logger logger =
            LoggerFactory.getLogger(CiudadServiceImpl.class);

    // Repositorios necesarios
    private final ProductoRepository productoRepository;
    private final CategoriaRepository categoriaRepository;
    private final UnidadMedidaRepository unidadMedidaRepository;
    private final ProveedorRepository proveedorRepository;
    private final ImpuestoRepository impuestoRepository;

    public ProductoServiceImpl(ProductoRepository productoRepository,
                               CategoriaRepository categoriaRepository,
                               ProveedorRepository proveedorRepository,
                               UnidadMedidaRepository unidadMedidaRepository,
                               ImpuestoRepository impuestoRepository) {
        this.productoRepository = productoRepository;
        this.categoriaRepository = categoriaRepository;
        this.unidadMedidaRepository = unidadMedidaRepository;
        this.proveedorRepository = proveedorRepository;
        this.impuestoRepository = impuestoRepository;
    }

    /**
     * Listar únicamente los productos activos.
     */
    @Override
    public List<Producto> listarProductoActivo(){
        return productoRepository.findByActivoTrue();
    }

    /**
     * Buscar un producto por su ID.
     */
    @Override
    public Optional<Producto> buscarProducto(int id){
        return productoRepository.findById(id);
    }

    /**
     * Crear un producto validando que no exista previamente.
     * Si existe y está inactivo, se reactiva.
     */
    @Override
    public Producto guardarProducto(ProductoDTO productoDTO) {

        // -----------------------------
        // Validar producto duplicado
        // -----------------------------
        Optional<Producto> productoExistente =
                productoRepository.findByNombreProductoIgnoreCase(
                        productoDTO.getNombreProducto().trim()
                );

        if (productoExistente.isPresent()) {
            Producto producto = productoExistente.get();

            // Si existe pero está inactivo → reactivar
            if (!producto.isActivo()) {
                logger.info("Producto encontrado inactivo. Reactivando...");
                producto.setActivo(true);
                return productoRepository.save(producto);
            }

            // Si existe y está activo → error
            throw new DuplicadoException("Ya existe un producto con ese nombre.");
        }

        // -----------------------------
        // Obtener entidades relacionadas
        // -----------------------------
        Categoria categoria = categoriaRepository.findById(productoDTO.getIdCategoria())
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe esa categoría."));

        UnidadMedida unidad = unidadMedidaRepository.findById(productoDTO.getIdUnidadMedida())
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe la unidad de medida seleccionada."));

        Proveedor proveedor = proveedorRepository.findById(productoDTO.getIdProveedor())
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe el proveedor seleccionado."));

        Impuesto impuesto = impuestoRepository.findById(productoDTO.getIdImpuesto())
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe el impuesto seleccionado."));

        // -----------------------------
        // Crear producto
        // -----------------------------
        Producto producto = new Producto();
        producto.setNombreProducto(productoDTO.getNombreProducto().trim());
        producto.setPrecioCompra(BigDecimal.ZERO);
        producto.setPrecioVenta(BigDecimal.ZERO);
        producto.setDescripcion(productoDTO.getDescripcion());
        producto.setStock(0);
        producto.setStockMinimo(productoDTO.getStockMinimo());
        producto.setCostoPromedio(BigDecimal.ZERO);

        // Asignar relaciones
        producto.setCategoria(categoria);
        producto.setUnidadMedida(unidad);
        producto.setProveedor(proveedor);
        producto.setImpuesto(impuesto);

        // Guardar producto
        Producto productoGuardado = productoRepository.save(producto);

        // Generar código automático si no existe
        if (productoGuardado.getCodigoProducto() == null) {
            String codigo = String.format("PRO%04d", productoGuardado.getIdProducto());
            productoGuardado.setCodigoProducto(codigo);
            productoRepository.save(productoGuardado);
        }

        return productoGuardado;
    }

    /**
     * Actualizar un producto existente.
     */
    @Override
    public Producto actualizarProducto(int id, ProductoDTO productoDTO) {

        // Validar existencia
        Producto productoExistente = productoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe ese producto."));

        String nuevoNombre = productoDTO.getNombreProducto().trim();

        // Validar nombre duplicado en otro producto
        Optional<Producto> optionalProducto =
                productoRepository.findByNombreProductoIgnoreCase(nuevoNombre);

        if (optionalProducto.isPresent()) {
            Producto productoConNombre = optionalProducto.get();
            if (productoConNombre.getIdProducto() != productoExistente.getIdProducto()) {
                throw new DuplicadoException("Ya existe otro producto con ese nombre.");
            }
        }

        // Obtener relaciones
        Categoria categoria = categoriaRepository.findById(productoDTO.getIdCategoria())
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe esa categoría."));

        UnidadMedida unidad = unidadMedidaRepository.findById(productoDTO.getIdUnidadMedida())
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe la unidad de medida seleccionada."));

        Proveedor proveedor = proveedorRepository.findById(productoDTO.getIdProveedor())
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe el proveedor seleccionado."));

        // Actualizar datos
        productoExistente.setNombreProducto(nuevoNombre);
        productoExistente.setDescripcion(productoDTO.getDescripcion());
        productoExistente.setStockMinimo(productoDTO.getStockMinimo());
        productoExistente.setPrecioVenta(productoDTO.getPrecioVenta());

        // Actualizar relaciones
        productoExistente.setCategoria(categoria);
        productoExistente.setUnidadMedida(unidad);
        productoExistente.setProveedor(proveedor);

        return productoRepository.save(productoExistente);
    }

    /**
     * Eliminación lógica del producto.
     */
    @Override
    public void borrarProducto(int id){
        Producto producto = productoRepository.findById(id).orElse(null);
        if (producto != null){
            producto.setActivo(false);
            productoRepository.save(producto);
        }
    }

    /**
     * Aplica una compra al producto y recalcula el costo promedio.
     */
    public void aplicarCompra(
            Producto producto,
            int cantidad,
            BigDecimal precioUnitario
    ) {

        int stockAnterior = producto.getStock();

        BigDecimal costoAnterior =
                producto.getCostoPromedio() != null
                        ? producto.getCostoPromedio()
                        : BigDecimal.ZERO;

        BigDecimal valorActual =
                costoAnterior.multiply(BigDecimal.valueOf(stockAnterior));

        BigDecimal valorCompra =
                precioUnitario.multiply(BigDecimal.valueOf(cantidad));

        int nuevoStock = stockAnterior + cantidad;

        BigDecimal nuevoCostoPromedio = valorActual
                .add(valorCompra)
                .divide(
                        BigDecimal.valueOf(nuevoStock),
                        2,
                        RoundingMode.HALF_UP
                );

        producto.setStock(nuevoStock);
        producto.setCostoPromedio(nuevoCostoPromedio);

        productoRepository.save(producto);
    }

}
