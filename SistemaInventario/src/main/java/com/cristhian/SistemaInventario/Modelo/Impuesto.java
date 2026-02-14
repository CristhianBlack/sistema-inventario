package com.cristhian.SistemaInventario.Modelo;

import com.cristhian.SistemaInventario.DTO.ImpuestoDTO;
import com.cristhian.SistemaInventario.Enums.TipoImpuesto;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Impuesto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idImpuesto;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoImpuesto tipoImpuesto;


    // 0.19 = 19%
    @Column(nullable = false, precision = 5, scale = 4)
    private BigDecimal porcentaje;

    private boolean activo;

    @OneToMany(mappedBy = "impuesto", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnoreProperties("impuesto") //  evita recursión infinita
    private List<Producto> productos = new ArrayList<>();

    /*@OneToMany(mappedBy = "impuesto", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnoreProperties("impuesto") //  evita recursión infinita
    private List<Venta> ventas = new ArrayList<>();

    @OneToMany(mappedBy = "impuesto", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnoreProperties("impuesto") //  evita recursión infinita
    private List<DetalleVenta> detalleVentas = new ArrayList<>();*/

    public Impuesto() {
    }

    public Impuesto(ImpuestoDTO impuestoDTO){
        this.tipoImpuesto = impuestoDTO.getTipoImpuesto();
        this.porcentaje = impuestoDTO.getPorcentaje();
        this.activo = impuestoDTO.isActivo();
    }

    public int getIdImpuesto() {
        return idImpuesto;
    }

    public void setIdImpuesto(int idImpuestos) {
        this.idImpuesto = idImpuesto;
    }

    public TipoImpuesto getTipoImpuesto() {
        return tipoImpuesto;
    }

    public void setTipoImpuesto(TipoImpuesto tipoImpuesto) {
        this.tipoImpuesto = tipoImpuesto;
    }

    public BigDecimal getPorcentaje() {
        return porcentaje;
    }

    public void setPorcentaje(BigDecimal porcentaje) {
        this.porcentaje = porcentaje;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }
}

