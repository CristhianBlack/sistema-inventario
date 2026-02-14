package com.cristhian.SistemaInventario.DTO;

import com.cristhian.SistemaInventario.Modelo.CompraPago;
import com.cristhian.SistemaInventario.Enums.EstadoPago;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class CompraPagoDTO {

    private int idCompraPago;
    private BigDecimal montoCompra;
    private LocalDateTime fechaPago;
    private EstadoPago estadoPago;
    private Integer idCompra;
    private Integer idFormaPago;
    private Integer numeroCuotas; // Si es pago a cuotas
    private LocalDateTime fechaVencimientoCuota; // Vencimiento de la siguiente cuota

    public CompraPagoDTO() {
    }

    public CompraPagoDTO(CompraPago compraPago){
        this.idCompraPago = compraPago.getIdCompraPago();
        this.montoCompra = compraPago.getMontoCompra();
        this.fechaPago = compraPago.getFechaPago();
        this.estadoPago = compraPago.getEstadoPago();
        this.numeroCuotas = compraPago.getNumeroCuotas();
        this.fechaVencimientoCuota = compraPago.getFechaVencimientoCuota();

        // Enviamos el ID para no exponer objetos completos
        this.idCompra = compraPago.getCompra() != null
                ? compraPago.getCompra().getIdCompra()
                : null;

        this.idFormaPago = compraPago.getFormaPago() != null
                ? compraPago.getFormaPago().getIdFormaPago()
                : null;
    }

    public int getIdCompraPago() {
        return idCompraPago;
    }

    public void setIdCompraPago(int idCompraPago) {
        this.idCompraPago = idCompraPago;
    }

    public BigDecimal getMontoCompra() {
        return montoCompra;
    }

    public void setMontoCompra(BigDecimal montoCompra) {
        this.montoCompra = montoCompra;
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

    public Integer getIdCompra() {
        return idCompra;
    }

    public void setIdCompra(Integer idCompra) {
        this.idCompra = idCompra;
    }

    public Integer getIdFormaPago() {
        return idFormaPago;
    }

    public void setIdFormaPago(Integer idFormaPago) {
        this.idFormaPago = idFormaPago;
    }

    public Integer getNumeroCuotas() {
        return numeroCuotas;
    }

    public void setNumeroCuotas(Integer numeroCuotas) {
        this.numeroCuotas = numeroCuotas;
    }

    public LocalDateTime getFechaVencimientoCuota() {
        return fechaVencimientoCuota;
    }

    public void setFechaVencimientoCuota(LocalDateTime fechaVencimientoCuota) {
        this.fechaVencimientoCuota = fechaVencimientoCuota;
    }
}
