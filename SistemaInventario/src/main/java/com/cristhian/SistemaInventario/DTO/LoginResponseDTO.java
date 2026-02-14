package com.cristhian.SistemaInventario.DTO;

public class LoginResponseDTO {

    private String username;
    private String rol;
    private String nombreCompleto;
    private String token;
    private boolean debeCambiarPassword;

    public LoginResponseDTO(String username, String rol, String nombreCompleto, String token,
                            boolean debeCambiarPassword) {
        this.username = username;
        this.rol = rol;
        this.nombreCompleto = nombreCompleto;
        this.token = token;
        this.debeCambiarPassword = debeCambiarPassword;

    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }

    public String getNombreCompleto() {
        return nombreCompleto;
    }

    public void setNombreCompleto(String nombreCompleto) {
        this.nombreCompleto = nombreCompleto;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public boolean isDebeCambiarPassword() {
        return debeCambiarPassword;
    }

    public void setDebeCambiarPassword(boolean debeCambiarPassword) {
        this.debeCambiarPassword = debeCambiarPassword;
    }
}
