package com.cristhian.SistemaInventario.Modelo;

import com.cristhian.SistemaInventario.DTO.UnidadMedidaDTO;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
public class UnidadMedida {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idUnidadMedida;

    @Column(nullable = false, length = 45)
    private String nombreMedida;

    @Column(nullable = false, length = 5)
    private String sigla;

    @OneToMany(mappedBy = "unidadMedida", cascade = CascadeType.ALL)
    private List<Producto> productos = new ArrayList<>();

    @Column(nullable = false)
    private boolean activo = true;

    // 🔥 Constructor vacío (obligatorio para JPA)
    public UnidadMedida() {
    }

    // 🔥 Constructor que recibe el DTO → útil para CREAR entidades desde el DTO
    public UnidadMedida(UnidadMedidaDTO dto) {
        this.nombreMedida = dto.getNombreMedida();
        this.sigla = dto.getSigla();
        this.activo = dto.isActivo();
    }

    // 🔥 Para actualización no se recomienda un constructor,
    //     se hace desde el service con setters.

    public int getIdUnidadMedida() {
        return idUnidadMedida;
    }

    public void setIdUnidadMedida(int idUnidadMedida) {
        this.idUnidadMedida = idUnidadMedida;
    }

    public String getNombreMedida() {
        return nombreMedida;
    }

    public void setNombreMedida(String nombreMedida) {
        this.nombreMedida = nombreMedida;
    }

    public String getSigla() {
        return sigla;
    }

    public void setSigla(String sigla) {
        this.sigla = sigla;
    }

    public List<Producto> getProductos() {
        return productos;
    }

    public void setProductos(List<Producto> productos) {
        this.productos = productos;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }
}

