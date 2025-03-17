package com.bazar.api.model;

import jakarta.persistence.*;

import java.util.List;

@Entity
public class Venta {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "producto_seq")
    @SequenceGenerator(name = "producto_seq", sequenceName = "nombre_secuencia", allocationSize = 1)
    private Long id_venta;
    private Long codigo_venta;
    private Double total_venta;
    private List<Producto> lista_productos;
    private Cliente cliente;
    private Estado estado_venta;

    public Venta() {
    }

    public Long getId_venta() {
        return id_venta;
    }

    public Long getCodigo_venta() {
        return codigo_venta;
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

    public void setCodigo_venta(Long codigo_venta) {
        this.codigo_venta = codigo_venta;
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
