package com.cristhian.SistemaInventario.ServicioImplement;

import com.cristhian.SistemaInventario.DTO.ProductoDTO;
import com.cristhian.SistemaInventario.Excepciones.DuplicadoException;
import com.cristhian.SistemaInventario.Excepciones.RecursoNoEncontradoException;
import com.cristhian.SistemaInventario.Mensaje.Mensaje;
import com.cristhian.SistemaInventario.Modelo.*;
import com.cristhian.SistemaInventario.Repositorio.CategoriaRepository;
import com.cristhian.SistemaInventario.Repositorio.ProductoRepository;
import com.cristhian.SistemaInventario.Repositorio.ProveedorRepository;
import com.cristhian.SistemaInventario.Repositorio.UnidadMedidaRepository;
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

    public ProductoServiceImpl(ProductoRepository productoRepository,
                               CategoriaRepository categoriaRepository,
                               ProveedorRepository proveedorRepository,
                               UnidadMedidaRepository unidadMedidaRepository) {
        this.productoRepository = productoRepository;
        this.categoriaRepository = categoriaRepository;
        this.unidadMedidaRepository = unidadMedidaRepository;
        this.proveedorRepository = proveedorRepository;
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
}
