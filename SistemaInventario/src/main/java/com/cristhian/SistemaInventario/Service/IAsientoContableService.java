package com.cristhian.SistemaInventario.Service;

import com.cristhian.SistemaInventario.DTO.AsientoContableDTO;
import com.cristhian.SistemaInventario.Modelo.Compra;
import com.cristhian.SistemaInventario.Modelo.CompraPago;
import com.cristhian.SistemaInventario.Modelo.Venta;
import com.cristhian.SistemaInventario.Modelo.VentaPago;

public interface IAsientoContableService {

    public void registrarAsiento(AsientoContableDTO dto, Compra compra, CompraPago pago, Venta venta, VentaPago ventaPago);
}
