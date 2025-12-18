package com.cristhian.SistemaInventario.Controlador;

import com.cristhian.SistemaInventario.DTO.CiudadDTO;
import com.cristhian.SistemaInventario.DTO.ProductoDTO;
import com.cristhian.SistemaInventario.Mensaje.Mensaje;
import com.cristhian.SistemaInventario.Modelo.*;
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


    @Autowired
    public ProductoController(ProductoServiceImpl productoService) {
        this.productoService = productoService;
    }

    @GetMapping("/Productos")
    public ResponseEntity<List<ProductoDTO>> listadoProductos(){
        List<ProductoDTO> response = productoService.listarProductoActivo().stream()
                .map(ProductoDTO :: new).toList(); // mapeo entidad → DTO
        return ResponseEntity.ok(response);
    }

    @GetMapping("/Productos/{id}")
    public ResponseEntity<?> obtenerProductoPorId(@PathVariable int id){
        Optional<Producto> data = productoService.buscarProducto(id);

        if (data.isEmpty()) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(new Mensaje("No existe el registro buscado"));
        }
        return ResponseEntity.ok(new ProductoDTO(data.get()));
    }

    @PostMapping("/Productos")
    public ResponseEntity<?> agregarProducto(@RequestBody ProductoDTO productoDTO){

        try{
            var producto = productoService.guardarProducto(productoDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(new ProductoDTO(producto));
        }catch (IllegalArgumentException e){
            return ResponseEntity.badRequest().body(new Mensaje(e.getMessage()));
        }
    }

    @PutMapping("/Productos/{id}")
    public ResponseEntity<?> actualizarProducto(@PathVariable int id, @RequestBody ProductoDTO productoDTO){
        try{
            var actualizado = productoService.actualizarProducto(id, productoDTO);
            return ResponseEntity.status(HttpStatus.OK).body(new ProductoDTO(actualizado));
        }catch (IllegalArgumentException e){
            return ResponseEntity.badRequest().body(new Mensaje(e.getMessage()));
        }
    }

    @DeleteMapping("/Productos/{id}")
    public ResponseEntity<?> eliminarProducto(@PathVariable int id){
        try{
            productoService.borrarProducto(id);
            return ResponseEntity.status(HttpStatus.OK).body(new Mensaje("Producto eliminado con éxito"));
        }catch (IllegalArgumentException e){
            return ResponseEntity.badRequest().body(new Mensaje(e.getMessage()));
        }
    }
}
