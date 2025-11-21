package com.cristhian.SistemaInventario.DTO;

import com.cristhian.SistemaInventario.Modelo.Persona;
import com.cristhian.SistemaInventario.Modelo.RolPersona;
import com.fasterxml.jackson.annotation.JsonAlias;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import java.util.List;

public class PersonaDTO {

    private int idPersona;
    @NotBlank(message = "El número de documento es obligatorio")
    private String documentoPersona;
    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;
    @NotBlank(message = "El primer apellido es obligatorio")
    private String apellido;
    @NotBlank(message = "El segundo apellido es obligatorio")
    private String segundoApellido;
    @NotBlank(message = "La dirección es obligatoria")
    private String direccion;
    @NotBlank(message = "El número de teléfono es obligatorio")
    @Pattern(regexp = "^[0-9\\-\\+]{7,15}$", message = "El teléfono solo puede contener números y signos + o -")
    private String telefono;
    @Email(message = "El correo electrónico no tiene un formato válido")
    @NotBlank(message = "El correo electrónico es obligatorio")
    private String email;

    @JsonAlias({ "tipoDocumento", "idTipoDocumento" })
    private Integer idTipoDocumento;

    @JsonAlias({ "tipoPersona", "idTipoPersona" })
    private Integer idTipoPersona;

    @JsonAlias({ "ciudad", "idCiudad" })
    private Integer idCiudad;
    // 🔹 NUEVO: lista de IDs de roles
    private List<Integer> idsRoles; // SOLO EL ADMIN LOS ENVÍA


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


}
