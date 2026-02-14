package com.cristhian.SistemaInventario.ServicioImplement;

import com.cristhian.SistemaInventario.DTO.CategoriaDTO;
import com.cristhian.SistemaInventario.Excepciones.DuplicadoException;
import com.cristhian.SistemaInventario.Excepciones.RecursoNoEncontradoException;
import com.cristhian.SistemaInventario.Modelo.Categoria;
import com.cristhian.SistemaInventario.Modelo.Ciudad;
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

    /*private static final Logger logger = LoggerFactory.getLogger(CategoriaServiceImpl.class);

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
            throw new DuplicadoException("Ya existe una categoría con ese nombre");
        }

        // Crear entidad usando el constructor DTO → OK
        Categoria categoria = new Categoria(dto);

        return categoriaRepository.save(categoria);
    }

    @Override
    public Categoria actualizarCategoria(int id, CategoriaDTO dto) {

        Categoria categoriaExistente = categoriaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe esa categoría"));

        String nuevoNombre = dto.getNombreCategoria().trim();

        // Validación: el nombre existe en otra categoría diferente
        if (categoriaRepository.existsByNombreCategoriaIgnoreCase(nuevoNombre)
                && !nuevoNombre.equalsIgnoreCase(categoriaExistente.getNombreCategoria())) {

            throw new DuplicadoException("Ya existe otra categoría con ese nombre");
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
    }*/

    // Logger para trazabilidad de operaciones sobre categorías
    private static final Logger logger = LoggerFactory.getLogger(CategoriaServiceImpl.class);

    // Repositorio para la gestión de categorías
    @Autowired
    private CategoriaRepository categoriaRepository;

    /**
     * Obtiene la lista de categorías activas.
     *
     * @return lista de categorías con estado activo = true
     */
    @Override
    public List<Categoria> listarCategoriasActivas() {
        return categoriaRepository.findByActivoTrue();
    }

    /**
     * Busca una categoría por su identificador.
     *
     * @param id identificador de la categoría
     * @return Optional con la categoría encontrada, si existe
     */
    @Override
    public Optional<Categoria> buscarCategoriaId(int id) {
        return categoriaRepository.findById(id);
    }

    /**
     * Registra una nueva categoría en el sistema.
     *
     * Reglas de negocio:
     * - No se permiten categorías con nombres duplicados (ignorando mayúsculas/minúsculas)
     * - El nombre se normaliza eliminando espacios innecesarios
     *
     * @param dto datos de la categoría a registrar
     * @return categoría creada y persistida
     * @throws DuplicadoException si ya existe una categoría con el mismo nombre
     */
    @Override
    public Categoria guardarCategoria(CategoriaDTO dto) {
        logger.info("Ingreso al método agregar categoría");

        // Validación de nombre duplicado
        if (categoriaRepository.existsByNombreCategoriaIgnoreCase(dto.getNombreCategoria().trim())) {
            throw new DuplicadoException("Ya existe una categoría con ese nombre");
        }

        Optional<Categoria> categoriaExistente = categoriaRepository.findByNombreCategoriaIgnoreCase(dto.getNombreCategoria().trim());

        // Si existe la ciudad
        if (categoriaExistente.isPresent()) {
            Categoria categoria = categoriaExistente.get();

            // Si existe pero está inactiva → Reactivar
            if (!categoria.isActivo()) {
                logger.info("Ciudad encontrada inactiva. Se activará nuevamente.");
                categoria.setActivo(true);
                return categoriaRepository.save(categoria);   // ✔ IMPORTANTE: retornar
            }

            // Si ya existe y está activa → Error
            logger.info("La categoria ya existe y está activa.");
            throw new DuplicadoException("Ya existe una categoria con ese nombre");
        }

        // Creación de la entidad a partir del DTO
        Categoria categoria = new Categoria(dto);

        return categoriaRepository.save(categoria);
    }

    /**
     * Actualiza una categoría existente.
     *
     * Reglas de negocio:
     * - La categoría debe existir
     * - No se permite actualizar el nombre si ya existe en otra categoría
     * - El campo "activo" no se modifica desde un update estándar
     *
     * @param id  identificador de la categoría a actualizar
     * @param dto datos actualizados de la categoría
     * @return categoría actualizada
     * @throws RecursoNoEncontradoException si la categoría no existe
     * @throws DuplicadoException si el nuevo nombre ya está en uso por otra categoría
     */
    @Override
    public Categoria actualizarCategoria(int id, CategoriaDTO dto) {

        // Obtiene la categoría existente o lanza excepción si no existe
        Categoria categoriaExistente = categoriaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe esa categoría"));

        String nuevoNombre = dto.getNombreCategoria().trim();

        // Validación: el nombre existe en otra categoría diferente
        if (categoriaRepository.existsByNombreCategoriaIgnoreCase(nuevoNombre)
                && !nuevoNombre.equalsIgnoreCase(categoriaExistente.getNombreCategoria())) {

            throw new DuplicadoException("Ya existe otra categoría con ese nombre");
        }

        // Actualización de campos permitidos
        categoriaExistente.setNombreCategoria(nuevoNombre);
        categoriaExistente.setDescripcion(dto.getDescripcion());
        // El campo "activo" no debe actualizarse desde el DTO en un update normal

        return categoriaRepository.save(categoriaExistente);
    }

    /**
     * Elimina lógicamente una categoría.
     *
     * La eliminación se realiza marcando el registro como inactivo,
     * preservando la información histórica.
     *
     * @param id identificador de la categoría a eliminar
     */
    @Override
    public void eliminarCategoria(int id) {
        Categoria categoria = categoriaRepository.findById(id).orElse(null);
        if (categoria != null) {
            categoria.setActivo(false);
            categoriaRepository.save(categoria);
        }
    }

    /**
     * Verifica si existe una categoría por su identificador.
     *
     * @param id identificador de la categoría
     * @return true si existe, false en caso contrario
     */
    @Override
    public boolean existeCategoria(int id) {
        return categoriaRepository.existsById(id);
    }

    /**
     * Verifica si existe una categoría por su nombre.
     *
     * @param nombreCategoria nombre de la categoría
     * @return true si existe una categoría con ese nombre
     */
    @Override
    public boolean existsByNombreCategoria(String nombreCategoria) {
        return categoriaRepository.existsByNombreCategoriaIgnoreCase(nombreCategoria.trim());
    }
}



