package com.cristhian.SistemaInventario.DTO;

import com.cristhian.SistemaInventario.Modelo.DetalleCompra;

import java.util.List;

public class DetalleCompraDTO {

    private int idDetalleCompra;
    private int cantidad;

    private double precioUnitario;

    private double subTotal;

    private Integer idCompra;

    private Integer idProducto;


    private String nombreProducto;

    public DetalleCompraDTO() {
    }

    public DetalleCompraDTO(DetalleCompra detalleCompra){
        this.idDetalleCompra = detalleCompra.getIdDetalleCompra();
        this.cantidad = detalleCompra.getCantidad();
        this.precioUnitario = detalleCompra.getPrecioUnitario();
        this.subTotal = detalleCompra.getSubTotal();

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

    public double getPrecioUnitario() {
        return precioUnitario;
    }

    public void setPrecioUnitario(double precioUnitario) {
        this.precioUnitario = precioUnitario;
    }

    public double getSubTotal() {
        return subTotal;
    }

    public void setSubTotal(double subTotal) {
        this.subTotal = subTotal;
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
