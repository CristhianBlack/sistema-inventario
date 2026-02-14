package com.cristhian.SistemaInventario.DTO;

import com.cristhian.SistemaInventario.Enums.OrigenMovimiento;
import com.cristhian.SistemaInventario.Enums.TipoMovimiento;
import com.cristhian.SistemaInventario.Modelo.MovimientoInventario;
import jakarta.persistence.Column;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class MovimientoInventarioDTO {

    private int IdMovimientoInventario;

    private TipoMovimiento tipoMovimiento;

    private OrigenMovimiento origenMovimiento;
    private int cantidad;

    private String observacion;

    private LocalDateTime fechaMovimiento;

    private Integer idProducto;

    private Integer idProveedor;
    private String nombreProducto;

    private String nombre;

    private String apellido;

    private String segundoApellido;

    private String razonSocial;

    private String nombreProveedor;

    public MovimientoInventarioDTO() {
    }

    public MovimientoInventarioDTO(MovimientoInventario movimiento){
        this.IdMovimientoInventario = movimiento.getIdMovimientoInventario();
        this.tipoMovimiento = movimiento.getTipoMovimiento();
        this.origenMovimiento = movimiento.getOrigenMovimiento();
        this.cantidad = movimiento.getCantidad();
        this.observacion = movimiento.getObservacion();
        this.fechaMovimiento = movimiento.getFechaMovimiento();


        // Enviamos el ID para no exponer objetos completos
        this.idProducto = movimiento.getProducto() != null
                ? movimiento.getProducto().getIdProducto()
                : null;

        this.idProveedor = movimiento.getProveedor() != null
                ?movimiento.getProveedor().getIdProveedor()
                : null;

        // Traemos el nombre de los productos en la vista movimiento inventario
        this.nombreProducto = movimiento.getProducto() != null
                ? movimiento.getProducto().getNombreProducto()
                : null;

        this.nombre = movimiento.getProveedor() != null
                ? movimiento.getProveedor().getPersona().getNombre()
                : null;
        this.apellido = movimiento.getProveedor() != null
                ? movimiento.getProveedor().getPersona().getApellido()
                : null;
        this.segundoApellido = movimiento.getProveedor() != null
                ? movimiento.getProveedor().getPersona().getSegundoApellido()
                : null;
        this.razonSocial = movimiento.getProveedor() != null
                ? movimiento.getProveedor().getPersona().getRazonSocial()
                : null;
    }

    //constructor para filtrar las fechas desde y muestre los datos correcto
    public MovimientoInventarioDTO(
            LocalDateTime fechaMovimiento,
            String nombreProducto,
            String nombreProveedor,
            OrigenMovimiento origenMovimiento,
            TipoMovimiento tipoMovimiento,
            Integer cantidad,
            String observacion
    ) {
        this.fechaMovimiento = fechaMovimiento;
        this.nombreProducto = nombreProducto;
        this.nombre = nombreProveedor; // aquí va nombre o razón social ya concatenada
        this.origenMovimiento = origenMovimiento;
        this.tipoMovimiento = tipoMovimiento;
        this.cantidad = cantidad;
        this.observacion = observacion;
    }

    public int getIdMovimientoInventario() {
        return IdMovimientoInventario;
    }

    public void setIdMovimientoInventario(int idMovimientoInventario) {
        IdMovimientoInventario = idMovimientoInventario;
    }

    public TipoMovimiento getTipoMovimiento() {
        return tipoMovimiento;
    }

    public void setTipoMovimiento(TipoMovimiento tipoMovimiento) {
        this.tipoMovimiento = tipoMovimiento;
    }

    public OrigenMovimiento getOrigenMovimiento() {
        return origenMovimiento;
    }

    public void setOrigenMovimiento(OrigenMovimiento origenMovimiento) {
        this.origenMovimiento = origenMovimiento;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public String getObservacion() {
        return observacion;
    }

    public void setObservacion(String observacion) {
        this.observacion = observacion;
    }

    public LocalDateTime getFechaMovimiento() {
        return fechaMovimiento;
    }

    public void setFechaMovimiento(LocalDateTime fechaMovimiento) {
        this.fechaMovimiento = fechaMovimiento;
    }

    public Integer getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(Integer idProducto) {
        this.idProducto = idProducto;
    }

    public Integer getIdProveedor() {
        return idProveedor;
    }

    public void setIdProveedor(Integer idProveedor) {
        this.idProveedor = idProveedor;
    }
    public String getNombreProducto() {
        return nombreProducto;
    }

    public void setNombreProducto(String nombreProducto) {
        this.nombreProducto = nombreProducto;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getSegundoApellido() {
        return segundoApellido;
    }

    public void setSegundoApellido(String segundoApellido) {
        this.segundoApellido = segundoApellido;
    }

    public String getRazonSocial() {
        return razonSocial;
    }

    public void setRazonSocial(String razonSocial) {
        this.razonSocial = razonSocial;
    }

    public String getNombreProveedor() {
        return nombreProveedor;
    }

    public void setNombreProveedor(String nombreProveedor) {
        this.nombreProveedor = nombreProveedor;
    }
}
