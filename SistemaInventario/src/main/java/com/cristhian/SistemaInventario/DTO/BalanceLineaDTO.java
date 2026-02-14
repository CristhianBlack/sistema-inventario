package com.cristhian.SistemaInventario.DTO;

import com.cristhian.SistemaInventario.Enums.TipoCuenta;

import java.math.BigDecimal;

public class BalanceLineaDTO {

    private TipoCuenta tipo;      // ACTIVO, PASIVO, PATRIMONIO
    private String codigo;
    private String nombre;
    private BigDecimal saldo;

    private Long idCuenta;

    public BalanceLineaDTO(
            TipoCuenta tipo,
            String codigo,
            String nombre,
            Long idCuenta,
            BigDecimal saldo

    ) {
        this.tipo = tipo;
        this.codigo = codigo;
        this.nombre = nombre;
        this.idCuenta = idCuenta;
        this.saldo = saldo;

    }

    public TipoCuenta getTipo() {
        return tipo;
    }

    public void setTipo(TipoCuenta tipo) {
        this.tipo = tipo;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public BigDecimal getSaldo() {
        return saldo;
    }

    public void setSaldo(BigDecimal saldo) {
        this.saldo = saldo;
    }

    public Long getIdCuenta() {
        return idCuenta;
    }

    public void setIdCuenta(Long idCuenta) {
        this.idCuenta = idCuenta;
    }
}
