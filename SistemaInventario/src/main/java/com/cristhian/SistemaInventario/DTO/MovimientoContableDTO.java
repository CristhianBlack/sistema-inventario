package com.cristhian.SistemaInventario.DTO;

import com.cristhian.SistemaInventario.Modelo.AsientoContable;
import com.cristhian.SistemaInventario.Modelo.CuentaContable;
import com.cristhian.SistemaInventario.Modelo.MovimientoContable;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

import java.math.BigDecimal;

public class MovimientoContableDTO {

    private Long idMovimiento;
    private Long idAsiento;
    private Long idCuenta;
    private BigDecimal debe;
    private BigDecimal haber;
    private String CodigoCuenta;

    public MovimientoContableDTO() {
    }

    public MovimientoContableDTO(MovimientoContable movimientoContable){
        this.idMovimiento = movimientoContable.getIdMovimiento();
        this.debe = movimientoContable.getDebe();
        this.haber = movimientoContable.getHaber();

        // Enviamos el ID para no exponer objetos completos
        this.idCuenta = movimientoContable.getCuenta() != null
                ? movimientoContable.getCuenta().getIdCuenta()
                : null;

        this.idAsiento = movimientoContable.getAsiento() != null
                ? movimientoContable.getAsiento().getIdAsiento()
                : null;
    }

    public MovimientoContableDTO(String CodigoCuenta,
                                 BigDecimal debe,
                                 BigDecimal haber) {
        this.CodigoCuenta = CodigoCuenta;
        this.debe = debe;
        this.haber = haber;
    }

    public Long getIdMovimiento() {
        return idMovimiento;
    }

    public void setIdMovimiento(Long idMovimiento) {
        this.idMovimiento = idMovimiento;
    }

    public Long getIdAsiento() {
        return idAsiento;
    }

    public void setIdAsiento(Long idAsiento) {
        this.idAsiento = idAsiento;
    }

    public Long getIdCuenta() {
        return idCuenta;
    }

    public void setIdCuenta(Long idCuenta) {
        this.idCuenta = idCuenta;
    }

    public BigDecimal getDebe() {
        return debe;
    }

    public void setDebe(BigDecimal debe) {
        this.debe = debe;
    }

    public BigDecimal getHaber() {
        return haber;
    }

    public void setHaber(BigDecimal haber) {
        this.haber = haber;
    }

    public String getCodigoCuenta() {
        return CodigoCuenta;
    }

    public void setCodigoCuenta(String codigoCuenta) {
        CodigoCuenta = codigoCuenta;
    }
}
