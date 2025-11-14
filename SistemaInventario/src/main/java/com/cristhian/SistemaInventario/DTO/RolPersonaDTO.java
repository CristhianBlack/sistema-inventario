package com.cristhian.SistemaInventario.DTO;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;

public class RolPersonaDTO {

    @NotBlank(message = "La ciudad  es obligatoria")
    private String nombreRol;
    private String descripcion;

    private boolean activo;

    public String getNombreRol() {
        return nombreRol;
    }

    public void setNombreRol(String nombreRol) {
        this.nombreRol = nombreRol;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }
}
