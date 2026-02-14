package com.cristhian.SistemaInventario.DTO;

import com.cristhian.SistemaInventario.Modelo.FormaPago;
import com.cristhian.SistemaInventario.Enums.NombreFormaPago;
import com.cristhian.SistemaInventario.Enums.TipoPago;

public class FormaPagoDTO {

    private int idFormaPago;
    private NombreFormaPago nombreFormaPago;
    private boolean permiteCuotas;
    private TipoPago tipoPago;
    private boolean requiereConfirmacion = true;
    private boolean activo = true;

    public FormaPagoDTO(){}

    public FormaPagoDTO(FormaPago formaPago) {
        this.idFormaPago = formaPago.getIdFormaPago();
        this.nombreFormaPago = formaPago.getNombreFormaPago();
        this.permiteCuotas = formaPago.isPermiteCuotas();
        this.tipoPago = formaPago.getTipoPago();
        this.requiereConfirmacion = formaPago.isRequiereConfirmacion();
        this.activo = formaPago.isActivo();
    }

    public int getIdFormaPago() {
        return idFormaPago;
    }

    public void setIdFormaPago(int idFormaPago) {
        this.idFormaPago = idFormaPago;
    }

    public NombreFormaPago getNombreFormaPago() {
        return nombreFormaPago;
    }

    public void setNombreFormaPago(NombreFormaPago nombreFormaPago) {
        this.nombreFormaPago = nombreFormaPago;
    }

    public boolean isPermiteCuotas() {
        return permiteCuotas;
    }

    public void setPermiteCuotas(boolean permiteCuotas) {
        this.permiteCuotas = permiteCuotas;
    }

    public TipoPago getTipoPago() {
        return tipoPago;
    }

    public void setTipoPago(TipoPago tipoPago) {
        this.tipoPago = tipoPago;
    }

    public boolean isRequiereConfirmacion() {
        return requiereConfirmacion;
    }

    public void setRequiereConfirmacion(boolean requiereConfirmacion) {
        this.requiereConfirmacion = requiereConfirmacion;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }
}
