package com.cristhian.SistemaInventario.DTO;

import com.cristhian.SistemaInventario.Modelo.Impuesto;
import com.cristhian.SistemaInventario.Enums.TipoImpuesto;

import java.math.BigDecimal;

public class ImpuestoDTO {

    private int idImpuesto;

    private BigDecimal porcentaje;
    private TipoImpuesto tipoImpuesto;
    private boolean activo;

    public ImpuestoDTO(){}

    public ImpuestoDTO(Impuesto impuesto){
        this.idImpuesto = impuesto.getIdImpuesto();
        this.porcentaje = impuesto.getPorcentaje();
        this.tipoImpuesto = impuesto.getTipoImpuesto();
        this.activo = impuesto.isActivo();

    }

    public int getIdImpuesto() {
        return idImpuesto;
    }

    public void setIdImpuesto(int idImpuesto) {
        this.idImpuesto = idImpuesto;
    }

    public BigDecimal getPorcentaje() {
        return porcentaje;
    }

    public void setPorcentaje(BigDecimal porcentaje) {
        this.porcentaje = porcentaje;
    }

    public TipoImpuesto getTipoImpuesto() {
        return tipoImpuesto;
    }

    public void setTipoImpuesto(TipoImpuesto tipoImpuesto) {
        this.tipoImpuesto = tipoImpuesto;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }
}
