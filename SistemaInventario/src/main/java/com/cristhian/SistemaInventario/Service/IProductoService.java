package com.cristhian.SistemaInventario.Service;

import com.cristhian.SistemaInventario.Modelo.Producto;

import java.util.List;
import java.util.Optional;

public interface IProductoService {

    public List<Producto> lstarProducto();
    public Optional<Producto> buscarProducto(int id);
    public Producto guardarProducto(Producto producto);
    public void borrarProducto(int id);
    public boolean existsBynombreProducto(String nombreProducto);
}
