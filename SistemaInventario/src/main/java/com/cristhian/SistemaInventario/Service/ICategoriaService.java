package com.cristhian.SistemaInventario.Service;

import com.cristhian.SistemaInventario.Modelo.Categoria;

import java.util.List;
import java.util.Optional;

public interface ICategoriaService {

    public List<Categoria> listarCategoriasActivas();
    public Optional<Categoria> buscarCategoriaId(int id);
    public Categoria guardarCategoria(Categoria categoria);
    public void eliminarCategoria(int id);
    public boolean existeCategoria(int id);
    public boolean existsByNombreCategoria(String nombreCategoria);
}
