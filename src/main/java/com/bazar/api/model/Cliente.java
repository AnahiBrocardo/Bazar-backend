package com.bazar.api.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
public class Cliente {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "producto_seq")
    @SequenceGenerator(name = "producto_seq", sequenceName = "nombre_secuencia", allocationSize = 1)
    private Long id_cliente;
    private String nombre;
    private String apellido;
    private String dni;
    private boolean disponible;
    private LocalDateTime fechaEliminacion;
    @OneToMany(mappedBy = "cliente")
    @JsonIgnore
    private List<Venta> ventas; //Un cliente tiene muchas ventas asociadas

    public Cliente() {
    }

    public Cliente(Long id_cliente, String nombre, String apellido, String dni, boolean disponible, LocalDateTime fechaEliminacion, List<Venta> ventas) {
        this.id_cliente = id_cliente;
        this.nombre = nombre;
        this.apellido = apellido;
        this.dni = dni;
        this.disponible = disponible;
        this.fechaEliminacion = fechaEliminacion;
        this.ventas = ventas;
    }

    public Long getId_cliente() {
        return id_cliente;
    }

    public String getNombre() {
        return nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public String getDni() {
        return dni;
    }

    public boolean isDisponible() {
        return disponible;
    }

    public LocalDateTime getFechaEliminacion() {
        return fechaEliminacion;
    }

    public List<Venta> getVentas() {
        return ventas;
    }


    public void setId_cliente(Long id_cliente) {
        this.id_cliente = id_cliente;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public void setDisponible(boolean disponible) {
        this.disponible = disponible;
    }

    public void setFechaEliminacion(LocalDateTime fechaEliminacion) {
        this.fechaEliminacion = fechaEliminacion;
    }

    public void setVentas(List<Venta> ventas) {
        this.ventas = ventas;
    }
}
