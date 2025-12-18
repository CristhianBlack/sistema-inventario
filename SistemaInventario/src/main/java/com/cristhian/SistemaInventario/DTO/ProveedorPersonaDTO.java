package com.cristhian.SistemaInventario.DTO;

import com.cristhian.SistemaInventario.Modelo.Proveedor;
import java.time.LocalDate;

public class ProveedorPersonaDTO {

    // Datos del proveedor
    private int idProveedor;
    private String descripcionProveedor;
    private LocalDate fechaCreacion;
    private boolean activo;

    // Datos de persona
    private Integer idPersona;
    private String documentoPersona;
    private String nombre;
    private String apellido;
    private String segundoApellido;
    private String telefono;
    private String direccion;
    private String email;

    public ProveedorPersonaDTO(Proveedor proveedor) {
        this.idProveedor = proveedor.getIdProveedor();
        this.descripcionProveedor = proveedor.getDescripcionProveedor();
        this.fechaCreacion = proveedor.getFechaCreacion();
        this.activo = proveedor.isActivo();

        if (proveedor.getPersona() != null) {
            this.idPersona = proveedor.getPersona().getIdPersona();
            this.documentoPersona = proveedor.getPersona().getDocumentoPersona();
            this.nombre = proveedor.getPersona().getNombre();
            this.apellido = proveedor.getPersona().getApellido();
            this.segundoApellido = proveedor.getPersona().getSegundoApellido();
            this.telefono = proveedor.getPersona().getTelefono();
            this.direccion = proveedor.getPersona().getDireccion();
            this.email = proveedor.getPersona().getEmail();
        }
    }

    public ProveedorPersonaDTO(Integer idProveedor, String descripcion, boolean activo,
                               Integer idPersona, String nombre, String apellido, String segundoApellido,
                               String documento,String telefono, String email, String direccion) {
        this.idProveedor = idProveedor;
        this.descripcionProveedor = descripcion;
        this.activo = activo;
        this.idPersona = idPersona;
        this.nombre = nombre;
        this.apellido = apellido;
        this.segundoApellido = segundoApellido;
        this.documentoPersona = documento;
        this.telefono = telefono;
        this.email = email;
        this. direccion = direccion;
    }


    // Getters y setters...


    public int getIdProveedor() {
        return idProveedor;
    }

    public void setIdProveedor(int idProveedor) {
        this.idProveedor = idProveedor;
    }

    public String getDescripcionProveedor() {
        return descripcionProveedor;
    }

    public void setDescripcionProveedor(String descripcionProveedor) {
        this.descripcionProveedor = descripcionProveedor;
    }

    public LocalDate getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDate fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    public Integer getIdPersona() {
        return idPersona;
    }

    public void setIdPersona(Integer idPersona) {
        this.idPersona = idPersona;
    }

    public String getDocumentoPersona() {
        return documentoPersona;
    }

    public void setDocumentoPersona(String documentoPersona) {
        this.documentoPersona = documentoPersona;
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

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}

