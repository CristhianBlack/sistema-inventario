package com.cristhian.SistemaInventario.Modelo;

import com.cristhian.SistemaInventario.DTO.MovimientoContableDTO;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

import java.math.BigDecimal;
@Entity
public class MovimientoContable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idMovimiento;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_asiento")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler","movimientos"})
    private AsientoContable asiento;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_cuenta")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler","movimientos"})
    private CuentaContable cuenta;

    @Column(precision = 15, scale = 2)
    private BigDecimal debe;
    @Column(precision = 15, scale = 2)
    private BigDecimal haber;

    public MovimientoContable() {
    }

    public MovimientoContable(MovimientoContableDTO movimientoContableDTO){
        this.debe = movimientoContableDTO.getDebe();
        this.haber = movimientoContableDTO.getHaber();
    }

    private void validarMovimiento() {
        if (debe.compareTo(BigDecimal.ZERO) == 0 &&
                haber.compareTo(BigDecimal.ZERO) == 0) {
            throw new IllegalStateException("Debe o Haber debe tener valor");
        }
        if (debe.compareTo(BigDecimal.ZERO) > 0 &&
                haber.compareTo(BigDecimal.ZERO) > 0) {
            throw new IllegalStateException("No puede tener Debe y Haber simultáneamente");
        }
    }

    public Long getIdMovimiento() {
        return idMovimiento;
    }

    public void setIdMovimiento(Long idMovimiento) {
        this.idMovimiento = idMovimiento;
    }

    public AsientoContable getAsiento() {
        return asiento;
    }

    public void setAsiento(AsientoContable asiento) {
        this.asiento = asiento;
    }

    public CuentaContable getCuenta() {
        return cuenta;
    }

    public void setCuenta(CuentaContable cuenta) {
        this.cuenta = cuenta;
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
}
