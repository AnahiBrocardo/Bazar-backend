package com.bazar.api.dto;

import java.time.LocalDate;

public class ResumenVentasDTO {
    private LocalDate fecha;
    private Double monto_total;
    private int cantidad_ventas;

    public ResumenVentasDTO() {
    }

    public ResumenVentasDTO(int cantidad_ventas, Double monto_total, LocalDate fecha) {
        this.cantidad_ventas = cantidad_ventas;
        this.monto_total = monto_total;
        this.fecha = fecha;
    }

    public int getCantidad_ventas() {
        return cantidad_ventas;
    }

    public void setCantidad_ventas(int cantidad_ventas) {
        this.cantidad_ventas = cantidad_ventas;
    }

    public Double getMonto_total() {
        return monto_total;
    }

    public void setMonto_total(Double monto_total) {
        this.monto_total = monto_total;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }
}
