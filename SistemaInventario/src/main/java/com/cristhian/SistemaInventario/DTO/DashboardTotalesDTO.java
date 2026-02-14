package com.cristhian.SistemaInventario.DTO;

import java.math.BigDecimal;

public class DashboardTotalesDTO {

    private Long totalPersonas;
    private Long totalProveedores;
    private Long totalProductos;
    private Long totalCompras;
    private Long totalMovimientos;
    private Long totalAsientos;

    private Long totalVentas;

    private BigDecimal totalComprasMes;
    private BigDecimal valorInventario;
    private BigDecimal totalVentaMes;

    public DashboardTotalesDTO() {
    }

    public Long getTotalPersonas() {
        return totalPersonas;
    }

    public void setTotalPersonas(Long totalPersonas) {
        this.totalPersonas = totalPersonas;
    }

    public Long getTotalProveedores() {
        return totalProveedores;
    }

    public void setTotalProveedores(Long totalProveedores) {
        this.totalProveedores = totalProveedores;
    }

    public Long getTotalProductos() {
        return totalProductos;
    }

    public void setTotalProductos(Long totalProductos) {
        this.totalProductos = totalProductos;
    }

    public Long getTotalCompras() {
        return totalCompras;
    }

    public void setTotalCompras(Long totalCompras) {
        this.totalCompras = totalCompras;
    }

    public Long getTotalMovimientos() {
        return totalMovimientos;
    }

    public void setTotalMovimientos(Long totalMovimientos) {
        this.totalMovimientos = totalMovimientos;
    }

    public Long getTotalAsientos() {
        return totalAsientos;
    }

    public void setTotalAsientos(Long totalAsientos) {
        this.totalAsientos = totalAsientos;
    }

    public BigDecimal getTotalComprasMes() {
        return totalComprasMes;
    }

    public void setTotalComprasMes(BigDecimal totalComprasMes) {
        this.totalComprasMes = totalComprasMes;
    }

    public BigDecimal getValorInventario() {
        return valorInventario;
    }

    public void setValorInventario(BigDecimal valorInventario) {
        this.valorInventario = valorInventario;
    }

    public Long getTotalVentas() {
        return totalVentas;
    }

    public void setTotalVentas(Long totalVentas) {
        this.totalVentas = totalVentas;
    }

    public BigDecimal getTotalVentaMes() {
        return totalVentaMes;
    }

    public void setTotalVentaMes(BigDecimal totalVentaMes) {
        this.totalVentaMes = totalVentaMes;
    }
}
