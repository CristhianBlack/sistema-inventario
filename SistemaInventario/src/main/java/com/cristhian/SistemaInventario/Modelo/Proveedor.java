package com.cristhian.SistemaInventario.Modelo;

import com.cristhian.SistemaInventario.DTO.ProveedorDTO;
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
    @Column(length = 255)
    private String descripcionProveedor;
    @Column(nullable = false)
    private LocalDate fechaCreacion;

    @OneToMany(mappedBy = "proveedor" )
    @JsonIgnoreProperties("proveedor") // evita recursión infinita
    private List<Producto> productos = new ArrayList<>();
    @OneToMany(mappedBy = "proveedor" )
    @JsonIgnoreProperties("proveedor") // evita recursión infinita
    private List<MovimientoInventario> movimientos = new ArrayList<>();

    @Column(nullable = false)
    private boolean activo = true;

    @OneToOne
    @JoinColumn(name = "id_persona", referencedColumnName = "idPersona", unique = true)
    private Persona persona;

    @OneToMany(mappedBy = "proveedor", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnoreProperties("proveedor") // evita recursión infinita
    private List<Compra> compras = new ArrayList<>();
    public Proveedor() {
    }

    public Proveedor(ProveedorDTO proveedorDTO) {
        this.descripcionProveedor = proveedorDTO.getDescripcionProveedor();
        this.fechaCreacion = LocalDate.now();
        this.activo = proveedorDTO.isActivo();
    }

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

    public Persona getPersona() {
        return persona;
    }

    public void setPersona(Persona persona) {
        this.persona = persona;
    }

    public List<Producto> getProductos() {
        return productos;
    }

    public void setProductos(List<Producto> productos) {
        this.productos = productos;
    }
}
