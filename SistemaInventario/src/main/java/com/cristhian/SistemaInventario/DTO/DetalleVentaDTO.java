package com.cristhian.SistemaInventario.DTO;

import com.cristhian.SistemaInventario.Modelo.DetalleVenta;
import com.cristhian.SistemaInventario.Modelo.Impuesto;
import com.cristhian.SistemaInventario.Modelo.Producto;
import com.cristhian.SistemaInventario.Modelo.Venta;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.Column;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

import java.math.BigDecimal;

public class DetalleVentaDTO {

    private int idDetalleVenta;
    private int cantidad;
    private BigDecimal precioUnitario;// Precio del producto al momento de la venta
    private BigDecimal descuento;// Descuento aplicado en la línea (valor absoluto)
    private BigDecimal subtotalLinea;// Subtotal sin impuesto
    private BigDecimal
            impuestoLinea;// Valor del impuesto aplicado en esta línea
    private BigDecimal totalLinea;// Total final de la línea
    private Integer idVenta;
    private Integer idProducto;
    private Integer idImpuesto;
    private String nombreProducto;
    private BigDecimal porcentajeImpuesto;
    private BigDecimal costoUnitarioPromedio;

    public DetalleVentaDTO() {
    }

    public DetalleVentaDTO(DetalleVenta detalleVenta){
        this.idDetalleVenta = detalleVenta.getIdDetalleVenta();
        this.cantidad = detalleVenta.getCantidad();
        this.precioUnitario = detalleVenta.getPrecioUnitario();
        this.descuento = detalleVenta.getDescuento();
        this.subtotalLinea = detalleVenta.getSubtotalLinea();
        this.impuestoLinea = detalleVenta.getImpuestoLinea();
        this.totalLinea = detalleVenta.getTotalLinea();
        this.costoUnitarioPromedio = detalleVenta.getCostoUnitarioPromedio();

        // Enviamos el ID para no exponer objetos completos
        this.idVenta = detalleVenta.getVenta() != null
                ? detalleVenta.getVenta().getIdVenta()
                : null;

        this.idProducto = detalleVenta.getProducto() != null
                ? detalleVenta.getProducto().getIdProducto()
                : null;

        this.idImpuesto = detalleVenta.getImpuesto() != null
                ? detalleVenta.getImpuesto().getIdImpuesto()
                : null;

        if (detalleVenta.getProducto() != null) {
            this.idProducto = detalleVenta.getProducto().getIdProducto();
            this.nombreProducto = detalleVenta.getProducto().getNombreProducto();
        }

        if (detalleVenta.getImpuesto() != null) {
            this.idImpuesto = detalleVenta.getImpuesto().getIdImpuesto();
            this.porcentajeImpuesto = detalleVenta.getImpuesto().getPorcentaje();
        }

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

    public Integer getIdVenta() {
        return idVenta;
    }

    public void setIdVenta(Integer idVenta) {
        this.idVenta = idVenta;
    }

    public Integer getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(Integer idProducto) {
        this.idProducto = idProducto;
    }

    public Integer getIdImpuesto() {
        return idImpuesto;
    }

    public void setIdImpuesto(Integer idImpuesto) {
        this.idImpuesto = idImpuesto;
    }

    public String getNombreProducto() {
        return nombreProducto;
    }

    public void setNombreProducto(String nombreProducto) {
        this.nombreProducto = nombreProducto;
    }

    public BigDecimal getPorcentajeImpuesto() {
        return porcentajeImpuesto;
    }

    public void setPorcentajeImpuesto(BigDecimal porcentajeImpuesto) {
        this.porcentajeImpuesto = porcentajeImpuesto;
    }

    public BigDecimal getCostoUnitarioPromedio() {
        return costoUnitarioPromedio;
    }

    public void setCostoUnitarioPromedio(BigDecimal costoUnitarioPromedio) {
        this.costoUnitarioPromedio = costoUnitarioPromedio;
    }
}
