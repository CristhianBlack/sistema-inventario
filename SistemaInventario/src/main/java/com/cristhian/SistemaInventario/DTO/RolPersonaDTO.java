package com.cristhian.SistemaInventario.DTO;

import com.cristhian.SistemaInventario.Modelo.RolPersona;
import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;

public class RolPersonaDTO {

    private int idRolPersona;
    @NotBlank(message = "La ciudad  es obligatoria")
    private String nombreRol;
    private String descripcion;

    private boolean activo;

    public RolPersonaDTO(){

    }

    public RolPersonaDTO(RolPersona rolPersona){
        this.idRolPersona = rolPersona.getIdRolPersona();
        this.nombreRol = rolPersona.getNombreRol();
        this.descripcion = rolPersona.getDescripcion();
        this.activo = rolPersona.isActivo();
    }

    public int getIdRolPersona() {
        return idRolPersona;
    }

    public void setIdRolPersona(int idRolPersona) {
        this.idRolPersona = idRolPersona;
    }

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
