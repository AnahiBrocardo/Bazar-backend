package com.bazar.api.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

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

    public Cliente() {
    }

    public Cliente(Long id_cliente, String nombre, String apellido, String dni, boolean disponible, LocalDateTime fechaEliminacion) {
        this.id_cliente = id_cliente;
        this.nombre = nombre;
        this.apellido = apellido;
        this.dni = dni;
        this.disponible = disponible;
        this.fechaEliminacion = fechaEliminacion;
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
}
