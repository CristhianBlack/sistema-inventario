package com.cristhian.SistemaInventario.DTO;

import com.cristhian.SistemaInventario.Enums.TipoCuenta;
import com.cristhian.SistemaInventario.Modelo.CuentaContable;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

public class CuentaContableDTO {

    private Long idCuenta;
    private String codigo;
    private String nombre;
    private TipoCuenta tipo;
    private Boolean activa = true;

    public CuentaContableDTO() {
    }

    public CuentaContableDTO(CuentaContable cuentaContable){
        this.idCuenta = cuentaContable.getIdCuenta();
        this.codigo = cuentaContable.getCodigo();
        this. nombre = cuentaContable.getNombre();
        this.tipo = cuentaContable.getTipo();
        this.activa = cuentaContable.getActiva();
    }


    public Long getIdCuenta() {
        return idCuenta;
    }

    public void setIdCuenta(Long idCuenta) {
        this.idCuenta = idCuenta;
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

    public TipoCuenta getTipo() {
        return tipo;
    }

    public void setTipo(TipoCuenta tipo) {
        this.tipo = tipo;
    }

    public Boolean getActiva() {
        return activa;
    }

    public void setActiva(Boolean activa) {
        this.activa = activa;
    }
}
