package com.cristhian.SistemaInventario.Service;

import com.cristhian.SistemaInventario.DTO.VentaDTO;
import com.cristhian.SistemaInventario.Modelo.Venta;

import java.util.List;
import java.util.Optional;

public interface IVentaService {

    public List<Venta> listarVentas();
    public Optional<Venta> buscarVentaPorId(int id);
    public Venta guardarVenta(VentaDTO ventaDTO);
    public void cancelarVenta(int idVenta);
    public void confirmarVenta(int id);


}
