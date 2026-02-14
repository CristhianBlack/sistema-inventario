package com.cristhian.SistemaInventario.Modelo;

import com.cristhian.SistemaInventario.DTO.MovimientoInventarioDTO;
import com.cristhian.SistemaInventario.Enums.OrigenMovimiento;
import com.cristhian.SistemaInventario.Enums.TipoMovimiento;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
public class MovimientoInventario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int IdMovimientoInventario;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_movimiento", nullable = false)
    private TipoMovimiento tipoMovimiento;

    @Enumerated(EnumType.STRING)
    @Column(name = "origen_movimiento", nullable = false)
    private OrigenMovimiento origenMovimiento;

    @Column(nullable = false)
    private int cantidad;

    @Column(nullable = false, length = 255)
    private String observacion;

    @CreationTimestamp
    @Column(nullable = false)
    private LocalDateTime fechaMovimiento;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_producto")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "movimientos"})
    private Producto producto;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_proveedor", nullable = true)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "movimientos"})
    private Proveedor proveedor;

    public MovimientoInventario() {
    }

    public MovimientoInventario(MovimientoInventarioDTO moviminetoDTO){
        this.tipoMovimiento = moviminetoDTO.getTipoMovimiento();
        this.origenMovimiento = moviminetoDTO.getOrigenMovimiento();
        this.cantidad = moviminetoDTO.getCantidad();
        this.observacion = moviminetoDTO.getObservacion();
        this.fechaMovimiento = moviminetoDTO.getFechaMovimiento();
    }

    public int getIdMovimientoInventario() {
        return IdMovimientoInventario;
    }

    public void setIdMovimientoInventario(int idMovimientoInventario) {
        IdMovimientoInventario = idMovimientoInventario;
    }

    public TipoMovimiento getTipoMovimiento() {
        return tipoMovimiento;
    }

    public void setTipoMovimiento(TipoMovimiento tipoMovimiento) {
        this.tipoMovimiento = tipoMovimiento;
    }

    public OrigenMovimiento getOrigenMovimiento() {
        return origenMovimiento;
    }

    public void setOrigenMovimiento(OrigenMovimiento origenMovimiento) {
        this.origenMovimiento = origenMovimiento;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public String getObservacion() {
        return observacion;
    }

    public void setObservacion(String observacion) {
        this.observacion = observacion;
    }

    public LocalDateTime getFechaMovimiento() {
        return fechaMovimiento;
    }

    public void setFechaMovimiento(LocalDateTime fechaMovimiento) {
        this.fechaMovimiento = fechaMovimiento;
    }

    public Producto getProducto() {
        return producto;
    }

    public void setProducto(Producto producto) {
        this.producto = producto;
    }

    public Proveedor getProveedor() {
        return proveedor;
    }

    public void setProveedor(Proveedor proveedor) {
        this.proveedor = proveedor;
    }
}
