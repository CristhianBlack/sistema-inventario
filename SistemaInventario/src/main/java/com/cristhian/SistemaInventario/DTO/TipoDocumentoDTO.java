package com.cristhian.SistemaInventario.DTO;

import com.cristhian.SistemaInventario.Modelo.TipoDocumento;
import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;

public class TipoDocumentoDTO {

    private int idTipoDocumento;
    @NotBlank(message = "El tipo de documento es obligstorio")
    private String nombreTipoDocumento;

    @NotBlank(message = "La sigla es obligatoria")
    private String sigla;

    private boolean activo = true;

    public TipoDocumentoDTO() {
    }

    public TipoDocumentoDTO(TipoDocumento tipo){
        this.idTipoDocumento = tipo.getIdTipoDocumento();
        this.nombreTipoDocumento = tipo.getNombreTipoDocumento();
        this.sigla = tipo.getSigla();
        this.activo = tipo.isActivo();
    }

    public int getIdTipoDocumento() {
        return idTipoDocumento;
    }

    public void setIdTipoDocumento(int idTipoDocumento) {
        this.idTipoDocumento = idTipoDocumento;
    }

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
