package com.cristhian.SistemaInventario.DTO;

import com.cristhian.SistemaInventario.Modelo.Ciudad;
import jakarta.validation.constraints.NotBlank;

public class CiudadDTO {

    private int idCiudad;
    @NotBlank(message = "La ciudad  es obligatoria")
    private String ciudad;

    private boolean activo;

    public CiudadDTO() {
    }

    public CiudadDTO(Ciudad entidad){
        this.idCiudad = entidad.getIdCiudad();
        this.ciudad = entidad.getCiudad();
        this.activo = entidad.isActivo();
    }

    public int getIdCiudad() {
        return idCiudad;
    }

    public void setIdCiudad(int id) {
        this.idCiudad = idCiudad;
    }

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
