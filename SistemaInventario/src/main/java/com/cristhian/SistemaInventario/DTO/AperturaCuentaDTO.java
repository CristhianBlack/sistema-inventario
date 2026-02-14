package com.cristhian.SistemaInventario.DTO;

import java.math.BigDecimal;
import java.time.LocalDate;

public class AperturaCuentaDTO {

    private Long idCuenta;
    private BigDecimal monto;
    private LocalDate fecha;
    private String descripcion;

    public AperturaCuentaDTO() {
    }

    public Long getIdCuenta() {
        return idCuenta;
    }

    public void setIdCuenta(Long idCuenta) {
        this.idCuenta = idCuenta;
    }

    public BigDecimal getMonto() {
        return monto;
    }

    public void setMonto(BigDecimal monto) {
        this.monto = monto;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
}
