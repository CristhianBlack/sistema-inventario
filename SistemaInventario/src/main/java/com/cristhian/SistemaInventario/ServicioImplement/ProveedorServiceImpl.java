package com.cristhian.SistemaInventario.ServicioImplement;

import com.cristhian.SistemaInventario.Modelo.Proveedor;
import com.cristhian.SistemaInventario.Repositorio.ProveedorRepository;
import com.cristhian.SistemaInventario.Service.IProveedorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ProveedorServiceImpl implements IProveedorService {

    @Autowired
    private ProveedorRepository proveedorRepository;

    @Override
    public List<Proveedor> listarProveedoresActivos(){
        return proveedorRepository.findByActivoTrue();
    }

    @Override
    public Optional<Proveedor> buscarProveedorId(int id){
        return proveedorRepository.findById(id);
    }

    @Override
    public Proveedor guardarProveedor(Proveedor proveedor){
        return proveedorRepository.save(proveedor);
    }

    @Override
    public void borrarProveedor(int id){
        Proveedor proveedor = proveedorRepository.findById(id).orElse(null);
        if (proveedor != null){
            proveedor.setActivo(false);
            proveedorRepository.save(proveedor);
        }
    }


}
