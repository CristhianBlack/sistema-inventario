package com.cristhian.SistemaInventario.DTO;

import com.cristhian.SistemaInventario.Modelo.Ciudad;
import com.cristhian.SistemaInventario.Modelo.Proveedor;
import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

import jakarta.validation.constraints.*;

import java.time.LocalDate;

public class ProveedorDTO {

    private int idProveedor;

    @Size(max = 255, message = "La descripción no puede superar los 255 caracteres")
    private String descripcionProveedor;

    //@NotNull(message = "La fecha de creación es obligatoria")
    private LocalDate fechaCreacion;
    @Column(nullable = false)
    private boolean activo;

    @JsonAlias({ "persona", "idPersona" })
    private Integer idPersona;

    public ProveedorDTO() {
    }

    public ProveedorDTO(Proveedor proveedor){
        this.idProveedor = proveedor.getIdProveedor();
        this.descripcionProveedor = proveedor.getDescripcionProveedor();
        this.fechaCreacion = proveedor.getFechaCreacion();
        this.activo = proveedor.isActivo();

        // Enviamos el ID para no exponer objetos completos
        this.idPersona = proveedor.getPersona() != null
                ? proveedor.getPersona().getIdPersona()
                : null;
    }

    // Getters y Setters


    public int getIdProveedor() {
        return idProveedor;
    }

    public void setIdProveedor(int idProveedor) {
        this.idProveedor = idProveedor;
    }

    public String getDescripcionProveedor() {
        return descripcionProveedor;
    }

    public void setDescripcionProveedor(String descripcionProveedor) {
        this.descripcionProveedor = descripcionProveedor;
    }

    public LocalDate getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDate fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    public Integer getIdPersona() {
        return idPersona;
    }

    public void setIdPersona(Integer idPersona) {
        this.idPersona = idPersona;
    }
}
