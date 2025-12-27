package com.cristhian.SistemaInventario.DTO;

import com.cristhian.SistemaInventario.Modelo.Categoria;
import com.cristhian.SistemaInventario.Modelo.Producto;
import com.cristhian.SistemaInventario.Modelo.Proveedor;
import com.cristhian.SistemaInventario.Modelo.UnidadMedida;
import com.fasterxml.jackson.annotation.JsonAlias;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Date;

public class ProductoDTO {

    private int idProducto;
    private String codigoProducto;

    private  String nombreProducto;

    private BigDecimal precioCompra;

    private double precioVenta;

    private int stock;

    private int stockMinimo;

    private String descripcion;

    private LocalDate fechaCreacion;

    private boolean activo;

    @JsonAlias({ "categoria", "idCategoria" })
    private Integer idCategoria;

    @JsonAlias({ "unidadMedida", "idUnidadMeida" })
    private Integer idUnidadMedida;

    @JsonAlias({ "proveedor", "idProveedor" })
    private Integer idProveedor;
    @JsonAlias({ "impuesto", "idImpuesto" })
    private Integer idImpuesto;

    public ProductoDTO() {
    }

    public ProductoDTO(Producto producto){
        this.idProducto = producto.getIdProducto();
        this.codigoProducto = producto.getCodigoProducto();
        this.nombreProducto = producto.getNombreProducto();
        this.precioCompra = producto.getPrecioCompra();
        this.precioVenta = producto.getPrecioVenta();
        this.stock = producto.getStock();
        this.stockMinimo = producto.getStockMinimo();
        this.descripcion = producto.getDescripcion();
        this.fechaCreacion = producto.getFechaCreacion();
        this.activo = producto.isActivo();

        // Enviamos el ID para no exponer objetos completos
        this.idCategoria = producto.getCategoria() != null
                ? producto.getCategoria().getIdCategoria()
                : null;

        this.idUnidadMedida = producto.getUnidadMedida() != null
                ? producto.getUnidadMedida().getIdUnidadMedida()
                : null;

        this.idProveedor = producto.getProveedor() != null
                ? producto.getProveedor().getIdProveedor()
                : null;

        this.idImpuesto = producto.getImpuesto() != null
                ? producto.getImpuesto().getIdImpuesto()
                : null;
    }

    public int getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(int idProducto) {
        this.idProducto = idProducto;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    public String getCodigoProducto() {
        return codigoProducto;
    }

    public void setCodigoProducto(String codigoProducto) {
        this.codigoProducto = codigoProducto;
    }

    public String getNombreProducto() {
        return nombreProducto;
    }

    public void setNombreProducto(String nombreProducto) {
        this.nombreProducto = nombreProducto;
    }

    public BigDecimal getPrecioCompra() {
        return precioCompra;
    }

    public void setPrecioCompra(BigDecimal precioCompra) {
        this.precioCompra = precioCompra;
    }

    public double getPrecioVenta() {
        return precioVenta;
    }

    public void setPrecioVenta(double precioVenta) {
        this.precioVenta = precioVenta;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public int getStockMinimo() {
        return stockMinimo;
    }

    public void setStockMinimo(int stockMinimo) {
        this.stockMinimo = stockMinimo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public LocalDate getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDate fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public boolean isActive() {
        return activo;
    }

    public void setActive(boolean activo) {
        activo = activo;
    }

    public Integer getIdCategoria() {
        return idCategoria;
    }

    public void setIdCategoria(Integer idCategoria) {
        this.idCategoria = idCategoria;
    }

    public Integer getIdUnidadMedida() {
        return idUnidadMedida;
    }

    public void setIdUnidadMedida(Integer idUnidadMedida) {
        this.idUnidadMedida = idUnidadMedida;
    }

    public Integer getIdProveedor() {
        return idProveedor;
    }

    public void setIdProveedor(Integer idProveedor) {
        this.idProveedor = idProveedor;
    }

    public Integer getIdImpuesto() {
        return idImpuesto;
    }

    public void setIdImpuesto(Integer idImpuesto) {
        this.idImpuesto = idImpuesto;
    }
}
