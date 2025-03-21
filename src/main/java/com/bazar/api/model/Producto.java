package com.bazar.api.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import org.springframework.boot.autoconfigure.batch.BatchTransactionManager;

import java.time.LocalDate;

@Entity
public class Producto {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "producto_seq")
    @SequenceGenerator(name = "producto_seq", sequenceName = "nombre_secuencia", allocationSize = 1)
    private Long id_producto;
    private Long codigoProducto;
    private String nombreProducto;
    private String marca;
    private Double costo;
    private int cantidad_disponible;
    private boolean disponible;
    private LocalDate fechaEliminacion;
    private String descripcion;
    private String url_imagen;
    @ManyToOne
    @JoinColumn (name="venta_id")
    @JsonBackReference
    private Venta venta;

    public Producto() {
    }

    public Producto(Long id_producto, Long codigoProducto, String nombreProducto, String marca, Double costo, int cantidad_disponible, boolean disponible, LocalDate fechaEliminacion, String descripcion, String url_imagen, Venta venta) {
        this.id_producto = id_producto;
        this.codigoProducto = codigoProducto;
        this.nombreProducto = nombreProducto;
        this.marca = marca;
        this.costo = costo;
        this.cantidad_disponible = cantidad_disponible;
        this.disponible = disponible;
        this.fechaEliminacion = fechaEliminacion;
        this.descripcion = descripcion;
        this.url_imagen = url_imagen;
        this.venta = venta;
    }

    public Long getId_producto() {
        return id_producto;
    }

    public Long getCodigoProducto() {
        return codigoProducto;
    }

    public String getNombreProducto() {
        return nombreProducto;
    }

    public String getMarca() {
        return marca;
    }

    public Double getCosto() {
        return costo;
    }

    public int getCantidad_disponible() {
        return cantidad_disponible;
    }

    public boolean isDisponible() {
        return disponible;
    }

    public LocalDate getFechaEliminacion() {
        return fechaEliminacion;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public String getUrl_imagen() {
        return url_imagen;
    }

    public Venta getVenta() {
        return venta;
    }


    public void setId_producto(Long id_producto) {
        this.id_producto = id_producto;
    }

    public void setCodigoProducto(Long codigoProducto) {
        this.codigoProducto = codigoProducto;
    }

    public void setNombreProducto(String nombreProducto) {
        this.nombreProducto = nombreProducto;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public void setCosto(Double costo) {
        this.costo = costo;
    }

    public void setCantidad_disponible(int cantidad_disponible) {
        this.cantidad_disponible = cantidad_disponible;
    }

    public void setDisponible(boolean disponible) {
        this.disponible = disponible;
    }

    public void setFechaEliminacion(LocalDate fechaEliminacion) {
        this.fechaEliminacion = fechaEliminacion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public void setUrl_imagen(String url_imagen) {
        this.url_imagen = url_imagen;
    }

    public void setVenta(Venta venta) {
        this.venta = venta;
    }


}
