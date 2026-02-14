package com.cristhian.SistemaInventario.DTO;

import com.cristhian.SistemaInventario.Enums.NombreTipoPersona;
import com.cristhian.SistemaInventario.Modelo.TipoPersona;
import jakarta.validation.constraints.NotBlank;

public class TipoPersonaDTO {

    private int idTipoPersona;
    @NotBlank(message = "El nombre del tipo de persona es obligatorio")
    private NombreTipoPersona nombreTipoPersona;

    private boolean activo = true;

    public TipoPersonaDTO() {
    }

    public TipoPersonaDTO(TipoPersona tipoPersona){
        this.idTipoPersona = tipoPersona.getIdTipoPersona();
        this.nombreTipoPersona = tipoPersona.getNombreTipoPersona();
        this.activo = tipoPersona.isActivo();
    }

    public int getIdTipoPersona() {
        return idTipoPersona;
    }

    public void setIdTipoPersona(int idTipoPersona) {
        this.idTipoPersona = idTipoPersona;
    }

    public NombreTipoPersona getNombreTipoPersona() {
        return nombreTipoPersona;
    }

    public void setNombreTipoPersona(NombreTipoPersona nombreTipoPersona) {
        this.nombreTipoPersona = nombreTipoPersona;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }
}
