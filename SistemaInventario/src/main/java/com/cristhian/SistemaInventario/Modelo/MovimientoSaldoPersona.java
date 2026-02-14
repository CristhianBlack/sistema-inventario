package com.cristhian.SistemaInventario.Modelo;

import com.cristhian.SistemaInventario.DTO.MovimientoSaldoPersonaDTO;
import com.cristhian.SistemaInventario.Enums.TipoMovimientoSaldo;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
public class MovimientoSaldoPersona {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idMovimientoSaldo;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_Persona", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "movimientoSaldos"})
    private Persona persona;

    private BigDecimal monto;

    @Enumerated(EnumType.STRING)
    private TipoMovimientoSaldo tipoMovimiento; // ENTRADA / SALIDA

    private String concepto;

    @CreationTimestamp
    private LocalDateTime fechaMovimiento;

    public MovimientoSaldoPersona() {
    }

    public MovimientoSaldoPersona(MovimientoSaldoPersonaDTO movimientoSaldoDTO){
        this.monto = movimientoSaldoDTO.getMonto();
        this.tipoMovimiento = movimientoSaldoDTO.getTipoMovimiento();
        this.concepto = movimientoSaldoDTO.getConcepto();
        this.fechaMovimiento = movimientoSaldoDTO.getFechaMovimiento();
    }

    public Integer getIdMovimientoSaldo() {
        return idMovimientoSaldo;
    }

    public void setIdMovimientoSaldo(Integer idMovimientoSaldo) {
        this.idMovimientoSaldo = idMovimientoSaldo;
    }

    public Persona getPersona() {
        return persona;
    }

    public void setPersona(Persona persona) {
        this.persona = persona;
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
