package com.cristhian.SistemaInventario.ServicioImplement;

import com.cristhian.SistemaInventario.Modelo.Categoria;
import com.cristhian.SistemaInventario.Repositorio.CategoriaRepository;
import com.cristhian.SistemaInventario.Service.ICategoriaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class CategoriaServiceImpl implements ICategoriaService {

    @Autowired
    private CategoriaRepository categoriaRepository;

    @Override
    public List<Categoria> listarCategoriasActivas(){
        return categoriaRepository.findByActivoTrue();
    }

    @Override
    public Optional<Categoria> buscarCategoriaId(int id){
        return categoriaRepository.findById(id);
    }

    @Override
    public Categoria guardarCategoria(Categoria categoria){
        return categoriaRepository.save(categoria);
    }

    @Override
    public void eliminarCategoria(int id){
        Categoria categoria = categoriaRepository.findById(id).orElse(null);
        if (categoria != null){
            categoria.setActivo(false);
            categoriaRepository.save(categoria);
        }
    }

    @Override
    public boolean existeCategoria(int id){
        return categoriaRepository.existsById(id);
    }

    @Override
    public boolean existsByNombreCategoria(String nombreCategoria){
        return categoriaRepository.existsByNombreCategoria(nombreCategoria);
    };

}
