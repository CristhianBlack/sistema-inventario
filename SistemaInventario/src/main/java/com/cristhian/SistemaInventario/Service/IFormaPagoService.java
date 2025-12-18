package com.cristhian.SistemaInventario.Service;

import com.cristhian.SistemaInventario.DTO.FormaPagoDTO;
import com.cristhian.SistemaInventario.Modelo.FormaPago;

import java.util.List;
import java.util.Optional;

public interface IFormaPagoService {

    public List<FormaPago> listarFormaPagoActivo();
    public Optional<FormaPago> buscarFormaPagoByID(int id);
    public void guardarFormaPagoAutomatico();
    public FormaPago actualizarFormaPago(int id, FormaPagoDTO formaPagoDTO);
    public void eliminarFormaPago(int id);
}
