package com.cristhian.SistemaInventario.Modelo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

import java.util.List;

@Entity
public class TipoDocumento {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idTipoDocumento;

    @Column(nullable = false, length = 45)
    private String nombreTipoDocumento;

    @Column(nullable = false, length = 10)
    private String sigla;

    @Column(nullable = false)
    private boolean activo = true;

    @OneToMany(mappedBy = "tipoDocumento")
    @JsonIgnoreProperties("tipoDocumento")
    private List<Persona> personas;

    public TipoDocumento() {
    }

    public TipoDocumento(String nombreTipoDocumento, String sigla, boolean activo) {
        this.nombreTipoDocumento = nombreTipoDocumento;
        this.sigla = sigla;
        this.activo = activo;
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

    public List<Persona> getPersonas() {
        return personas;
    }

    public void setPersonas(List<Persona> personas) {
        this.personas = personas;
    }

    @Override
    public String toString() {
        return "TipoDocumento{" +
                "idTipoDocumento=" + idTipoDocumento +
                ", nombreTipoDocumento='" + nombreTipoDocumento + '\'' +
                ", sigla='" + sigla + '\'' +
                ", activo=" + activo +
                '}';
    }
}
