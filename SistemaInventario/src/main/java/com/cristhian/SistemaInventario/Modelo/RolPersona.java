package com.cristhian.SistemaInventario.Modelo;

import com.cristhian.SistemaInventario.DTO.RolPersonaDTO;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
public class RolPersona {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idRolPersona;
    @Column(nullable = false, length = 50)
    private String nombreRol;
    private String descripcion;
    @Column(nullable = false)
    private boolean activo = true;

    @OneToMany(mappedBy = "rolPersona")
    @JsonIgnoreProperties("rolPersona")
    private List<PersonaRol> personaRoles = new ArrayList<>();

    public RolPersona() {
    }

    public RolPersona(RolPersonaDTO rolPersonaDTO) {
        this.nombreRol = rolPersonaDTO.getNombreRol();
        this.descripcion = rolPersonaDTO.getDescripcion();
        this.activo = rolPersonaDTO.isActivo();
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

    public List<PersonaRol> getPersonaRoles() {
        return personaRoles;
    }

    public void setPersonaRoles(List<PersonaRol> personaRoles) {
        this.personaRoles = personaRoles;
    }
}
