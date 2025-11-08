package com.cristhian.SistemaInventario.DTO;

import jakarta.validation.constraints.NotBlank;

public class CiudadDTO {
    @NotBlank(message = "La ciudad  es obligatoria")
    private String ciudad;

    private boolean activo;

    public String getCiudad() {
        return ciudad;
    }

    public void setCiudad(String ciudad) {
        this.ciudad = ciudad;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }
}
