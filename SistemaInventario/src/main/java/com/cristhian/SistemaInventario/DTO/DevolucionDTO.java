package com.cristhian.SistemaInventario.DTO;

import com.cristhian.SistemaInventario.Modelo.Devolucion;
import com.cristhian.SistemaInventario.Modelo.TipoDevolucion;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class DevolucionDTO {

    private int idDevolucion;
    private String motivo;
    private int cantidad;
    private LocalDateTime fechaDevolucion;

    private TipoDevolucion tipoDevolucion;
    private Integer idProducto;
    private Integer idVenta;

    private boolean activo;
    private Integer idProductoCambio;  // producto entregado (opcional)

    private BigDecimal valorDevuelto;


    // Constructores, getters y setters
    public DevolucionDTO() {

    }

    public DevolucionDTO(Devolucion devolucion){
        this.idDevolucion = devolucion.getIdDevolucion();
        this.motivo = devolucion.getMotivo();
        this.cantidad = devolucion.getCantidad();
        this.fechaDevolucion = devolucion.getFechaDevolucion();
        this.tipoDevolucion = devolucion.getTipoDevolucion();
        this.activo = devolucion.isActivo();
        this.valorDevuelto = devolucion.getValorDevuelto();

        // Enviamos el ID para no exponer objetos completos
        this.idVenta = devolucion.getVenta() != null
                ? devolucion.getVenta().getIdVenta()
                : null;

        this.idProducto = devolucion.getProducto() != null
                ? devolucion.getProducto().getIdProducto()
                : null;
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

    public Integer getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(Integer idProducto) {
        this.idProducto = idProducto;
    }

    public Integer getIdVenta() {
        return idVenta;
    }

    public void setIdVenta(Integer idVenta) {
        this.idVenta = idVenta;
    }

    public TipoDevolucion getTipoDevolucion() {
        return tipoDevolucion;
    }

    public void setTipoDevolucion(TipoDevolucion tipoDevolucion) {
        this.tipoDevolucion = tipoDevolucion;
    }

    public boolean isActivo() {
        return activo;
    }
    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    public Integer getIdProductoCambio() {
        return idProductoCambio;
    }

    public void setIdProductoCambio(Integer idProductoCambio) {
        this.idProductoCambio = idProductoCambio;
    }

    public BigDecimal getValorDevuelto() {
        return valorDevuelto;
    }

    public void setValorDevuelto(BigDecimal valorDevuelto) {
        this.valorDevuelto = valorDevuelto;
    }
}
