package com.cristhian.SistemaInventario.Modelo;

import com.cristhian.SistemaInventario.DTO.CompraDTO;
import com.cristhian.SistemaInventario.DTO.DetalleCompraDTO;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Compra {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idCompra;

    @CreationTimestamp
    @Column(nullable = false)
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

    @Column(nullable = false)
    private boolean activo = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_proveedor")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "compras"})
    private Proveedor proveedor;

    // Relación con compra
    @OneToMany(mappedBy = "compra", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnoreProperties("compra") // evita recursión infinita
    private List<DetalleCompra> detalles = new ArrayList<>();

    @OneToMany(mappedBy = "compra", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnoreProperties("compra") // 👈 evita recursión infinita
    private List<CompraPago> compraPagos = new ArrayList<>();

    public Compra() {
    }

    public Compra(CompraDTO compraDTO){
        this.fechaCompra = compraDTO.getFechaCompra();
        this.subTotalCompra = compraDTO.getSubTotalCompra();
        this.totalImpuestos = compraDTO.getTotalImpuestos();
        this.totalCompra = compraDTO.getTotalCompra();
        this.estado = compraDTO.getEstado();
        this.activo = compraDTO.isActivo();
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
        this.fechaCompra = fechaCompra;
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

    public Proveedor getProveedor() {
        return proveedor;
    }

    public void setProveedor(Proveedor proveedor) {
        this.proveedor = proveedor;
    }

    public List<DetalleCompra> getDetalles() {
        return detalles;
    }

    public void setDetalles(List<DetalleCompra> detalles) {
        this.detalles = detalles;
    }

    public List<CompraPago> getCompraPagos() {
        return compraPagos;
    }

    public void setCompraPagos(List<CompraPago> compraPagos) {
        this.compraPagos = compraPagos;
    }
}
