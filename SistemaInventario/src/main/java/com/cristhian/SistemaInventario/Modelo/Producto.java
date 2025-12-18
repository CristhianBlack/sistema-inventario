package com.cristhian.SistemaInventario.Modelo;

import com.cristhian.SistemaInventario.DTO.ProductoDTO;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idProducto;
    @Column(nullable = false, length = 30)
    private String codigoProducto;
    @Column(nullable = false, length = 50)
    private  String nombreProducto;
    @Column(nullable = false)
    private double precioCompra;
    @Column(nullable = false)
    private double precioVenta;
    @Column(nullable = false)
    private int stock;
    @Column(nullable = false)
    private int stockMinimo;
    @Column(nullable = false, length = 255)
    private String descripcion;

    @CreationTimestamp
    @Column(nullable = false)
    private LocalDate fechaCreacion;
    @Column(nullable = false)
    private boolean activo = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_categoria")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "productos"})
    private Categoria categoria;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_unidad_medida")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "productos"})
    private UnidadMedida unidadMedida;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_proveedor")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "productos"})
    private Proveedor proveedor;

    // Relación con producto
    @OneToMany(mappedBy = "producto", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnoreProperties("producto") // evita recursión infinita
    private List<DetalleCompra> detalles = new ArrayList<>();

    @OneToMany(mappedBy = "producto" )
    @JsonIgnoreProperties("producto") // evita recursión infinita
    private List<MovimientoInventario> movimientos = new ArrayList<>();

    @OneToMany(mappedBy = "producto", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnoreProperties("producto") // 👈 evita recursión infinita
    private List<DetalleVenta> detalleVentas = new ArrayList<>();

    @Override
    public String toString() {
        return "Producto{" +
                "idProducto=" + idProducto +
                ", nombreProducto='" + nombreProducto + '\'' +
                ", codigoProducto='" + codigoProducto + '\'' +
                ", precioCompra=" + precioCompra +
                ", precioVenta=" + precioVenta +
                ", stock=" + stock +
                ", stockMinimo=" + stockMinimo +
                ", descripcion='" + descripcion + '\'' +
                ", fechaCreacion=" + fechaCreacion +
                ", active=" + activo +
                ", categoria=" + (categoria != null ? categoria.getNombreCategoria() : "N/A") +
                ", unidadMedida=" + (unidadMedida != null ? unidadMedida.getNombreMedida() : "N/A") +
                //", proveedor=" + (proveedor != null ? proveedor.getPersonaContacto() : "N/A") +
                '}';
    }

    public Producto() {
    }

    public Producto(ProductoDTO productoDTO){
        this.codigoProducto = productoDTO.getCodigoProducto();
        this.nombreProducto = productoDTO.getNombreProducto();
        this.precioCompra = productoDTO.getPrecioCompra();
        this.precioVenta = productoDTO.getPrecioVenta();
        this.stock = productoDTO.getStock();
        this.stockMinimo = productoDTO.getStockMinimo();
        this.descripcion = productoDTO.getDescripcion();
        this.fechaCreacion = productoDTO.getFechaCreacion();
        this. activo = productoDTO.isActivo();
    }

    public int getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(int idProducto) {
        this.idProducto = idProducto;
    }

    public String getCodigoProducto() {
        return codigoProducto;
    }

    public void setCodigoProducto(String codigoProducto) {
        this.codigoProducto = codigoProducto;
    }

    public String getNombreProducto() {
        return nombreProducto;
    }

    public void setNombreProducto(String nombreProducto) {
        this.nombreProducto = nombreProducto;
    }

    public double getPrecioCompra() {
        return precioCompra;
    }

    public void setPrecioCompra(double precioCompra) {
        this.precioCompra = precioCompra;
    }

    public double getPrecioVenta() {
        return precioVenta;
    }

    public void setPrecioVenta(double precioVenta) {
        this.precioVenta = precioVenta;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public int getStockMinimo() {
        return stockMinimo;
    }

    public void setStockMinimo(int stockMinimo) {
        this.stockMinimo = stockMinimo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    /*public int getIdUnidadMedida() {
        return idUnidadMedida;
    }

    public void setIdUnidadMedida(int idUnidadMedida) {
        this.idUnidadMedida = idUnidadMedida;
    }*/

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

    public Categoria getCategoria() {
        return categoria;
    }

    public void setCategoria(Categoria categoria) {
        this.categoria = categoria;
    }

    public UnidadMedida getUnidadMedida() {
        return unidadMedida;
    }

    public void setUnidadMedida(UnidadMedida unidadMedida) {
        this.unidadMedida = unidadMedida;
    }

    public Proveedor getProveedor() {
        return proveedor;
    }

    public void setProveedor(Proveedor proveedor) {
        this.proveedor = proveedor;
    }


}
