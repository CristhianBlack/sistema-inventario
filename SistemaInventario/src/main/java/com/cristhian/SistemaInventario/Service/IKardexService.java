package com.cristhian.SistemaInventario.Service;

import com.cristhian.SistemaInventario.DTO.KardexDTO;
import com.cristhian.SistemaInventario.DTO.MovimientoInventarioDTO;
import com.cristhian.SistemaInventario.Modelo.MovimientoInventario;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;


public interface IKardexService {

    public List<KardexDTO> generarKardex(int idProduct);

    //Filtramos y exportamos el kardex por producto
    public List<KardexDTO> generarKardexPorFechas(
            int idProducto,
            LocalDateTime desde,
            LocalDateTime hasta);

    public byte[] exportarKardexExcel(int idProducto, LocalDateTime desde, LocalDateTime hasta);

    public byte[] exportarKardexPdf(int idProducto, LocalDateTime desde, LocalDateTime hasta);


}


