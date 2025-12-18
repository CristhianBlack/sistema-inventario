package com.cristhian.SistemaInventario.Service;

import com.cristhian.SistemaInventario.DTO.CompraDTO;
import com.cristhian.SistemaInventario.Modelo.Compra;

import java.util.List;
import java.util.Optional;

public interface ICompraService {

    List<Compra> listarComprasActivas();
    Optional<Compra> BuscarCompraId(int id);
    public Compra guardarCompra(CompraDTO compraDTO);
    public Compra actualizarCompra(int id, CompraDTO compraDTO);
    public void eliminarCompra(int id);

}
