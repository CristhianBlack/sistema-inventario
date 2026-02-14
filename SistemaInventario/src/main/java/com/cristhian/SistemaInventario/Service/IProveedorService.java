package com.cristhian.SistemaInventario.Service;

import com.cristhian.SistemaInventario.DTO.ProveedorDTO;
import com.cristhian.SistemaInventario.Modelo.Proveedor;

import java.util.List;
import java.util.Optional;

public interface IProveedorService {


    public List<Proveedor> listarProveedoresActivos();
    public Optional<Proveedor> buscarProveedorId(int id);
    public Proveedor guardarProveedor(ProveedorDTO proveedorDTO);
    public Proveedor actualizarProveedor( int id, ProveedorDTO proveedorDTO);
    public void borrarProveedor(int id);
}

