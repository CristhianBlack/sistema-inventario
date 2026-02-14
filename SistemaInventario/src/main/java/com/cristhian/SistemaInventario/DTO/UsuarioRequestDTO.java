package com.cristhian.SistemaInventario.DTO;

/**
 * DTO de solicitud para operaciones relacionadas con Usuario.
 *
 * Se utiliza para recibir información desde el frontend
 * al momento de crear o actualizar un usuario en el sistema.
 *
 * Contiene únicamente los datos necesarios para el registro
 * y mantenimiento del usuario.
 */
public class UsuarioRequestDTO {
    private String username;
    private String password;
    private String rolSeguridad;
    private int idPersona;

    public UsuarioRequestDTO() {
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

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRolSeguridad() {
        return rolSeguridad;
    }

    public void setRolSeguridad(String rolSeguridad) {
        this.rolSeguridad = rolSeguridad;
    }

    public int getIdPersona() {
        return idPersona;
    }

    public void setIdPersona(int idPersona) {
        this.idPersona = idPersona;
    }
}
