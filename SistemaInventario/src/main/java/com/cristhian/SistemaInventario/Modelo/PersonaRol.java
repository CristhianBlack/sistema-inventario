package com.cristhian.SistemaInventario.Modelo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
public class PersonaRol {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idPersonaRol;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_persona", nullable= false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler" })
    private Persona persona;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_rol_persona", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private RolPersona rolPersona;

    @Column(nullable = false, length = 50)
    private LocalDate fechaAsignacion;
    @Column(nullable = false)
    private boolean activo = true;

    public PersonaRol() {
    }

    public PersonaRol(LocalDate fechaAsignacion) {
        this.fechaAsignacion = fechaAsignacion;
    }

    public int getIdPersonaRol() {
        return idPersonaRol;
    }

    public void setIdPersonaRol(int idPersonaRol) {
        this.idPersonaRol = idPersonaRol;
    }

    public Persona getPersona() {
        return persona;
    }

    public void setPersona(Persona persona) {
        this.persona = persona;
    }

    public RolPersona getRolPersona() {
        return rolPersona;
    }

    public void setRolPersona(RolPersona rolPersona) {
        this.rolPersona = rolPersona;
    }

    public LocalDate getFechaAsignacion() {
        return fechaAsignacion;
    }

    public void setFechaAsignacion(LocalDate fechaAsignacion) {
        this.fechaAsignacion = fechaAsignacion;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }
}
