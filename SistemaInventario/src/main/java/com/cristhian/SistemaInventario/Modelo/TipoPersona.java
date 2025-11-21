package com.cristhian.SistemaInventario.Modelo;

import com.cristhian.SistemaInventario.DTO.TipoPersonaDTO;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

import java.util.List;

@Entity
public class TipoPersona {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idTipoPersona;

    @Column(nullable = false, length = 45)
    private String nombreTipoPersona;

    @Column(nullable = false)
    private boolean activo = true;

    @OneToMany(mappedBy = "tipoPersona")
    @JsonIgnoreProperties("tipoPersona")
    private List<Persona> personas;

    public TipoPersona() {
    }

    public TipoPersona(TipoPersonaDTO tipoPersonaDTO){
        this.nombreTipoPersona = tipoPersonaDTO.getNombreTipoPersona();
        this.activo = tipoPersonaDTO.isActivo();
    }

    public int getIdTipoPersona() {
        return idTipoPersona;
    }

    public void setIdTipoPersona(int idTipoPersona) {
        this.idTipoPersona = idTipoPersona;
    }

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

    public List<Persona> getPersonas() {
        return personas;
    }

    public void setPersonas(List<Persona> personas) {
        this.personas = personas;
    }

    @Override
    public String toString() {
        return "TipoPersona{" +
                "idTipoPersona=" + idTipoPersona +
                ", nombreTipoPersona='" + nombreTipoPersona + '\'' +
                ", activo=" + activo +
                '}';
    }
}
