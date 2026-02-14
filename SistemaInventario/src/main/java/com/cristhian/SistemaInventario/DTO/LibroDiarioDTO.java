package com.cristhian.SistemaInventario.DTO;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class LibroDiarioDTO {

    private LocalDateTime fecha;
    private Long idAsiento;
    private String descripcionAsiento;
    private String codigoCuenta;
    private String nombreCuenta;
    private BigDecimal debe;
    private BigDecimal haber;

    public LibroDiarioDTO(LocalDateTime fecha, Long idAsiento,
                          String descripcionAsiento,
                          String codigoCuenta, String nombreCuenta,
                          BigDecimal debe, BigDecimal haber) {
        this.fecha = fecha;
        this.idAsiento = idAsiento;
        this.descripcionAsiento = descripcionAsiento;
        this.codigoCuenta = codigoCuenta;
        this.nombreCuenta = nombreCuenta;
        this.debe = debe;
        this.haber = haber;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }

    public Long getIdAsiento() {
        return idAsiento;
    }

    public void setIdAsiento(Long idAsiento) {
        this.idAsiento = idAsiento;
    }

    public String getDescripcionAsiento() {
        return descripcionAsiento;
    }

    public void setDescripcionAsiento(String descripcionAsiento) {
        this.descripcionAsiento = descripcionAsiento;
    }

    public String getCodigoCuenta() {
        return codigoCuenta;
    }

    public void setCodigoCuenta(String codigoCuenta) {
        this.codigoCuenta = codigoCuenta;
    }

    public String getNombreCuenta() {
        return nombreCuenta;
    }

    public void setNombreCuenta(String nombreCuenta) {
        this.nombreCuenta = nombreCuenta;
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
