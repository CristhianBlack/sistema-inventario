package com.cristhian.SistemaInventario.Modelo;

import com.cristhian.SistemaInventario.DTO.DetalleCompraDTO;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.List;

@Entity
public class DetalleCompra {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idDetalleCompra;

    @Column(nullable = false)
    private int cantidad;


    // Precio del producto al momento de la venta
    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal precioUnitario;

    // Subtotal sin impuesto
    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal subtotalLinea;

    // Valor del impuesto aplicado en esta línea
    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal impuestoLinea;

    // Total final de la línea
    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal totalLinea;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_compra")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "detalles"})
    private Compra compra;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_producto")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Producto producto;

    public DetalleCompra() {
    }

    public DetalleCompra(DetalleCompraDTO detalleCompraDTO){
        this.cantidad = detalleCompraDTO.getCantidad();
        this.precioUnitario = detalleCompraDTO.getPrecioUnitario();
        this.subtotalLinea = detalleCompraDTO.getSubtotalLinea();
        this.impuestoLinea = detalleCompraDTO.getImpuestoLinea();
        this.totalLinea = detalleCompraDTO.getTotalLinea();
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

    public Compra getCompra() {
        return compra;
    }

    public void setCompra(Compra compra) {
        this.compra = compra;
    }

    public Producto getProducto() {
        return producto;
    }

    public void setProducto(Producto producto) {
        this.producto = producto;
    }
}
