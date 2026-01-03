package com.cristhian.SistemaInventario.Modelo;

import com.cristhian.SistemaInventario.DTO.DevolucionDTO;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
public class Devolucion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idDevolucion;

    private String motivo;

    private int cantidad;
    @CreationTimestamp
    private LocalDateTime fechaDevolucion;
    @Enumerated(EnumType.STRING)
    private TipoDevolucion tipoDevolucion;

    private boolean activo = true;
    @ManyToOne
    @JoinColumn(name = "id_Producto", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "productos"})
    private Producto producto;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_venta")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "devoluciones"})
    private Venta venta;

    @ManyToOne
    @JoinColumn(name = "idProductoCambio", nullable = true)
    private Producto productoCambio; // Solo si la devolución es tipo CAMBIO

    @Column(nullable = false)
    private BigDecimal valorDevuelto;



    // Constructores, getters y setters
    public Devolucion() {
        this.fechaDevolucion = LocalDateTime.now();
    }

    public Devolucion(DevolucionDTO devolucionDTO){
        this.motivo = devolucionDTO.getMotivo();
        this.cantidad = devolucionDTO.getCantidad();
        this.fechaDevolucion = devolucionDTO.getFechaDevolucion();
        this.tipoDevolucion = devolucionDTO.getTipoDevolucion();
        this.activo = devolucionDTO.isActivo();
        this.valorDevuelto = devolucionDTO.getValorDevuelto();
    }

    public int getIdDevolucion() {
        return idDevolucion;
    }

    public void setIdDevolucion(int idDevolucion) {
        this.idDevolucion = idDevolucion;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public LocalDateTime getFechaDevolucion() {
        return fechaDevolucion;
    }

    public void setFechaDevolucion(LocalDateTime fechaDevolucion) {
        this.fechaDevolucion = fechaDevolucion;
    }

    public TipoDevolucion getTipoDevolucion() {
        return tipoDevolucion;
    }

    public void setTipoDevolucion(TipoDevolucion tipoDevolucion) {
        this.tipoDevolucion = tipoDevolucion;
    }

    public Producto getProducto() {
        return producto;
    }

    public void setProducto(Producto producto) {
        this.producto = producto;
    }

    public Venta getVenta() {
        return venta;
    }

    public void setVenta(Venta venta) {
        this.venta = venta;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    public Producto getProductoCambio() {
        return productoCambio;
    }

    public void setProductoCambio(Producto productoCambio) {
        this.productoCambio = productoCambio;
    }

    public BigDecimal getValorDevuelto() {
        return valorDevuelto;
    }

    public void setValorDevuelto(BigDecimal valorDevuelto) {
        this.valorDevuelto = valorDevuelto;
    }
}
