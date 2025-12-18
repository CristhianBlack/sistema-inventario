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

public class DetalleVentaDTO {

    private int idDetalleVenta;
    private int cantidad;
    private double precioUnitario;// Precio del producto al momento de la venta
    private double descuento;// Descuento aplicado en la línea (valor absoluto)
    private double subtotalLinea;// Subtotal sin impuesto
    private double impuestoLinea;// Valor del impuesto aplicado en esta línea
    private double totalLinea;// Total final de la línea
    private Integer idVenta;
    private Integer idProducto;
    private Integer idImpuesto;

    public DetalleVentaDTO(DetalleVenta detalleVenta){
        this.idDetalleVenta = detalleVenta.getIdDetalleVenta();
        this.cantidad = detalleVenta.getCantidad();
        this.precioUnitario = detalleVenta.getPrecioUnitario();
        this.descuento = detalleVenta.getDescuento();
        this.subtotalLinea = detalleVenta.getSubtotalLinea();
        this.impuestoLinea = detalleVenta.getImpuestoLinea();
        this.totalLinea = detalleVenta.getTotalLinea();

        // Enviamos el ID para no exponer objetos completos
        this.idVenta = detalleVenta.getVenta() != null
                ? detalleVenta.getVenta().getIdVenta()
                : null;

        this.idProducto = detalleVenta.getProducto() != null
                ? detalleVenta.getProducto().getIdProducto()
                : null;

        this.idImpuesto = detalleVenta.getImpuesto() != null
                ? detalleVenta.getImpuesto().getIdImpuestos()
                : null;

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

    public double getTotalLinea() {
        return totalLinea;
    }

    public void setTotalLinea(double totalLinea) {
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
}
