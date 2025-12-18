package com.cristhian.SistemaInventario.Service;

import com.cristhian.SistemaInventario.DTO.ProductoDTO;
import com.cristhian.SistemaInventario.Modelo.Producto;

import java.util.List;
import java.util.Optional;

public interface IProductoService {

    public List<Producto> listarProductoActivo();
    public Optional<Producto> buscarProducto(int id);
    public Producto guardarProducto(ProductoDTO productoDTO);
    public Producto actualizarProducto(int id, ProductoDTO productoDTO);
    public void borrarProducto(int id);
}
