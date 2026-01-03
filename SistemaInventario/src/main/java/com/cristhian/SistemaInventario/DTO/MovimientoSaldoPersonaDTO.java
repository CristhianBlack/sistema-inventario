package com.cristhian.SistemaInventario.DTO;

import com.cristhian.SistemaInventario.Modelo.MovimientoSaldoPersona;
import com.cristhian.SistemaInventario.Modelo.Persona;
import com.cristhian.SistemaInventario.Modelo.TipoMovimientoSaldo;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class MovimientoSaldoPersonaDTO {

    private Integer idMovimientoSaldo;

    private Integer idPersona;

    private BigDecimal monto;


    private TipoMovimientoSaldo tipoMovimiento; // ENTRADA / SALIDA

    private String concepto;

    private LocalDateTime fechaMovimiento;

    public MovimientoSaldoPersonaDTO() {
    }

    public MovimientoSaldoPersonaDTO(MovimientoSaldoPersona movimientoSaldo) {
        this.idMovimientoSaldo = movimientoSaldo.getIdMovimientoSaldo();
        this.monto = movimientoSaldo.getMonto();
        this.tipoMovimiento = movimientoSaldo.getTipoMovimiento();
        this.concepto = movimientoSaldo.getConcepto();
        this.fechaMovimiento = movimientoSaldo.getFechaMovimiento();

        // Enviamos el ID para no exponer objetos completos
        this.idPersona = movimientoSaldo.getPersona() != null
                ? movimientoSaldo.getPersona().getIdPersona()
                : null;

    }

    public Integer getIdMovimientoSaldo() {
        return idMovimientoSaldo;
    }

    public void setIdMovimientoSaldo(Integer idMovimientoSaldo) {
        this.idMovimientoSaldo = idMovimientoSaldo;
    }

    public Integer getIdPersona() {
        return idPersona;
    }

    public void setIdPersona(Integer idPersona) {
        this.idPersona = idPersona;
    }

    public BigDecimal getMonto() {
        return monto;
    }

    public void setMonto(BigDecimal monto) {
        this.monto = monto;
    }

    public TipoMovimientoSaldo getTipoMovimiento() {
        return tipoMovimiento;
    }

    public void setTipoMovimiento(TipoMovimientoSaldo tipoMovimiento) {
        this.tipoMovimiento = tipoMovimiento;
    }

    public String getConcepto() {
        return concepto;
    }

    public void setConcepto(String concepto) {
        this.concepto = concepto;
    }

    public LocalDateTime getFechaMovimiento() {
        return fechaMovimiento;
    }

    public void setFechaMovimiento(LocalDateTime fechaMovimiento) {
        this.fechaMovimiento = fechaMovimiento;
    }
}
