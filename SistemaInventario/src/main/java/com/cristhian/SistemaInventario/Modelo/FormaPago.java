package com.cristhian.SistemaInventario.Modelo;

import com.cristhian.SistemaInventario.DTO.FormaPagoDTO;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
public class FormaPago {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idFormaPago;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private NombreFormaPago nombreFormaPago;

    @Column(nullable = false)
    private boolean permiteCuotas;

    @Enumerated(EnumType.STRING)
    private TipoPago tipoPago;

    @Column(nullable = false)
    private boolean requiereConfirmacion = true;

    private boolean activo = true;

    @OneToMany(mappedBy = "formaPago", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnoreProperties("formaPago") // evita recursión infinita
    private List<VentaPago> ventaPagos = new ArrayList<>();

    @OneToMany(mappedBy = "formaPago", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnoreProperties("formaPago") // evita recursión infinita
    private List<CompraPago> compraPagos = new ArrayList<>();

    public FormaPago() {
    }

    public FormaPago(FormaPagoDTO formaDto){
        this.nombreFormaPago = formaDto.getNombreFormaPago();
        this.permiteCuotas = formaDto.isPermiteCuotas();
        this.tipoPago = formaDto.getTipoPago();
        this.requiereConfirmacion = formaDto.isRequiereConfirmacion();
        this.activo = formaDto.isActivo();
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

    public List<VentaPago> getVentaPagos() {
        return ventaPagos;
    }

    public void setVentaPagos(List<VentaPago> ventaPagos) {
        this.ventaPagos = ventaPagos;
    }

    public boolean isPermiteCuotas() {
        return permiteCuotas;
    }

    public void setPermiteCuotas(boolean permiteCuotas) {
        this.permiteCuotas = permiteCuotas;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
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
}
