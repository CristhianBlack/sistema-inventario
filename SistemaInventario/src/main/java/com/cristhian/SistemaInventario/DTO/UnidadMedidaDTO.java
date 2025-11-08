package com.cristhian.SistemaInventario.DTO;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;

public class UnidadMedidaDTO {

    @NotBlank
    private String nombreMedida;
    @NotBlank
    private String sigla;

    private  boolean activo;

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    public String getNombreMedida() {
        return nombreMedida;
    }

    public void setNombreMedida(String nombreMedida) {
        this.nombreMedida = nombreMedida;
    }

    public String getSigla() {
        return sigla;
    }

    public void setSigla(String sigla) {
        this.sigla = sigla;
    }
}
