package com.cristhian.SistemaInventario.Modelo;

import com.cristhian.SistemaInventario.DTO.AsientoContableDTO;
import com.cristhian.SistemaInventario.Enums.TipoAsiento;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
public class AsientoContable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idAsiento;

    private LocalDateTime fecha;

    private String descripcion;
   /* @Column(precision = 15, scale = 2)
    private BigDecimal Total;*/

    @Enumerated(EnumType.STRING)
    private TipoAsiento tipo;

    @OneToMany(mappedBy = "asiento", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnoreProperties("asiento") // evita recursión infinita
    private List<MovimientoContable> movimientos = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_compra")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "asientos"})
    private Compra compra;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_compra_pago")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "compraPagos"})
    private CompraPago pago;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_venta")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "asientos"})
    private Venta venta;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_venta_pago")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "ventaPagos"})
    private VentaPago ventaPago;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Usuario usuario;

    public void agregarMovimiento(MovimientoContable mov) {
        movimientos.add(mov);
        mov.setAsiento(this);
    }


    public AsientoContable() {
    }

    public AsientoContable(AsientoContableDTO asientoContableDTO){
        this.fecha = asientoContableDTO.getFecha();
        this.descripcion = asientoContableDTO.getDescripcion();
      //  this.Total = asientoContableDTO.getTotal();
    }

    public Long getIdAsiento() {
        return idAsiento;
    }

    public void setIdAsiento(Long idAsiento) {
        this.idAsiento = idAsiento;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }


    public List<MovimientoContable> getMovimientos() {
        return movimientos;
    }

    public void setMovimientos(List<MovimientoContable> movimientos) {
        this.movimientos = movimientos;
    }

    /*public BigDecimal getTotal() {
        return Total;
    }

    public void setTotal(BigDecimal total) {
        Total = total;
    }*/

    public Compra getCompra() {
        return compra;
    }

    public void setCompra(Compra compra) {
        this.compra = compra;
    }

    public CompraPago getPago() {
        return pago;
    }

    public void setPago(CompraPago pago) {
        this.pago = pago;
    }

    public TipoAsiento getTipo() {
        return tipo;
    }

    public void setTipo(TipoAsiento tipo) {
        this.tipo = tipo;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public Venta getVenta() {
        return venta;
    }

    public void setVenta(Venta venta) {
        this.venta = venta;
    }

    public VentaPago getVentaPago() {
        return ventaPago;
    }

    public void setVentaPago(VentaPago ventaPago) {
        this.ventaPago = ventaPago;
    }
}
