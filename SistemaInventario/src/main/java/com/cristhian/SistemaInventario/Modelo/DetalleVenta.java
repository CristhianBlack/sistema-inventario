package com.cristhian.SistemaInventario.Modelo;

import com.cristhian.SistemaInventario.DTO.DetalleVentaDTO;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

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
    @Column(nullable = false)
    private double precioUnitario;

    // Descuento aplicado en la línea (valor absoluto)
    @Column(nullable = false)
    private double descuento;

    // Subtotal sin impuesto
    @Column(nullable = false)
    private double subtotalLinea;

    // Valor del impuesto aplicado en esta línea
    @Column(nullable = false)
    private double impuestoLinea;

    // Total final de la línea
    @Column(nullable = false)
    private double totalLinea;

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

    public double getPrecioUnitario() {
        return precioUnitario;
    }

    public void setPrecioUnitario(double precioUnitario) {
        this.precioUnitario = precioUnitario;
    }

    public double getDescuento() {
        return descuento;
    }

    public void setDescuento(double descuento) {
        this.descuento = descuento;
    }

    public double getTotalLinea() {
        return totalLinea;
    }

    public void setTotalLinea(double totalLinea) {
        this.totalLinea = totalLinea;
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

    public double getSubtotalLinea() {
        return subtotalLinea;
    }

    public void setSubtotalLinea(double subtotalLinea) {
        this.subtotalLinea = subtotalLinea;
    }

    public double getImpuestoLinea() {
        return impuestoLinea;
    }

    public void setImpuestoLinea(double impuestoLinea) {
        this.impuestoLinea = impuestoLinea;
    }
}
