package com.cristhian.SistemaInventario.DTO;

import com.cristhian.SistemaInventario.Modelo.DetalleCompra;
import jakarta.persistence.Column;

import java.math.BigDecimal;
import java.util.List;

public class DetalleCompraDTO {

    private int idDetalleCompra;
    private int cantidad;

    private BigDecimal precioUnitario;

    // Subtotal sin impuesto
    private BigDecimal subtotalLinea;

    // Valor del impuesto aplicado en esta línea
    private BigDecimal impuestoLinea;

    // Total final de la línea
    private BigDecimal totalLinea;

    private Integer idCompra;

    private Integer idProducto;


    private String nombreProducto;

    public DetalleCompraDTO() {
    }

    public DetalleCompraDTO(DetalleCompra detalleCompra){
        this.idDetalleCompra = detalleCompra.getIdDetalleCompra();
        this.cantidad = detalleCompra.getCantidad();
        this.precioUnitario = detalleCompra.getPrecioUnitario();
        this.subtotalLinea = detalleCompra.getSubtotalLinea();
        this.impuestoLinea = detalleCompra.getImpuestoLinea();
        this.totalLinea = detalleCompra.getTotalLinea();

        // Enviamos el ID para no exponer objetos completos
        this.idCompra = detalleCompra.getCompra() != null
                ? detalleCompra.getCompra().getIdCompra()
                : null;

        this.idProducto = detalleCompra.getProducto() != null
                ? detalleCompra.getProducto().getIdProducto()
                : null;

        // Traemos el nombre de los productos en el detalle
        this.nombreProducto = detalleCompra.getProducto() != null
                ? detalleCompra.getProducto().getNombreProducto()
                : null;

    }

    public int getIdDetalleCompra() {
        return idDetalleCompra;
    }

    public void setIdDetalleCompra(int idDetalleCompra) {
        this.idDetalleCompra = idDetalleCompra;
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

    public Integer getIdCompra() {
        return idCompra;
    }

    public void setIdCompra(Integer idCompra) {
        this.idCompra = idCompra;
    }

    public Integer getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(Integer idProducto) {
        this.idProducto = idProducto;
    }

    public String getNombreProducto() {
        return nombreProducto;
    }

    public void setNombreProducto(String nombreProducto) {
        this.nombreProducto = nombreProducto;
    }
}
