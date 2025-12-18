package com.cristhian.SistemaInventario.DTO;

import com.cristhian.SistemaInventario.Modelo.EstadoVenta;
import com.cristhian.SistemaInventario.Modelo.Venta;


import java.time.LocalDate;

public class VentaDTO {

    private int idVenta;
    private LocalDate fechaVenta;
    private double subTotalVenta;
    private double totalImpuestos;
    private double totalVenta;
    private EstadoVenta estado; // Pendiente, parcial , pagada
    private Integer idPersona;

    public VentaDTO(){

    }

    public VentaDTO(Venta venta){
        this.idVenta = venta.getIdVenta();
        this.fechaVenta = venta.getFechaVenta();
        this.subTotalVenta = venta.getSubTotalVenta();
        this.totalImpuestos = venta.getTotalImpuestos();
        this.totalVenta = venta.getTotalVenta();
        this.estado = venta.getEstado();

        // Enviamos el ID para no exponer objetos completos
        this.idPersona = venta.getPersona() != null
                ? venta.getPersona().getIdPersona()
                : null;

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

    public double getTotalImpuestos() {
        return totalImpuestos;
    }

    public void setTotalImpuestos(double totalImpuestos) {
        this.totalImpuestos = totalImpuestos;
    }

    public double getTotalVenta() {
        return totalVenta;
    }

    public void setTotalVenta(double totalVenta) {
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
}
