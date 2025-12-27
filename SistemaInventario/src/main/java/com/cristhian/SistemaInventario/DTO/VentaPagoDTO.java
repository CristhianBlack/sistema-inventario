package com.cristhian.SistemaInventario.DTO;

import com.cristhian.SistemaInventario.Modelo.EstadoPago;
import com.cristhian.SistemaInventario.Modelo.FormaPago;
import com.cristhian.SistemaInventario.Modelo.Venta;
import com.cristhian.SistemaInventario.Modelo.VentaPago;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;

public class VentaPagoDTO {

    private int idVentaPago;
    private BigDecimal monto;
    private LocalDate fechaPago;
    private EstadoPago estadoPago;
    private Integer numeroCuotas; // Si es pago a cuotas
    private LocalDate fechaVencimientoCuota; // Vencimiento de la siguiente cuota
    private Integer idVenta;
    private Integer idFormaPago;

    public VentaPagoDTO(){}

    public VentaPagoDTO(VentaPago ventaPago){
        this.idVentaPago = ventaPago.getIdVentaPago();
        this.monto = ventaPago.getMonto();
        this.fechaPago = ventaPago.getFechaPago();
        this.estadoPago = ventaPago.getEstadoPago();
        this.numeroCuotas = ventaPago.getNumeroCuotas();
        this.fechaVencimientoCuota = ventaPago.getFechaVencimientoCuota();

        // Enviamos el ID para no exponer objetos completos
        this.idVenta = ventaPago.getVenta() != null
                ? ventaPago.getVenta().getIdVenta()
                : null;

        this.idFormaPago = ventaPago.getFormaPago() != null
                ? ventaPago.getFormaPago().getIdFormaPago()
                : null;
    }

    public int getIdVentaPago() {
        return idVentaPago;
    }

    public void setIdVentaPago(int idVentaPago) {
        this.idVentaPago = idVentaPago;
    }

    public BigDecimal getMonto() {
        return monto;
    }

    public void setMonto(BigDecimal monto) {
        this.monto = monto;
    }

    public LocalDate getFechaPago() {
        return fechaPago;
    }

    public void setFechaPago(LocalDate fechaPago) {
        this.fechaPago = fechaPago;
    }

    public EstadoPago getEstadoPago() {
        return estadoPago;
    }

    public void setEstadoPago(EstadoPago estadoPago) {
        this.estadoPago = estadoPago;
    }

    public Integer getNumeroCuotas() {
        return numeroCuotas;
    }

    public void setNumeroCuotas(Integer numeroCuotas) {
        this.numeroCuotas = numeroCuotas;
    }

    public LocalDate getFechaVencimientoCuota() {
        return fechaVencimientoCuota;
    }

    public void setFechaVencimientoCuota(LocalDate fechaVencimientoCuota) {
        this.fechaVencimientoCuota = fechaVencimientoCuota;
    }

    public Integer getIdVenta() {
        return idVenta;
    }

    public void setIdVenta(Integer idVenta) {
        this.idVenta = idVenta;
    }

    public Integer getIdFormaPago() {
        return idFormaPago;
    }

    public void setIdFormaPago(Integer idFormaPago) {
        this.idFormaPago = idFormaPago;
    }
}
