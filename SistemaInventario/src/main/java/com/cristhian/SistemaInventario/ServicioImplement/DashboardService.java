package com.cristhian.SistemaInventario.ServicioImplement;

import com.cristhian.SistemaInventario.DTO.DashboardTotalesDTO;
import com.cristhian.SistemaInventario.Repositorio.*;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class DashboardService {

    @Autowired private PersonaRepository personaRepo;
    @Autowired private ProveedorRepository proveedorRepo;
    @Autowired private ProductoRepository productoRepo;
    @Autowired
    private CompraRepository compraRepo;
    @Autowired private MovimientoInventarioRepository movRepo;
    @Autowired private AsientoContableRepository asientoRepo;
    @Autowired private VentaRepository ventaRepository;


    public DashboardTotalesDTO obtenerTotales() {
        DashboardTotalesDTO dto = new DashboardTotalesDTO();

        dto.setTotalComprasMes(compraRepo.totalComprasMes());
        dto.setValorInventario(productoRepo.valorInventario());
        dto.setTotalVentaMes(ventaRepository.totalVentasMes());

        dto.setTotalPersonas(personaRepo.count());
        dto.setTotalProveedores(proveedorRepo.count());
        dto.setTotalProductos(productoRepo.count());
        dto.setTotalCompras(compraRepo.count());
        dto.setTotalMovimientos(movRepo.count());
        dto.setTotalAsientos(asientoRepo.count());
        dto.setTotalVentas(ventaRepository.count());
        return dto;
    }
}

