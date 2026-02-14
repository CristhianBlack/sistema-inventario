package com.cristhian.SistemaInventario.Modelo;

import com.cristhian.SistemaInventario.Enums.RolSeguridad;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idUsuario;

    @Column(nullable = false, unique = true, length = 50)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private boolean activo = true;
    private boolean debeCambiarPassword = true;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private RolSeguridad rolSeguridad;

    @OneToOne(optional = false)
    @JoinColumn(
            name = "id_persona",
            referencedColumnName = "idPersona",
            unique = true
    )
    private Persona persona;

    // =====================
    // Constructores
    // =====================

    public Usuario() {
    }

    // =====================
    // Getters y Setters
    // =====================

    public Integer getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Integer idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    /**
     * IMPORTANTE:
     * Aquí se guardará el password ENCRIPTADO (BCrypt)
     */
    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    public RolSeguridad getRolSeguridad() {
        return rolSeguridad;
    }

    public void setRolSeguridad(RolSeguridad rolSeguridad) {
        this.rolSeguridad = rolSeguridad;
    }

    public Persona getPersona() {
        return persona;
    }

    public void setPersona(Persona persona) {
        this.persona = persona;
    }

    public boolean isDebeCambiarPassword() {
        return debeCambiarPassword;
    }

    public void setDebeCambiarPassword(boolean debeCambiarPassword) {
        this.debeCambiarPassword = debeCambiarPassword;
    }
}
