package com.cristhian.SistemaInventario.DTO;

import com.cristhian.SistemaInventario.Modelo.Impuesto;
import jakarta.persistence.Column;

public class ImpuestoDTO {

    private int idImpuestos;
    private String nombreImpuesto;
    private double valorImpuesto;
    private String tipoImpuesto;
    private boolean activo;

    public ImpuestoDTO(){}

    public ImpuestoDTO(Impuesto impuesto){
        this.idImpuestos = impuesto.getIdImpuestos();
        this.nombreImpuesto = impuesto.getNombreImpuesto();
        this.valorImpuesto = impuesto.getValorImpuesto();
        this.tipoImpuesto = impuesto.getTipoImpuesto();
        this.activo = impuesto.isActivo();

    }

    public int getIdImpuestos() {
        return idImpuestos;
    }

    public void setIdImpuestos(int idImpuestos) {
        this.idImpuestos = idImpuestos;
    }

    public String getNombreImpuesto() {
        return nombreImpuesto;
    }

    public void setNombreImpuesto(String nombreImpuesto) {
        this.nombreImpuesto = nombreImpuesto;
    }

    public double getValorImpuesto() {
        return valorImpuesto;
    }

    public void setValorImpuesto(double valorImpuesto) {
        this.valorImpuesto = valorImpuesto;
    }

    public String getTipoImpuesto() {
        return tipoImpuesto;
    }

    public void setTipoImpuesto(String tipoImpuesto) {
        this.tipoImpuesto = tipoImpuesto;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }
}
