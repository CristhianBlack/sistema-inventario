package com.cristhian.SistemaInventario.Service;

import com.cristhian.SistemaInventario.DTO.MovimientoInventarioDTO;
import com.cristhian.SistemaInventario.Modelo.Ciudad;
import com.cristhian.SistemaInventario.Modelo.MovimientoInventario;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface IMovimientoInventario {

    public List<MovimientoInventarioDTO> listarMovimientosInventario();

    //filtramos y exportamos el movimiento del inventario completo si id
    public List<MovimientoInventarioDTO> generarMovimientoInventarioPorFechas(LocalDateTime desde, LocalDateTime hasta);
    public byte[] exportarMovimientoInventarioExcel(LocalDateTime desde, LocalDateTime hasta);

    public byte[] exportarMovimientoInventarioPdf( LocalDateTime desde, LocalDateTime hasta);

}

