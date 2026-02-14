package com.cristhian.SistemaInventario.DTO;

import com.cristhian.SistemaInventario.Enums.EstadoPago;
import com.cristhian.SistemaInventario.Modelo.VentaPago;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class VentaPagoDTO {

    private int idVentaPago;
    private BigDecimal monto;
    private LocalDateTime fechaPago;
    private EstadoPago estadoPago;
    private Integer numeroCuotas; // Si es pago a cuotas
    private LocalDate fechaVencimientoCuota; // Vencimiento de la siguiente cuota
    private Integer idVenta;
    private Integer idFormaPago;
    private String nombreFormaPago;


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

        //this.formaPago = new FormaPagoDTO(ventaPago.getFormaPago());

        // Traemos el nombre de la forma de pago en el detalle
        this.nombreFormaPago = ventaPago.getFormaPago() != null
                ? String.valueOf(ventaPago.getFormaPago().getNombreFormaPago())
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

    public LocalDateTime getFechaPago() {
        return fechaPago;
    }

    public void setFechaPago(LocalDateTime fechaPago) {
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

    public String getNombreFormaPago() {
        return nombreFormaPago;
    }

    public void setNombreFormaPago(String nombreFormaPago) {
        this.nombreFormaPago = nombreFormaPago;
    }
}
