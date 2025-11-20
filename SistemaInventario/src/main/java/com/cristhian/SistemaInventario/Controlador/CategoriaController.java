package com.cristhian.SistemaInventario.Controlador;

import com.cristhian.SistemaInventario.DTO.CategoriaDTO;
import com.cristhian.SistemaInventario.Mensaje.Mensaje;
import com.cristhian.SistemaInventario.Service.ICategoriaService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


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
    public ResponseEntity<List<CategoriaDTO>> listarCategoria(){
        List<CategoriaDTO> response = categoriaService.listarCategoriasActivas().stream()
                .map(CategoriaDTO::new) // mapeo entidad → DTO
                .toList();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/Categorias/{id}")
    public ResponseEntity<?> obtenerCategoriaPorId(@PathVariable int id){
        return categoriaService.buscarCategoriaId(id)
                .<ResponseEntity<?>>map(categoria ->
                        ResponseEntity.ok(new CategoriaDTO(categoria)) // ← corregido
                ).orElseGet(() ->
                        ResponseEntity.status(HttpStatus.NOT_FOUND)
                                .body(new Mensaje("No existe el registro buscado"))
                );
    }


    @PostMapping("/Categorias")
    public ResponseEntity<?> agregrarCategoria(@Valid @RequestBody CategoriaDTO categoriaDTO){
        try{
            var categoria = categoriaService.guardarCategoria(categoriaDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(new CategoriaDTO(categoria));
        }catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new Mensaje(e.getMessage()));
        }
    }

    @PutMapping("/Categorias/{id}")
    public ResponseEntity<?> actualizarCategoria(@PathVariable int id, @RequestBody CategoriaDTO categoriaDTO){
        try {
            var actualizado = categoriaService.actualizarCategoria(id, categoriaDTO);
            return ResponseEntity.status(HttpStatus.OK).body(new CategoriaDTO(actualizado));
        }catch (IllegalArgumentException e){
            return ResponseEntity.badRequest().body(new Mensaje(e.getMessage()));
        }
    }

    @DeleteMapping("/Categorias/{id}")
    public ResponseEntity<?> eliminarCategoria(@PathVariable int id){
        try {
            categoriaService.eliminarCategoria(id);
            return ResponseEntity.ok(new Mensaje("Categoria eliminada con éxito"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new Mensaje(e.getMessage()));
        }
    }
}
