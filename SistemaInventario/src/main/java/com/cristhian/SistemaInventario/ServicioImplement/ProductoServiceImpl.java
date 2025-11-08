package com.cristhian.SistemaInventario.ServicioImplement;

import com.cristhian.SistemaInventario.Modelo.Producto;
import com.cristhian.SistemaInventario.Repositorio.ProductoRepository;
import com.cristhian.SistemaInventario.Service.IProductoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ProductoServiceImpl implements IProductoService {

    @Autowired
    private ProductoRepository productoRepository;

    @Override
    public List<Producto> lstarProducto(){
        return productoRepository.findByActivoTrue();
    }

    @Override
    public Optional<Producto> buscarProducto(int id){
        return productoRepository.findById(id);
    }

    @Override
    public Producto guardarProducto(Producto producto){
        return productoRepository.save(producto);
    }

    @Override
    public void borrarProducto(int id){
        Producto producto = productoRepository.findById(id).orElse(null);
        if (producto != null){
            producto.setActive(false);
            productoRepository.save(producto);
        }
    }

    @Override
    public boolean existsBynombreProducto(String nombreProducto){
        return productoRepository.existsByNombreProducto(nombreProducto);
    }
}
