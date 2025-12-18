package com.cristhian.SistemaInventario.DTO;

import com.cristhian.SistemaInventario.Modelo.Compra;

import com.fasterxml.jackson.annotation.JsonAlias;


import java.time.LocalDate;
import java.util.List;

public class CompraDTO {

    private int idCompra;
    private LocalDate FechaCompra;
    private double total;
    private boolean activo = true;

    @JsonAlias({ "proveedor", "idProveedor" })
    private Integer idProveedor;

    private List<DetalleCompraDTO> detalles;

    public CompraDTO() {
    }

    public CompraDTO(Compra compra){
        this.idCompra = compra.getIdCompra();
        this.total = compra.getTotal();
        this.FechaCompra = compra.getFechaCompra();
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

    public LocalDate getFechaCompra() {
        return FechaCompra;
    }

    public void setFechaCompra(LocalDate fechaCompra) {
        FechaCompra = fechaCompra;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
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
}
