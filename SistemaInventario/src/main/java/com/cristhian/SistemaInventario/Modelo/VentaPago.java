package com.cristhian.SistemaInventario.Modelo;

import com.cristhian.SistemaInventario.DTO.VentaPagoDTO;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
public class VentaPago {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idVentaPago;

    @Column(nullable = false)
    private double monto;

    @Column(nullable = false)
    private LocalDate fechaPago;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoPago estadoPago;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_venta")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "ventaPagos"})
    private Venta venta;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_forma_pago")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "ventaPagos"})
    private FormaPago formaPago;

    @Column(nullable = true)
    private int numeroCuotas; // Si es pago a cuotas

    @Column(nullable = true)
    private LocalDate fechaVencimientoCuota; // Vencimiento de la siguiente cuota

    public VentaPago() {
    }

    public VentaPago(VentaPagoDTO ventaPagoDto) {
        this.monto = ventaPagoDto.getMonto();
        this.fechaPago = ventaPagoDto.getFechaPago();
        this.estadoPago = ventaPagoDto.getEstadoPago();
        this.numeroCuotas = ventaPagoDto.getNumeroCuotas();
        this.fechaVencimientoCuota = ventaPagoDto.getFechaVencimientoCuota();
    }

    public int getIdVentaPago() {
        return idVentaPago;
    }

    public void setIdVentaPago(int idVentaPago) {
        this.idVentaPago = idVentaPago;
    }

    public double getMonto() {
        return monto;
    }

    public void setMonto(double monto) {
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

    public Venta getVenta() {
        return venta;
    }

    public void setVenta(Venta venta) {
        this.venta = venta;
    }

    public FormaPago getFormaPago() {
        return formaPago;
    }

    public void setFormaPago(FormaPago formaPago) {
        this.formaPago = formaPago;
    }

    public int getNumeroCuotas() {
        return numeroCuotas;
    }

    public void setNumeroCuotas(int numeroCuotas) {
        this.numeroCuotas = numeroCuotas;
    }

    public LocalDate getFechaVencimientoCuota() {
        return fechaVencimientoCuota;
    }

    public void setFechaVencimientoCuota(LocalDate fechaVencimientoCuota) {
        this.fechaVencimientoCuota = fechaVencimientoCuota;
    }
}
