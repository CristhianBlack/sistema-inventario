package com.cristhian.SistemaInventario.DTO;

import com.cristhian.SistemaInventario.Modelo.MovimientoInventario;
import com.cristhian.SistemaInventario.Modelo.OrigenMovimiento;
import com.cristhian.SistemaInventario.Modelo.TipoMovimiento;
import jakarta.persistence.Column;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class MovimientoInventarioDTO {

    private int IdMovimientoInventario;

    private TipoMovimiento tipoMovimiento;

    private OrigenMovimiento origenMovimiento;
    private int cantidad;

    private String observacion;

    private LocalDateTime fechaMovimiento;

    private Integer idProducto;

    private Integer idProveedor;

    public MovimientoInventarioDTO() {
    }

    public MovimientoInventarioDTO(MovimientoInventario movimiento){
        this.IdMovimientoInventario = movimiento.getIdMovimientoInventario();
        this.tipoMovimiento = movimiento.getTipoMovimiento();
        this.origenMovimiento = movimiento.getOrigenMovimiento();
        this.cantidad = movimiento.getCantidad();
        this.observacion = movimiento.getObservacion();
        this.fechaMovimiento = movimiento.getFechaMovimiento();

        // Enviamos el ID para no exponer objetos completos
        this.idProducto = movimiento.getProducto() != null
                ? movimiento.getProducto().getIdProducto()
                : null;

        this.idProveedor = movimiento.getProveedor() != null
                ?movimiento.getProveedor().getIdProveedor()
                : null;
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

    public Integer getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(Integer idProducto) {
        this.idProducto = idProducto;
    }

    public Integer getIdProveedor() {
        return idProveedor;
    }

    public void setIdProveedor(Integer idProveedor) {
        this.idProveedor = idProveedor;
    }
}
