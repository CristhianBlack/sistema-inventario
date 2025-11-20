package com.cristhian.SistemaInventario.DTO;

import com.cristhian.SistemaInventario.Modelo.Categoria;
import jakarta.validation.constraints.NotBlank;

public class CategoriaDTO {

    private int idCategoria;
    @NotBlank(message = "El nombre de la categoria es obligatorio")
    private String nombreCategoria;
    @NotBlank(message = "La descripcion de la categoria es obligatorio")
    private String descripcion;

    private boolean activo;

    public CategoriaDTO() {
    }

    public CategoriaDTO(Categoria categoria){
        this.idCategoria = categoria.getIdCategoria();
        this.nombreCategoria = categoria.getNombreCategoria();
        this.descripcion = categoria.getDescripcion();
        this.activo = categoria.isActivo();
    }

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
}
