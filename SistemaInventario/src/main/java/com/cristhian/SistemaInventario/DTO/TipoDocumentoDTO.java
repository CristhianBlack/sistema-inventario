package com.cristhian.SistemaInventario.DTO;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;

public class TipoDocumentoDTO {

    @NotBlank(message = "El tipo de documento es obligstorio")
    private String nombreTipoDocumento;

    @NotBlank(message = "La sigla es obligatoria")
    private String sigla;

    private boolean activo = true;

    public String getNombreTipoDocumento() {
        return nombreTipoDocumento;
    }

    public void setNombreTipoDocumento(String nombreTipoDocumento) {
        this.nombreTipoDocumento = nombreTipoDocumento;
    }

    public String getSigla() {
        return sigla;
    }

    public void setSigla(String sigla) {
        this.sigla = sigla;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }
}
