package com.cristhian.SistemaInventario.Service;

import com.cristhian.SistemaInventario.DTO.CompraDTO;
import com.cristhian.SistemaInventario.DTO.VentaDTO;

public interface IVentaContableService {

    public VentaDTO registrarCompraContable(VentaDTO ventaDTO);
}
