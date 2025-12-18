package com.cristhian.SistemaInventario.Service;

import com.cristhian.SistemaInventario.DTO.MovimientoInventarioDTO;
import com.cristhian.SistemaInventario.Modelo.Ciudad;
import com.cristhian.SistemaInventario.Modelo.MovimientoInventario;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface IMovimientoInventario {

    public List<MovimientoInventario> listarMovimientosInventario();

    //filtramos y exportamos el movimiento del inventario completo si id
    public List<MovimientoInventarioDTO> generarMovimientoInventarioPorFechas(LocalDate desde, LocalDate hasta);
    public byte[] exportarMovimientoInventarioExcel(LocalDate desde, LocalDate hasta);

    public byte[] exportarMovimientoInventarioPdf( LocalDate desde, LocalDate hasta);

}
