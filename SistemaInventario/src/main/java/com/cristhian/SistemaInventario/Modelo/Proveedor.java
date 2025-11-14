package com.cristhian.SistemaInventario.Modelo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Entity
public class Proveedor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idProveedor;
    @Column(nullable = false, length = 50)
    private String razonSocial;
    @Column(nullable = false, length = 30)
    private String nitProveedor;

    @Column(length = 255)
    private String descripcionProveedor;
    @Column(nullable = false)
    @Temporal(TemporalType.DATE)
    private LocalDate fechaCreacion;


    @OneToMany(mappedBy = "proveedor" )
    @JsonIgnore
    private List<Producto> productos;
    @Column(nullable = false)
    private boolean activo = true;

    @OneToOne
    @JoinColumn(name = "id_persona", referencedColumnName = "idPersona")
    private Persona persona;

    public Proveedor() {
    }

    public Proveedor(String razonSocial, String nitProveedor, String descripcionProveedor, LocalDate fechaCreacion, boolean activo) {
        this.razonSocial = razonSocial;
        this.nitProveedor = nitProveedor;
        this.descripcionProveedor = descripcionProveedor;
        this.fechaCreacion = fechaCreacion;
        this.activo = activo;
    }

    public int getIdProveedor() {
        return idProveedor;
    }

    public void setIdProveedor(int idProveedor) {
        this.idProveedor = idProveedor;
    }

    public String getRazonSocial() {
        return razonSocial;
    }

    public void setRazonSocial(String razonSocial) {
        this.razonSocial = razonSocial;
    }

    public String getNitProveedor() {
        return nitProveedor;
    }

    public void setNitProveedor(String nitProveedor) {
        this.nitProveedor = nitProveedor;
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

    /**public List<Producto> getProductos() {
        return productos;
    }

    public void setProductos(List<Producto> productos) {
        this.productos = productos;
    }**/
}
