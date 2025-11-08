package com.cristhian.SistemaInventario.Controlador;

import com.cristhian.SistemaInventario.DTO.ProductoDTO;
import com.cristhian.SistemaInventario.Mensaje.Mensaje;
import com.cristhian.SistemaInventario.Modelo.Categoria;
import com.cristhian.SistemaInventario.Modelo.Producto;
import com.cristhian.SistemaInventario.Modelo.Proveedor;
import com.cristhian.SistemaInventario.Modelo.UnidadMedida;
import com.cristhian.SistemaInventario.ServicioImplement.CategoriaServiceImpl;
import com.cristhian.SistemaInventario.ServicioImplement.ProductoServiceImpl;
import com.cristhian.SistemaInventario.ServicioImplement.ProveedorServiceImpl;
import com.cristhian.SistemaInventario.ServicioImplement.UnidadMedidaServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/Inventario")
@CrossOrigin(origins = "http://localhost:4200")
public class ProductoController {


    private ProductoServiceImpl productoService;
    private CategoriaServiceImpl categoriaService;
    private UnidadMedidaServiceImpl unidadMedidaService;
    private ProveedorServiceImpl proveedorService;

    @Autowired
    public ProductoController(ProductoServiceImpl productoService, CategoriaServiceImpl categoriaService, UnidadMedidaServiceImpl unidadMedidaService, ProveedorServiceImpl proveedorService) {
        this.productoService = productoService;
        this.categoriaService = categoriaService;
        this.unidadMedidaService = unidadMedidaService;
        this.proveedorService = proveedorService;
    }

    @GetMapping("/Productos")
    public ResponseEntity<List<Producto>> listadoProductos(){
        List<Producto> listado = productoService.lstarProducto();
        return new ResponseEntity(listado, HttpStatus.OK);
    }

    @GetMapping("/Productos/{id}")
    public ResponseEntity<Producto> obtenerProductoPorId(@PathVariable int id){
        Optional<Producto> producto = productoService.buscarProducto(id);
        if(producto.isPresent()){
            return new ResponseEntity<>(producto.get(), HttpStatus.OK);
        }else{
            return new ResponseEntity(new Mensaje("No existe el producto"), HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/Productos")
    public ResponseEntity<?> agregarProducto(@RequestBody ProductoDTO productoDTO){

        // 1️⃣ Validar si el nombre del producto ya existe
        if(productoService.existsBynombreProducto(productoDTO.getNombreProducto())){
            return new ResponseEntity<>(new Mensaje("El producto ya existe."), HttpStatus.BAD_REQUEST);
        }

        // 2️⃣ Buscar las entidades relacionadas (categoría, unidad, proveedor)
        Optional<Categoria> categoriaOpt = categoriaService.buscarCategoriaId(productoDTO.getIdCategoria());
        Optional<UnidadMedida> unidadOpt = unidadMedidaService.buscarUnidadId(productoDTO.getIdUnidadMedida());
        Optional<Proveedor> proveedorOpt = proveedorService.buscarProveedorId(productoDTO.getIdProveedor());

        // 3️⃣ Validar existencia de cada relación (si alguna falta, error)
        if (!categoriaOpt.isPresent()) {
            return new ResponseEntity<>(new Mensaje("No existe la categoría seleccionada."), HttpStatus.BAD_REQUEST);
        }
        if (!unidadOpt.isPresent()) {
            return new ResponseEntity<>(new Mensaje("No existe la unidad de medida seleccionada."), HttpStatus.BAD_REQUEST);
        }
        if (!proveedorOpt.isPresent()) {
            return new ResponseEntity<>(new Mensaje("No existe el proveedor seleccionado."), HttpStatus.BAD_REQUEST);
        }

        // 4️⃣ Creamos y llenamos el producto
        Producto producto = new Producto();
        producto.setCodigoProducto(productoDTO.getCodigoProducto());
        producto.setNombreProducto(productoDTO.getNombreProducto());
        producto.setPrecioCompra(productoDTO.getPrecioCompra());
        producto.setPrecioVenta(productoDTO.getPrecioVenta());
        producto.setStock(productoDTO.getStock());
        producto.setStockMinimo(productoDTO.getStockMinimo());
        producto.setDescripcion(productoDTO.getDescripcion());
        producto.setFechaCreacion(productoDTO.getFechaCreacion());
        producto.setActive(productoDTO.isActive());

        // 5️⃣ Asignar relaciones usando Optional (más limpio)
        categoriaOpt.ifPresent(producto::setCategoria);
        unidadOpt.ifPresent(producto::setUnidadMedida);
        proveedorOpt.ifPresent(producto::setProveedor);

        // 6️⃣ Guardar producto
        productoService.guardarProducto(producto);

        return  new ResponseEntity<>(new Mensaje("Producto creado con exito."),HttpStatus.CREATED);
    }

    @PutMapping("/Productos/{id}")
    public ResponseEntity<Producto> actualizarProducto(@PathVariable int id, @RequestBody ProductoDTO productoDTO){

        // 1️⃣ Validar que el producto exista
        Optional<Producto> productoOpt = productoService.buscarProducto(id);
        if (!productoOpt.isPresent()) {
            return new ResponseEntity(new Mensaje("No existe el producto."), HttpStatus.NOT_FOUND);
        }

        Producto producto = productoOpt.get();

        // 2️⃣ Validar si el nuevo nombre ya está usado por OTRO producto
        if (productoService.existsBynombreProducto(productoDTO.getNombreProducto()) &&
                !producto.getNombreProducto().equalsIgnoreCase(productoDTO.getNombreProducto())) {
            return new ResponseEntity(new Mensaje("Ya existe otro producto con ese nombre."), HttpStatus.BAD_REQUEST);
        }

        // 3️⃣ Buscar las entidades relacionadas (categoría, unidad, proveedor)
        Optional<Categoria> categoriaOpt = categoriaService.buscarCategoriaId(productoDTO.getIdCategoria());
        Optional<UnidadMedida> unidadOpt = unidadMedidaService.buscarUnidadId(productoDTO.getIdUnidadMedida());
        Optional<Proveedor> proveedorOpt = proveedorService.buscarProveedorId(productoDTO.getIdProveedor());

        // 4️⃣ Validar existencia de relaciones
        if (!categoriaOpt.isPresent()) {
            return new ResponseEntity(new Mensaje("No existe la categoría seleccionada."), HttpStatus.BAD_REQUEST);
        }
        if (!unidadOpt.isPresent()) {
            return new ResponseEntity(new Mensaje("No existe la unidad de medida seleccionada."), HttpStatus.BAD_REQUEST);
        }
        if (!proveedorOpt.isPresent()) {
            return new ResponseEntity(new Mensaje("No existe el proveedor seleccionado."), HttpStatus.BAD_REQUEST);
        }

        // 5️⃣ Actualizar los datos del producto
        producto.setCodigoProducto(productoDTO.getCodigoProducto());
        producto.setNombreProducto(productoDTO.getNombreProducto());
        producto.setPrecioCompra(productoDTO.getPrecioCompra());
        producto.setPrecioVenta(productoDTO.getPrecioVenta());
        producto.setStock(productoDTO.getStock());
        producto.setStockMinimo(productoDTO.getStockMinimo());
        producto.setDescripcion(productoDTO.getDescripcion());
        producto.setFechaCreacion(productoDTO.getFechaCreacion());
        producto.setActive(productoDTO.isActive());

        // 6️⃣ Asignar relaciones
        categoriaOpt.ifPresent(producto::setCategoria);
        unidadOpt.ifPresent(producto::setUnidadMedida);
        proveedorOpt.ifPresent(producto::setProveedor);

        // 7️⃣ Guardar producto actualizado
        productoService.guardarProducto(producto);
        return new ResponseEntity(new Mensaje("Se actualizo el producto con exito"), HttpStatus.OK);
    }

    @DeleteMapping("/Productos/{id}")
    public ResponseEntity<?> eliminarProducto(@PathVariable int id){
        try{
            Optional<Producto> producto = productoService.buscarProducto(id);
            if(producto.isPresent()){
                productoService.borrarProducto(id);
                return new ResponseEntity<>(new Mensaje("Producto eliminado con exito "), HttpStatus.OK);
            }else{
                return new ResponseEntity<>(new Mensaje("No existe el producto seleccionado"), HttpStatus.NOT_FOUND);
            }
        }catch (Exception e){
            e.printStackTrace();
            return new ResponseEntity<>(new Mensaje("Error interno al eliminar el proveedor"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
