package com.cristhian.SistemaInventario.DTO;

import com.cristhian.SistemaInventario.Modelo.AsientoContable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class AsientoContableDTO {

    private Long idAsiento;

    private LocalDateTime fecha;

    private String descripcion;

    private BigDecimal total;

    private List<MovimientoContableDTO> movimientos;
    public AsientoContableDTO() {
    }

    public AsientoContableDTO(AsientoContable asientoContable){
        this.idAsiento = asientoContable.getIdAsiento();
        this.fecha = asientoContable.getFecha();
        this.descripcion = asientoContable.getDescripcion();
        //this.total = asientoContable.getTotal();
    }

    public Long getIdAsiento() {
        return idAsiento;
    }

    public void setIdAsiento(Long idAsiento) {
        this.idAsiento = idAsiento;
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

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public List<MovimientoContableDTO> getMovimientos() {
        return movimientos;
    }

    public void setMovimientos(List<MovimientoContableDTO> movimientos) {
        this.movimientos = movimientos;
    }
}
