package com.cristhian.SistemaInventario.ServicioImplement;

import com.cristhian.SistemaInventario.DTO.CategoriaDTO;
import com.cristhian.SistemaInventario.Modelo.Categoria;
import com.cristhian.SistemaInventario.Repositorio.CategoriaRepository;
import com.cristhian.SistemaInventario.Service.ICategoriaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class CategoriaServiceImpl implements ICategoriaService {

    private static final Logger logger = LoggerFactory.getLogger(CategoriaServiceImpl.class);

    @Autowired
    private CategoriaRepository categoriaRepository;

    @Override
    public List<Categoria> listarCategoriasActivas() {
        return categoriaRepository.findByActivoTrue();
    }

    @Override
    public Optional<Categoria> buscarCategoriaId(int id) {
        return categoriaRepository.findById(id);
    }

    @Override
    public Categoria guardarCategoria(CategoriaDTO dto) {
        logger.info("Ingreso al método agregar categoría");

        // Validación de nombre duplicado
        if (categoriaRepository.existsByNombreCategoriaIgnoreCase(dto.getNombreCategoria().trim())) {
            throw new IllegalArgumentException("Ya existe una categoría con ese nombre");
        }

        // Crear entidad usando el constructor DTO → OK
        Categoria categoria = new Categoria(dto);

        return categoriaRepository.save(categoria);
    }

    @Override
    public Categoria actualizarCategoria(int id, CategoriaDTO dto) {

        Categoria categoriaExistente = categoriaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("No existe esa categoría"));

        String nuevoNombre = dto.getNombreCategoria().trim();

        // Validación: el nombre existe en otra categoría diferente
        if (categoriaRepository.existsByNombreCategoriaIgnoreCase(nuevoNombre)
                && !nuevoNombre.equalsIgnoreCase(categoriaExistente.getNombreCategoria())) {

            throw new IllegalArgumentException("Ya existe otra categoría con ese nombre");
        }

        // Actualización
        categoriaExistente.setNombreCategoria(nuevoNombre);
        categoriaExistente.setDescripcion(dto.getDescripcion());
        // El campo "activo" no debe actualizarse desde el DTO en un update normal

        return categoriaRepository.save(categoriaExistente);
    }

    @Override
    public void eliminarCategoria(int id) {
        Categoria categoria = categoriaRepository.findById(id).orElse(null);
        if (categoria != null) {
            categoria.setActivo(false);
            categoriaRepository.save(categoria);
        }
    }

    @Override
    public boolean existeCategoria(int id) {
        return categoriaRepository.existsById(id);
    }

    @Override
    public boolean existsByNombreCategoria(String nombreCategoria) {
        return categoriaRepository.existsByNombreCategoriaIgnoreCase(nombreCategoria.trim());
    }
}

