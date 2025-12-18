package com.cristhian.SistemaInventario.Modelo;

import com.cristhian.SistemaInventario.DTO.ImpuestoDTO;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
public class Impuesto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idImpuestos;

    @Column(nullable = false)
    private String nombreImpuesto;

    @Column(nullable = false)
    private double valorImpuesto;

    @Column(nullable = false)
    private String tipoImpuesto;

    private boolean activo;

    /*@OneToMany(mappedBy = "impuesto", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnoreProperties("impuesto") //  evita recursión infinita
    private List<Venta> ventas = new ArrayList<>();

    @OneToMany(mappedBy = "impuesto", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnoreProperties("impuesto") //  evita recursión infinita
    private List<DetalleVenta> detalleVentas = new ArrayList<>();*/

    public Impuesto() {
    }

    public Impuesto(ImpuestoDTO impuestoDTO){
        this.nombreImpuesto = impuestoDTO.getNombreImpuesto();
        this.valorImpuesto = impuestoDTO.getValorImpuesto();
        this.tipoImpuesto = impuestoDTO.getTipoImpuesto();
        this.activo = impuestoDTO.isActivo();
    }

    public int getIdImpuestos() {
        return idImpuestos;
    }

    public void setIdImpuestos(int idImpuestos) {
        this.idImpuestos = idImpuestos;
    }

    public double getValorImpuesto() {
        return valorImpuesto;
    }

    public void setValorImpuesto(double valorImpuesto) {
        this.valorImpuesto = valorImpuesto;
    }

   /* public List<Venta> getVentas() {
        return ventas;
    }

    public void setVentas(List<Venta> ventas) {
        this.ventas = ventas;
    }*/

    public String getNombreImpuesto() {
        return nombreImpuesto;
    }

    public void setNombreImpuesto(String nombreImpuesto) {
        this.nombreImpuesto = nombreImpuesto;
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

