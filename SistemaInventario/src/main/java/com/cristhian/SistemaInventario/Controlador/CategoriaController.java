package com.cristhian.SistemaInventario.Controlador;

import com.cristhian.SistemaInventario.DTO.CategoriaDTO;
import com.cristhian.SistemaInventario.Mensaje.Mensaje;
import com.cristhian.SistemaInventario.Modelo.Categoria;
import com.cristhian.SistemaInventario.Service.ICategoriaService;
import com.cristhian.SistemaInventario.ServicioImplement.CategoriaServiceImpl;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/Inventario")
@CrossOrigin(origins = "http://localhost:4200")
public class CategoriaController {

    private static final Logger logger = LoggerFactory.getLogger(CategoriaController.class);
    private ICategoriaService categoriaService;

    public CategoriaController(ICategoriaService categoriaService) {
        this.categoriaService = categoriaService;
    }

    @GetMapping("/Categorias")
    public ResponseEntity<List<Categoria>> listarCategoria(){
        List<Categoria> listado = categoriaService.listarCategoriasActivas();
        return new ResponseEntity(listado, HttpStatus.OK);
    }

    @GetMapping("/Categorias/{id}")
    public ResponseEntity<Categoria> obtenerProductoPorId(@PathVariable int id){
        Optional<Categoria> categoria = categoriaService.buscarCategoriaId(id);
        if (categoria.isPresent()){
            return new ResponseEntity<>(categoria.get(), HttpStatus.OK);
        }else{
            return new ResponseEntity(new Mensaje("No Existe la Categoria buscada"), HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/Categorias")
    public ResponseEntity<?> agregrarCategoria(@Valid @RequestBody CategoriaDTO categoriaDTO){

        logger.info("Ingreso al metodo agregar categoria");

        if(categoriaService.existsByNombreCategoria(categoriaDTO.getNombreCategoria())){
            return new ResponseEntity<>(new Mensaje("Ya existe la categoria"), HttpStatus.BAD_REQUEST);
        }

        Categoria categoria = new Categoria();
        categoria.setNombreCategoria(categoriaDTO.getNombreCategoria());
        categoria.setDescripcion(categoriaDTO.getDescripcion());
        categoria.setActivo(categoriaDTO.isActivo());

        logger.info("categoria a agregar: "+categoria);
        categoriaService.guardarCategoria(categoria);

        return new ResponseEntity<>(new Mensaje("Categoria Registrada con exito."), HttpStatus.CREATED);
    }

    @PutMapping("/Categorias/{id}")
    public ResponseEntity<?> actualizarCategoria(@PathVariable int id, @RequestBody CategoriaDTO categoriaDTO){

        Optional<Categoria> categoriaOpt = categoriaService.buscarCategoriaId(id);

        if(!categoriaOpt.isPresent()){
            return new ResponseEntity<>(new Mensaje("La categoria no existe"), HttpStatus.NOT_FOUND);
        }

        if (categoriaService.existsByNombreCategoria(categoriaDTO.getNombreCategoria()) &&
        !categoriaDTO.getNombreCategoria().equals(categoriaOpt.get().getNombreCategoria())){
            return new ResponseEntity<>(new Mensaje("Ya exisite otra categoria con ese nombre"), HttpStatus.BAD_REQUEST);
        }

        Categoria categoria = categoriaOpt.get();

        categoria.setNombreCategoria(categoriaDTO.getNombreCategoria());
        categoria.setDescripcion(categoriaDTO.getDescripcion());
        categoria.setActivo(categoriaDTO.isActivo());

        categoriaService.guardarCategoria(categoria);

        return new ResponseEntity(new Mensaje("Se Actualizo con exito la categoria"), HttpStatus.OK);
    }

    @DeleteMapping("/Categorias/{id}")
    public ResponseEntity<?> eliminarCategoria(@PathVariable int id){
        try {
            Optional<Categoria> categoria = this.categoriaService.buscarCategoriaId(id);
            if (categoria.isPresent()){
                this.categoriaService.eliminarCategoria(id);
                return new ResponseEntity<>(new Mensaje("Categoria eliminada con exito"), HttpStatus.OK);
            }else{
                return new ResponseEntity<>(new Mensaje("No existe la Categoria"), HttpStatus.NOT_FOUND);
            }
        }catch (Exception e) {
            e.printStackTrace(); // para ver el error exacto en la consola
            return new ResponseEntity<>(new Mensaje("Error interno al eliminar la categoria"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
