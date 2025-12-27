package com.cristhian.SistemaInventario.DTO;

import com.cristhian.SistemaInventario.Modelo.Compra;

import com.cristhian.SistemaInventario.Modelo.EstadoCompra;
import com.fasterxml.jackson.annotation.JsonAlias;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;


import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class CompraDTO {

    private int idCompra;
    private LocalDateTime fechaCompra;
    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal subTotalCompra;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal totalImpuestos;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal totalCompra;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false, length = 20)
    private EstadoCompra estado; // Pendiente, parcial , pagada
    private boolean activo = true;

    @JsonAlias({ "proveedor", "idProveedor" })
    private Integer idProveedor;

    private List<DetalleCompraDTO> detalles;

    private List<CompraPagoDTO> pagos;

    public CompraDTO() {
    }

    public CompraDTO(Compra compra){
        this.idCompra = compra.getIdCompra();
        this.fechaCompra = compra.getFechaCompra();
        this.subTotalCompra = compra.getSubTotalCompra();
        this.totalImpuestos = compra.getTotalImpuestos();
        this.totalCompra = compra.getTotalCompra();
        this.estado = compra.getEstado();
        this.activo = compra.isActivo();

        // Enviamos el ID para no exponer objetos completos
        this.idProveedor = compra.getProveedor() != null
                ? compra.getProveedor().getIdProveedor()
                : null;

        this.detalles = compra.getDetalles() != null
                ? compra.getDetalles().stream()
                .map(DetalleCompraDTO::new)
                .toList()
                : null;

    }

    public int getIdCompra() {
        return idCompra;
    }

    public void setIdCompra(int idCompra) {
        this.idCompra = idCompra;
    }

    public LocalDateTime getFechaCompra() {
        return fechaCompra;
    }

    public void setFechaCompra(LocalDateTime fechaCompra) {
        fechaCompra = fechaCompra;
    }

    public BigDecimal getSubTotalCompra() {
        return subTotalCompra;
    }

    public void setSubTotalCompra(BigDecimal subTotalCompra) {
        this.subTotalCompra = subTotalCompra;
    }

    public BigDecimal getTotalImpuestos() {
        return totalImpuestos;
    }

    public void setTotalImpuestos(BigDecimal totalImpuestos) {
        this.totalImpuestos = totalImpuestos;
    }

    public BigDecimal getTotalCompra() {
        return totalCompra;
    }

    public void setTotalCompra(BigDecimal totalCompra) {
        this.totalCompra = totalCompra;
    }

    public EstadoCompra getEstado() {
        return estado;
    }

    public void setEstado(EstadoCompra estado) {
        this.estado = estado;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    public Integer getIdProveedor() {
        return idProveedor;
    }

    public void setIdProveedor(Integer idProveedor) {
        this.idProveedor = idProveedor;
    }

    public List<DetalleCompraDTO> getDetalles() {
        return detalles;
    }

    public void setDetalles(List<DetalleCompraDTO> detalles) {
        this.detalles = detalles;
    }

    public List<CompraPagoDTO> getPagos() {
        return pagos;
    }

    public void setPagos(List<CompraPagoDTO> pagos) {
        this.pagos = pagos;
    }
}
