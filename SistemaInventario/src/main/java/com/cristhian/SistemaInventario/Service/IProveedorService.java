package com.cristhian.SistemaInventario.Service;

import com.cristhian.SistemaInventario.Modelo.Proveedor;

import java.util.List;
import java.util.Optional;

public interface IProveedorService {


    public List<Proveedor> listarProveedoresActivos();
    public Optional<Proveedor> buscarProveedorId(int id);
    public Proveedor guardarProveedor(Proveedor proveedor);
    public void borrarProveedor(int id);
}
