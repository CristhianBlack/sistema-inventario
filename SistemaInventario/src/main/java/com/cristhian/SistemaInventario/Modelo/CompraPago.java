package com.cristhian.SistemaInventario.Modelo;

import com.cristhian.SistemaInventario.DTO.CompraPagoDTO;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
public class CompraPago {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idCompraPago;

    @Column(nullable = false)
    private BigDecimal montoCompra;

    @Column(nullable = false)
    private LocalDateTime fechaPago;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoPago estadoPago;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_compra")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "compraPagos"})
    private Compra compra;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_forma_pago")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "compraPagos"})
    private FormaPago formaPago;

    @Column(nullable = true)
    private Integer numeroCuotas; // Si es pago a cuotas

    @Column(nullable = true)
    private LocalDateTime fechaVencimientoCuota; // Vencimiento de la siguiente cuota

    public CompraPago() {
    }

    public CompraPago(CompraPagoDTO compraPagoDTO){
        this.montoCompra = compraPagoDTO.getMontoCompra();
        this.fechaPago = compraPagoDTO.getFechaPago();
        this.estadoPago = compraPagoDTO.getEstadoPago();
        this.numeroCuotas = compraPagoDTO.getNumeroCuotas();
        this.fechaVencimientoCuota = compraPagoDTO.getFechaVencimientoCuota();
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

    public Compra getCompra() {
        return compra;
    }

    public void setCompra(Compra compra) {
        this.compra = compra;
    }

    public FormaPago getFormaPago() {
        return formaPago;
    }

    public void setFormaPago(FormaPago formaPago) {
        this.formaPago = formaPago;
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
