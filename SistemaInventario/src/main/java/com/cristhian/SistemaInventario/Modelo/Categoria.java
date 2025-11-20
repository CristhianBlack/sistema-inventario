package com.cristhian.SistemaInventario.Modelo;

import com.cristhian.SistemaInventario.DTO.CategoriaDTO;
import com.cristhian.SistemaInventario.DTO.UnidadMedidaDTO;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.persistence.OneToMany;

import java.util.ArrayList;
import java.util.List;


@Entity
public class Categoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idCategoria;

    @Column(nullable = false, length = 45)
    private String nombreCategoria;

    @Column(nullable = false, length = 255)
    private String descripcion;

    @Column(nullable = false)
    private boolean activo = true;

    @OneToMany(mappedBy = "categoria", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<Producto> productos;

    public Categoria() {
    }

    // Constructor desde DTO → SOLO PARA CREACIÓN
    public Categoria(CategoriaDTO dto) {
        this.nombreCategoria = dto.getNombreCategoria();
        this.descripcion = dto.getDescripcion();
        this.activo = true; // siempre activo al crear
    }

    // Manejo de relación bidireccional
    public void agregarCategoria(Producto elProducto){
        if (productos == null) {
            productos = new ArrayList<>();
        }
        productos.add(elProducto);
        elProducto.setCategoria(this);
    }

    // Getters y Setters
    public int getIdCategoria() {
        return idCategoria;
    }

    public void setIdCategoria(int idCategoria) {
        this.idCategoria = idCategoria;
    }

    public String getNombreCategoria() {
        return nombreCategoria;
    }

    public void setNombreCategoria(String nombreCategoria) {
        this.nombreCategoria = nombreCategoria;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    public List<Producto> getProductos() {
        return productos;
    }

    public void setProductos(List<Producto> productos) {
        this.productos = productos;
    }
}
