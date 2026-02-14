package com.cristhian.SistemaInventario.Modelo;

import com.cristhian.SistemaInventario.DTO.CuentaContableDTO;
import com.cristhian.SistemaInventario.Enums.TipoCuenta;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity

public class CuentaContable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idCuenta;

    @Column(nullable = false, unique = true)
    private String codigo;

    @Column(nullable = false)
    private String nombre;

    @Enumerated(EnumType.STRING)
    private TipoCuenta tipo;

    private Boolean activa = true;

    @OneToMany(mappedBy = "cuenta", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnoreProperties("cuenta") // evita recursión infinita
    private List<MovimientoContable> movimientos = new ArrayList<>();


    public CuentaContable() {
    }

    public CuentaContable(CuentaContableDTO cuentaContableDTO){
        this.codigo = cuentaContableDTO.getCodigo();
        this.nombre = cuentaContableDTO.getNombre();
        this.tipo = cuentaContableDTO.getTipo();
        this.activa = cuentaContableDTO.getActiva();
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

    public List<MovimientoContable> getMovimientos() {
        return movimientos;
    }

    public void setMovimientos(List<MovimientoContable> movimientos) {
        this.movimientos = movimientos;
    }

}
