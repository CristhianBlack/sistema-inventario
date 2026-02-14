package com.cristhian.SistemaInventario.Service;

import com.cristhian.SistemaInventario.DTO.ImpuestoDTO;
import com.cristhian.SistemaInventario.Modelo.Impuesto;

import java.util.List;
import java.util.Optional;

public interface IImpuestoService {

    public List<Impuesto> listarImpuestosActivos();
    public Optional<Impuesto> buscarImpuestoPorId(int id);
    public void guardarImpuestoAutomatico();
    public void borrarImpuesto(int id);


}

