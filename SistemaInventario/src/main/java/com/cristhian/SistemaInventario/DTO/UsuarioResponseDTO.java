package com.cristhian.SistemaInventario.DTO;

/**
 * DTO de respuesta para la entidad Usuario.
 *
 * Se utiliza para enviar información del usuario hacia el frontend
 * o hacia otras capas de la aplicación, sin exponer directamente
 * la entidad Usuario.
 *
 * Contiene información básica del usuario, su rol de seguridad
 * y datos asociados a la persona vinculada.
 */
public class UsuarioResponseDTO {

    private int idUsuario;
    private String username;
    private String rolSeguridad;
    private Boolean activo;
    private int idPersona;

    private String nombre;
    private String apellido;
    private String segundoApellido;

    public UsuarioResponseDTO() {
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getRolSeguridad() {
        return rolSeguridad;
    }

    public void setRolSeguridad(String rolSeguridad) {
        this.rolSeguridad = rolSeguridad;
    }

    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }

    public int getIdPersona() {
        return idPersona;
    }

    public void setIdPersona(int idPersona) {
        this.idPersona = idPersona;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getSegundoApellido() {
        return segundoApellido;
    }

    public void setSegundoApellido(String segundoApellido) {
        this.segundoApellido = segundoApellido;
    }
}
