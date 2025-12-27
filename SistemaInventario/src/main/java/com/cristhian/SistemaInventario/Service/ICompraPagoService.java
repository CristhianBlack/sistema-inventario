package com.cristhian.SistemaInventario.Service;

import com.cristhian.SistemaInventario.DTO.CompraPagoDTO;
import com.cristhian.SistemaInventario.DTO.VentaPagoDTO;
import com.cristhian.SistemaInventario.Modelo.Compra;
import com.cristhian.SistemaInventario.Modelo.Venta;

import java.util.List;

public interface ICompraPagoService {
    public void registrarPago(int id, CompraPagoDTO pagoDTO);
    public void confirmarPago(int id);
    public void rechazarPago(int id);
    public void recalcularEstadoVenta(Compra compra);
    public List<CompraPagoDTO> listarPorVenta(Long id);
}

