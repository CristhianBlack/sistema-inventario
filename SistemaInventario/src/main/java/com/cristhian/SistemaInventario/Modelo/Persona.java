package com.cristhian.SistemaInventario.Modelo;

import com.cristhian.SistemaInventario.DTO.PersonaDTO;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Persona {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idPersona;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_tipo_documento")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "personas"})
    private TipoDocumento tipoDocumento;
    @Column(nullable = false, unique = true, length = 20)
    private String documentoPersona;
    @Column(nullable = true, length = 50)
    private String razonSocial;
    @Column(nullable = false, length = 50)
    private String nombre;

    @Column(nullable = false, length = 50)
    private String apellido;

    @Column(length = 50)
    private String segundoApellido;

    @Column(nullable = false, length = 40)
    private String direccion;

    @Column(nullable = false, length = 20)
    private String telefono;

    @Column(nullable = false, length = 50)
    private String email;

    @Column(length = 50)
    private String nombreContacto;
    @Column(length = 50)
    private String apellidoContacto;
    @Column(length = 50)
    private String segundoApellidoContacto;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_tipo_persona")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "personas"})
    private TipoPersona tipoPersona;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_ciudad")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "personas"})
    private Ciudad ciudad;

    @OneToOne(mappedBy = "persona")
    @JsonIgnoreProperties("persona")
    @JsonIgnore
    private Proveedor proveedor;

    @OneToMany(mappedBy = "persona", fetch = FetchType.LAZY, orphanRemoval = true)
    @JsonIgnore
    private List<PersonaRol> personaRoles = new ArrayList<>();

    @Column(nullable = false)
    private boolean activo = true;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal saldoFavor = BigDecimal.ZERO;

    @OneToMany(mappedBy = "persona", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnoreProperties("persona") //  evita recursión infinita
    private List<MovimientoSaldoPersona> movimientoSaldos = new ArrayList<>();

    @OneToOne(mappedBy = "persona")
    @JsonIgnoreProperties("persona")
    @JsonIgnore
    private Usuario usuario;

    // 🔹 Sobrescribimos el método toString()
    @Override
    public String toString() {
        return "Persona{" +
                "idPersona=" + idPersona +
                ", tipoDocumento=" + (tipoDocumento != null ? tipoDocumento.getNombreTipoDocumento() : "N/A") +
                ", documentoPersona='" + documentoPersona + '\'' +
                ", nombre='" + nombre + '\'' +
                ", apellido='" + apellido + '\'' +
                ", segundoApellido='" + (segundoApellido != null ? segundoApellido : "") + '\'' +
                ", direccion='" + direccion + '\'' +
                ", telefono='" + telefono + '\'' +
                ", email='" + email + '\'' +
                ", tipoPersona=" + (tipoPersona != null ? tipoPersona.getNombreTipoPersona() : "N/A") +
                ", ciudad=" + (ciudad != null ? ciudad.getCiudad() : "N/A") +
                '}';
    }

    public Persona() {
    }

    public Persona(PersonaDTO dto) {
        this.documentoPersona = dto.getDocumentoPersona();
        this.nombre = dto.getNombre();
        this.apellido = dto.getApellido();
        this.segundoApellido = dto.getSegundoApellido();
        this.direccion = dto.getDireccion();
        this.telefono = dto.getTelefono();
        this.email = dto.getEmail();
        this.saldoFavor = dto.getSaldoFavor();
        this.razonSocial = dto.getRazonSocial();
        this.nombreContacto = dto.getNombreContacto();
        this.apellidoContacto = dto.getApellidoContacto();
        this.segundoApellidoContacto = dto.getSegundoApellidoContacto();
    }

    public int getIdPersona() {
        return idPersona;
    }

    public void setIdPersona(int idPersona) {
        this.idPersona = idPersona;
    }

    public TipoDocumento getTipoDocumento() {
        return tipoDocumento;
    }

    public void setTipoDocumento(TipoDocumento tipoDocumento) {
        this.tipoDocumento = tipoDocumento;
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

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public TipoPersona getTipoPersona() {
        return tipoPersona;
    }

    public void setTipoPersona(TipoPersona tipoPersona) {
        this.tipoPersona = tipoPersona;
    }

    public Ciudad getCiudad() {
        return ciudad;
    }

    public void setCiudad(Ciudad ciudad) {
        this.ciudad = ciudad;
    }

    public Proveedor getProveedor() {
        return proveedor;
    }

    public void setProveedor(Proveedor proveedor) {
        this.proveedor = proveedor;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    public List<PersonaRol> getPersonaRoles() {
        return personaRoles;
    }

    public void setPersonaRoles(List<PersonaRol> personaRoles) {
        this.personaRoles = personaRoles;
    }

    public BigDecimal getSaldoFavor() {
        return saldoFavor;
    }

    public void setSaldoFavor(BigDecimal saldoFavor) {
        this.saldoFavor = saldoFavor;
    }

    public List<MovimientoSaldoPersona> getMovimientoSaldos() {
        return movimientoSaldos;
    }

    public void setMovimientoSaldos(List<MovimientoSaldoPersona> movimientoSaldos) {
        this.movimientoSaldos = movimientoSaldos;
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

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }
}
