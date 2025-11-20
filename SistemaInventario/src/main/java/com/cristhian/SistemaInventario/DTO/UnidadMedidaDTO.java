package com.cristhian.SistemaInventario.DTO;

import com.cristhian.SistemaInventario.Modelo.UnidadMedida;
import jakarta.validation.constraints.NotBlank;

public class UnidadMedidaDTO {

    @NotBlank
    private String nombreMedida;

    @NotBlank
    private String sigla;

    private boolean activo;

    // 👉 Constructor vacío (OBLIGATORIO para POST/PUT)
    public UnidadMedidaDTO() {}

    // 👉 Constructor desde entidad (para mapear entidad → DTO)
    public UnidadMedidaDTO(UnidadMedida entidad) {
        this.nombreMedida = entidad.getNombreMedida();
        this.sigla = entidad.getSigla();
        this.activo = entidad.isActivo();
    }

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
