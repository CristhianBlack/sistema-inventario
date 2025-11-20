package com.cristhian.SistemaInventario.Service;

import com.cristhian.SistemaInventario.DTO.CategoriaDTO;
import com.cristhian.SistemaInventario.Modelo.Categoria;

import java.util.List;
import java.util.Optional;

public interface ICategoriaService {

    public List<Categoria> listarCategoriasActivas();
    public Optional<Categoria> buscarCategoriaId(int id);
    public Categoria guardarCategoria(CategoriaDTO categoriaDto);
    public Categoria actualizarCategoria( int id, CategoriaDTO categoriaDTO);
    public void eliminarCategoria(int id);
    public boolean existeCategoria(int id);
    public boolean existsByNombreCategoria(String nombreCategoria);
}
