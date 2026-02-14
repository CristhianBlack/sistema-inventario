package com.cristhian.SistemaInventario.Modelo;

import com.cristhian.SistemaInventario.DTO.DetalleVentaDTO;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
public class DetalleVenta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idDetalleVenta;

    @Column(nullable = false)
    private int cantidad;

    // Precio del producto al momento de la venta
    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal precioUnitario;

    // Descuento aplicado en la línea (valor absoluto)
    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal descuento;

    // Subtotal sin impuesto
    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal subtotalLinea;

    // Valor del impuesto aplicado en esta línea
    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal impuestoLinea;

    // Total final de la línea
    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal totalLinea;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal costoUnitarioPromedio;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_venta")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "detalleVentas"})
    private Venta venta;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_producto")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "detalleVentas"})
    private Producto producto;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_impuesto")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "detalleVentas"})
    private Impuesto impuesto;

    public DetalleVenta() {
    }

    public DetalleVenta(DetalleVentaDTO detalleVentaDto) {
        this.cantidad = detalleVentaDto.getCantidad();
        this.precioUnitario = detalleVentaDto.getPrecioUnitario();
        this.descuento = detalleVentaDto.getDescuento();
        this.subtotalLinea = detalleVentaDto.getSubtotalLinea();
        this.impuestoLinea = detalleVentaDto.getImpuestoLinea();
        this.totalLinea = detalleVentaDto.getTotalLinea();
        this.costoUnitarioPromedio = detalleVentaDto.getCostoUnitarioPromedio();
    }

    public int getIdDetalleVenta() {
        return idDetalleVenta;
    }

    public void setIdDetalleVenta(int idDetalleVenta) {
        this.idDetalleVenta = idDetalleVenta;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }



    public Venta getVenta() {
        return venta;
    }

    public void setVenta(Venta venta) {
        this.venta = venta;
    }

    public Producto getProducto() {
        return producto;
    }

    public void setProducto(Producto producto) {
        this.producto = producto;
    }

    public Impuesto getImpuesto() {
        return impuesto;
    }

    public void setImpuesto(Impuesto impuesto) {
        this.impuesto = impuesto;
    }

    public BigDecimal getPrecioUnitario() {
        return precioUnitario;
    }

    public void setPrecioUnitario(BigDecimal precioUnitario) {
        this.precioUnitario = precioUnitario;
    }

    public BigDecimal getDescuento() {
        return descuento;
    }

    public void setDescuento(BigDecimal descuento) {
        this.descuento = descuento;
    }

    public BigDecimal getSubtotalLinea() {
        return subtotalLinea;
    }

    public void setSubtotalLinea(BigDecimal subtotalLinea) {
        this.subtotalLinea = subtotalLinea;
    }

    public BigDecimal getImpuestoLinea() {
        return impuestoLinea;
    }

    public void setImpuestoLinea(BigDecimal impuestoLinea) {
        this.impuestoLinea = impuestoLinea;
    }

    public BigDecimal getTotalLinea() {
        return totalLinea;
    }

    public void setTotalLinea(BigDecimal totalLinea) {
        this.totalLinea = totalLinea;
    }

    public BigDecimal getCostoUnitarioPromedio() {
        return costoUnitarioPromedio;
    }

    public void setCostoUnitarioPromedio(BigDecimal costoUnitarioPromedio) {
        this.costoUnitarioPromedio = costoUnitarioPromedio;
    }
}
