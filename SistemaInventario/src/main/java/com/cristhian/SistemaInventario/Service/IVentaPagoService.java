package com.cristhian.SistemaInventario.Service;

import com.cristhian.SistemaInventario.DTO.VentaPagoDTO;
import com.cristhian.SistemaInventario.Modelo.Venta;

import java.util.List;

public interface IVentaPagoService {

    public void registrarPago(int id, VentaPagoDTO pagoDTO);
    public void confirmarPago(int id);
    public void rechazarPago(int id);
    public void recalcularEstadoVenta(Venta venta);
    public List<VentaPagoDTO> listarPorVenta(Long idVenta);
}
