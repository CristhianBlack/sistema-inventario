package com.cristhian.SistemaInventario.DTO;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class MayorGeneralDTO {

        private Long idCuenta;
        private String codigoCuenta;
        private String nombreCuenta;
        private LocalDateTime fecha;
        private String descripcion;
        private BigDecimal debe;
        private BigDecimal haber;
        private BigDecimal saldo;   // 👈 ESTE es el que usa el mayor

        public MayorGeneralDTO() {
        }

        public MayorGeneralDTO(
                Long idCuenta,
                String codigoCuenta,
                String nombreCuenta,
                LocalDateTime fecha,
                String descripcion,
                BigDecimal debe,
                BigDecimal haber
        ) {
            this.idCuenta = idCuenta;
            this.codigoCuenta = codigoCuenta;
            this.nombreCuenta = nombreCuenta;
            this.fecha = fecha;
            this.descripcion = descripcion;
            this.debe = debe;
            this.haber = haber;
        }

    public Long getIdCuenta() {
        return idCuenta;
    }

    public void setIdCuenta(Long idCuenta) {
        this.idCuenta = idCuenta;
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

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
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

    public BigDecimal getSaldo() {
        return saldo;
    }

    public void setSaldo(BigDecimal saldo) {
        this.saldo = saldo;
    }
}
