package com.cristhian.SistemaInventario.DTO;

import com.cristhian.SistemaInventario.Enums.EstadoVenta;
import com.cristhian.SistemaInventario.Modelo.Venta;


import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class VentaDTO {

    private int idVenta;
    private LocalDateTime fechaVenta;
    private BigDecimal subTotalVenta;
    private BigDecimal totalImpuestos;
    private BigDecimal totalVenta;
    private BigDecimal saldoAplicado = BigDecimal.ZERO;
    private BigDecimal totalPagar;
    private EstadoVenta estado; // Pendiente, parcial , pagada
    private Integer idPersona;

    private List<DetalleVentaDTO> detalles;
    private List<VentaPagoDTO> pagos;

    private Boolean usarSaldoFavor = false;

    public VentaDTO(){

    }

    public VentaDTO(Venta venta){
        this.idVenta = venta.getIdVenta();
        this.fechaVenta = venta.getFechaVenta();
        this.subTotalVenta = venta.getSubTotalVenta();
        this.totalImpuestos = venta.getTotalImpuestos();
        this.totalVenta = venta.getTotalVenta();
        this.estado = venta.getEstado();
        this.saldoAplicado = venta.getSaldoAplicado();
        this.totalPagar = venta.getTotalPagar();

        // Enviamos el ID para no exponer objetos completos
        this.idPersona = venta.getPersona() != null
                ? venta.getPersona().getIdPersona()
                : null;

        this.detalles = venta.getDetalleVentas() != null
                ? venta.getDetalleVentas().stream()
                .map(DetalleVentaDTO::new)
                .toList()
                : null;


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

    public Integer getIdPersona() {
        return idPersona;
    }

    public void setIdPersona(Integer idPersona) {
        this.idPersona = idPersona;
    }

    public List<DetalleVentaDTO> getDetalles() {
        return detalles;
    }

    public void setDetalles(List<DetalleVentaDTO> detalles) {
        this.detalles = detalles;
    }

    public List<VentaPagoDTO> getPagos() {
        return pagos;
    }

    public void setPagos(List<VentaPagoDTO> pagos) {
        this.pagos = pagos;
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

    public Boolean getUsarSaldoFavor() {
        return usarSaldoFavor;
    }

    public void setUsarSaldoFavor(Boolean usarSaldoFavor) {
        this.usarSaldoFavor = usarSaldoFavor;
    }
}
