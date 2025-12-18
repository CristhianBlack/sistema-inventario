package com.cristhian.SistemaInventario.Modelo;

import com.cristhian.SistemaInventario.DTO.VentaDTO;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Venta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idVenta;

    @Column(nullable = false)
    private LocalDate fechaVenta;

    @Column(nullable = false)
    private double subTotalVenta;

    @Column(nullable = false)
    private double totalImpuestos;

    @Column(nullable = false)
    private double totalVenta;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoVenta estado; // Pendiente, parcial , pagada

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_persona")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "ventas"})
    private Persona persona;

    @OneToMany(mappedBy = "venta", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnoreProperties("venta") // 👈 evita recursión infinita
    private List<VentaPago> ventaPagos = new ArrayList<>();

    @OneToMany(mappedBy = "venta", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnoreProperties("venta") // 👈 evita recursión infinita
    private List<DetalleVenta> detalleVentas = new ArrayList<>();

    public Venta() {
    }

    public Venta(VentaDTO ventaDTO){
        this.fechaVenta = ventaDTO.getFechaVenta();
        this.subTotalVenta = ventaDTO.getSubTotalVenta();
        this.totalImpuestos = ventaDTO.getTotalImpuestos();
        this.totalVenta = ventaDTO.getTotalVenta();
        this.estado = ventaDTO.getEstado();
    }

    public int getIdVenta() {
        return idVenta;
    }

    public void setIdVenta(int idVenta) {
        this.idVenta = idVenta;
    }

    public LocalDate getFechaVenta() {
        return fechaVenta;
    }

    public void setFechaVenta(LocalDate fechaVenta) {
        this.fechaVenta = fechaVenta;
    }

    public double getSubTotalVenta() {
        return subTotalVenta;
    }

    public void setSubTotalVenta(double subTotalVenta) {
        this.subTotalVenta = subTotalVenta;
    }

    public double getTotalVenta() {
        return totalVenta;
    }

    public void setTotalVenta(double totalVenta) {
        this.totalVenta = totalVenta;
    }

    public double getTotalImpuestos() {
        return totalImpuestos;
    }

    public void setTotalImpuestos(double totalImpuestos) {
        this.totalImpuestos = totalImpuestos;
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
        return ventaPagos;
    }

    public void setVentaPagos(List<VentaPago> ventaPagos) {
        this.ventaPagos = ventaPagos;
    }
}
