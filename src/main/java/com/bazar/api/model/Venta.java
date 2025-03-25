package com.bazar.api.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.List;

@Entity
public class Venta {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "producto_seq")
    @SequenceGenerator(name = "producto_seq", sequenceName = "nombre_secuencia", allocationSize = 1)
    private Long id_venta;
    private Double total_venta;
    @OneToMany(mappedBy = "venta")
    @JsonManagedReference
    private List<Producto> lista_productos;
    @ManyToOne
    @JoinColumn(name="cliente_id")
    private Cliente cliente;// muchas ventas están asociadas con un único "cliente"
    @Enumerated(EnumType.STRING)
    private Estado estado_venta;
    private LocalDate fecha_realizacion_venta;

    public Venta() {
    }

    public Venta(LocalDate fecha_realizacion_venta, Estado estado_venta, Cliente cliente, List<Producto> lista_productos, Double total_venta, Long id_venta) {
        this.fecha_realizacion_venta = fecha_realizacion_venta;
        this.estado_venta = estado_venta;
        this.cliente = cliente;
        this.lista_productos = lista_productos;
        this.total_venta = total_venta;
        this.id_venta = id_venta;
    }

    public Venta( Double total_venta, List<Producto> lista_productos, Cliente cliente, Estado estado_venta, LocalDate fecha_realizacion_venta) {
        this.total_venta = total_venta;
        this.lista_productos = lista_productos;
        this.cliente = cliente;
        this.estado_venta = estado_venta;
        this.fecha_realizacion_venta = fecha_realizacion_venta;
    }

    public LocalDate getFecha_realizacion_venta() {
        return fecha_realizacion_venta;
    }

    public void setFecha_realizacion_venta(LocalDate fecha_realizacion_venta) {
        this.fecha_realizacion_venta = fecha_realizacion_venta;
    }

    public Long getId_venta() {
        return id_venta;
    }



    public Double getTotal_venta() {
        return total_venta;
    }

    public List<Producto> getLista_productos() {
        return lista_productos;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public Estado getEstado_venta() {
        return estado_venta;
    }

    public void setId_venta(Long id_venta) {
        this.id_venta = id_venta;
    }


    public void setTotal_venta(Double total_venta) {
        this.total_venta = total_venta;
    }

    public void setLista_productos(List<Producto> lista_productos) {
        this.lista_productos = lista_productos;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public void setEstado_venta(Estado estado_venta) {
        this.estado_venta = estado_venta;
    }
}
