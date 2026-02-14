package com.cristhian.SistemaInventario.DTO;

import com.cristhian.SistemaInventario.Modelo.Persona;
import com.cristhian.SistemaInventario.Modelo.RolPersona;
import com.fasterxml.jackson.annotation.JsonAlias;
import jakarta.persistence.Column;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

import java.math.BigDecimal;
import java.util.List;

public class PersonaDTO {

    private int idPersona;
    @NotBlank(message = "El número de documento es obligatorio")
    private String documentoPersona;
    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    private String apellido;

    private String segundoApellido;
    @NotBlank(message = "La dirección es obligatoria")
    private String direccion;
    @NotBlank(message = "El número de teléfono es obligatorio")
    @Pattern(regexp = "^[0-9\\-\\+]{7,15}$", message = "El teléfono solo puede contener números y signos + o -")
    private String telefono;
    @Email(message = "El correo electrónico no tiene un formato válido")
    @NotBlank(message = "El correo electrónico es obligatorio")
    private String email;

    private BigDecimal saldoFavor = BigDecimal.ZERO;

    @JsonAlias({ "tipoDocumento", "idTipoDocumento" })
    private Integer idTipoDocumento;

    @JsonAlias({ "tipoPersona", "idTipoPersona" })
    private Integer idTipoPersona;

    @JsonAlias({ "ciudad", "idCiudad" })
    private Integer idCiudad;
    // 🔹 NUEVO: lista de IDs de roles
    private List<Integer> idsRoles; // SOLO EL ADMIN LOS ENVÍA

    private String razonSocial;
    private String nombreContacto;
    private String apellidoContacto;
    private String segundoApellidoContacto;

    private String nombreTipoDocumento;
    private String nombreTipoPersona;
    private String nombreCiudad;




    public PersonaDTO() {
    }

    public PersonaDTO(Persona persona) {
        this.idPersona = persona.getIdPersona();
        this.documentoPersona = persona.getDocumentoPersona();
        this.nombre = persona.getNombre();
        this.apellido = persona.getApellido();
        this.segundoApellido = persona.getSegundoApellido();
        this.direccion = persona.getDireccion();
        this.telefono = persona.getTelefono();
        this.email = persona.getEmail();
        this.saldoFavor = persona.getSaldoFavor();
        this.razonSocial = persona.getRazonSocial();
        this.nombreContacto = persona.getNombreContacto();
        this.apellidoContacto = persona.getApellidoContacto();
        this.segundoApellidoContacto = persona.getSegundoApellidoContacto();

        // Solo enviamos los IDs para no exponer objetos completos
        this.idTipoDocumento = persona.getTipoDocumento() != null
                ? persona.getTipoDocumento().getIdTipoDocumento()
                : null;

        this.idTipoPersona = persona.getTipoPersona() != null
                ? persona.getTipoPersona().getIdTipoPersona()
                : null;

        this.idCiudad = persona.getCiudad() != null
                ? persona.getCiudad().getIdCiudad()
                : null;

        // Roles → convertimos la lista de entidades a una lista de IDs
        this.idsRoles = persona.getPersonaRoles() != null
                ? persona.getPersonaRoles()
                .stream()
                .map(pr -> pr.getRolPersona().getIdRolPersona()) // CORRECTO
                .toList()
                : null;

        // Traemos los nombre para mostrar en la vista persona
        this.nombreTipoDocumento = persona.getTipoDocumento() != null
                ? String.valueOf(persona.getTipoDocumento().getNombreTipoDocumento())
                : null;
        this.nombreTipoPersona = persona.getTipoPersona() != null
                ? String.valueOf(persona.getTipoPersona().getNombreTipoPersona())
                : null;
        this.nombreCiudad = persona.getCiudad() != null
                ? persona.getCiudad().getCiudad()
                : null;
    }

    public PersonaDTO(Integer idPersona, Integer idTipoPersona, String razonSocial, String nombre,
                      String apellido, String segundoApellido, String nombreContacto, String apellidoContacto,
                      String segundoApellidoContacto, String documentoPersona, String telefono, String email,
                      String direccion, BigDecimal saldoFavor
    ) {
        this.idPersona = idPersona;
        this.idTipoPersona = idTipoPersona;
        this.razonSocial = razonSocial;
        this.nombre = nombre;
        this.apellido = apellido;
        this.segundoApellido = segundoApellido;
        this.nombreContacto = nombreContacto;
        this.apellidoContacto = apellidoContacto;
        this.segundoApellidoContacto = segundoApellidoContacto;
        this.documentoPersona = documentoPersona;
        this.telefono = telefono;
        this.email = email;
        this.direccion = direccion;
        this.saldoFavor = saldoFavor;
    }


    // Getters y Setters
    public int getIdPersona() {
        return idPersona;
    }

    public void setIdPersona(int idPersona) {
        this.idPersona = idPersona;
    }

    public String getDocumentoPersona() { return documentoPersona; }
    public void setDocumentoPersona(String documentoPersona) { this.documentoPersona = documentoPersona; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getApellido() { return apellido; }
    public void setApellido(String apellido) { this.apellido = apellido; }

    public String getSegundoApellido() { return segundoApellido; }
    public void setSegundoApellido(String segundoApellido) { this.segundoApellido = segundoApellido; }

    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public Integer getIdTipoDocumento() {
        return idTipoDocumento;
    }

    public void setIdTipoDocumento(Integer idTipoDocumento) {
        this.idTipoDocumento = idTipoDocumento;
    }

    public Integer getIdTipoPersona() {
        return idTipoPersona;
    }

    public void setIdTipoPersona(Integer idTipoPersona) {
        this.idTipoPersona = idTipoPersona;
    }

    public Integer getIdCiudad() {
        return idCiudad;
    }

    public void setIdCiudad(Integer idCiudad) {
        this.idCiudad = idCiudad;
    }

    public List<Integer> getIdsRoles() { return idsRoles; }
    public void setIdsRoles(List<Integer> idsRoles) { this.idsRoles = idsRoles; }

    public BigDecimal getSaldoFavor() {
        return saldoFavor;
    }

    public void setSaldoFavor(BigDecimal saldoFavor) {
        this.saldoFavor = saldoFavor;
    }

    public String getRazonSocial() {
        return razonSocial;
    }

    public void setRazonSocial(String razonSocial) {
        this.razonSocial = razonSocial;
    }

    public String getNombreContacto() {
        return nombreContacto;
    }

    public void setNombreContacto(String nombreContacto) {
        this.nombreContacto = nombreContacto;
    }

    public String getApellidoContacto() {
        return apellidoContacto;
    }

    public void setApellidoContacto(String apellidoContacto) {
        this.apellidoContacto = apellidoContacto;
    }

    public String getSegundoApellidoContacto() {
        return segundoApellidoContacto;
    }

    public void setSegundoApellidoContacto(String segundoApellidoContacto) {
        this.segundoApellidoContacto = segundoApellidoContacto;
    }

    public String getNombreTipoDocumento() {
        return nombreTipoDocumento;
    }

    public void setNombreTipoDocumento(String nombreTipoDocumento) {
        this.nombreTipoDocumento = nombreTipoDocumento;
    }

    public String getNombreTipoPersona() {
        return nombreTipoPersona;
    }

    public void setNombreTipoPersona(String nombreTipoPersona) {
        this.nombreTipoPersona = nombreTipoPersona;
    }

    public String getNombreCiudad() {
        return nombreCiudad;
    }

    public void setNombreCiudad(String nombreCiudad) {
        this.nombreCiudad = nombreCiudad;
    }
}
