package com.cristhian.SistemaInventario.DTO;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;

public class TipoPersonaDTO {

    @NotBlank(message = "El nombre del tipo de persona es obligatorio")
    private String nombreTipoPersona;

    private boolean activo = true;

    public String getNombreTipoPersona() {
        return nombreTipoPersona;
    }

    public void setNombreTipoPersona(String nombreTipoPersona) {
        this.nombreTipoPersona = nombreTipoPersona;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }
}
