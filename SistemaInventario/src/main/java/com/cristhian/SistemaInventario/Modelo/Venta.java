package com.cristhian.SistemaInventario.Modelo;

import com.cristhian.SistemaInventario.DTO.VentaDTO;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Venta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idVenta;

    @CreationTimestamp
    @Column(nullable = false)
    private LocalDateTime fechaVenta;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal subTotalVenta;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal totalImpuestos;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal totalVenta;
    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal saldoAplicado = BigDecimal.ZERO;
    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal totalPagar;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false, length = 20)
    private EstadoVenta estado; // Pendiente, parcial , pagada

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_persona")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "ventas"})
    private Persona persona;

    @OneToMany(mappedBy = "venta", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnoreProperties("venta") // evita recursión infinita
    private List<VentaPago> ventaPagos = new ArrayList<>();

    @OneToMany(mappedBy = "venta", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnoreProperties("venta") // evita recursión infinita
    private List<DetalleVenta> detalleVentas = new ArrayList<>();

    @OneToMany(mappedBy = "venta", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnoreProperties("venta") // evita recursión infinita
    private List<Devolucion> devoluciones = new ArrayList<>();


    public Venta() {
    }

    public Venta(VentaDTO ventaDTO){
        this.fechaVenta = ventaDTO.getFechaVenta();
        this.subTotalVenta = ventaDTO.getSubTotalVenta();
        this.totalImpuestos = ventaDTO.getTotalImpuestos();
        this.totalVenta = ventaDTO.getTotalVenta();
        this.estado = ventaDTO.getEstado();
        this.saldoAplicado = ventaDTO.getSaldoAplicado();
        this.totalPagar = ventaDTO.getTotalPagar();
    }

    public int getIdVenta() {
        return idVenta;
    }

    public void setIdVenta(int idVenta) {
        this.idVenta = idVenta;
    }

    public LocalDateTime getFechaVenta() {
        return fechaVenta;
    }

    public void setFechaVenta(LocalDateTime fechaVenta) {
        this.fechaVenta = fechaVenta;
    }

    public BigDecimal getSubTotalVenta() {
        return subTotalVenta;
    }

    public void setSubTotalVenta(BigDecimal subTotalVenta) {
        this.subTotalVenta = subTotalVenta;
    }

    public BigDecimal getTotalImpuestos() {
        return totalImpuestos;
    }

    public void setTotalImpuestos(BigDecimal totalImpuestos) {
        this.totalImpuestos = totalImpuestos;
    }

    public BigDecimal getTotalVenta() {
        return totalVenta;
    }

    public void setTotalVenta(BigDecimal totalVenta) {
        this.totalVenta = totalVenta;
    }

    public EstadoVenta getEstado() {
        return estado;
    }

    public void setEstado(EstadoVenta estado) {
        this.estado = estado;
    }

    /* public Impuesto getImpuesto() {
        return impuesto;
    }

    public void setImpuesto(Impuesto impuesto) {
        this.impuesto = impuesto;
    }*/

    public Persona getPersona() {
        return persona;
    }

    public void setPersona(Persona persona) {
        this.persona = persona;
    }

    public List<VentaPago> getVentaPagos() {
        return ventaPagos  == null ? new ArrayList<>() : ventaPagos;
    }

    public void setVentaPagos(List<VentaPago> ventaPagos) {
        this.ventaPagos = ventaPagos;
    }

    public List<DetalleVenta> getDetalleVentas() {
        return detalleVentas;
    }

    public void setDetalleVentas(List<DetalleVenta> detalleVentas) {
        this.detalleVentas = detalleVentas;
    }

    public List<Devolucion> getDevoluciones() {
        return devoluciones;
    }

    public void setDevoluciones(List<Devolucion> devoluciones) {
        this.devoluciones = devoluciones;
    }

    public BigDecimal getSaldoAplicado() {
        return saldoAplicado;
    }

    public void setSaldoAplicado(BigDecimal saldoAplicado) {
        this.saldoAplicado = saldoAplicado;
    }

    public BigDecimal getTotalPagar() {
        return totalPagar;
    }

    public void setTotalPagar(BigDecimal totalPagar) {
        this.totalPagar = totalPagar;
    }
}
