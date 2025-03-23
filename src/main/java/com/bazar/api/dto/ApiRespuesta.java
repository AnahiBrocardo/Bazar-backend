package com.bazar.api.dto;

public class ApiRespuesta<T> {
    private boolean exito;
    private String mensaje;
    private T datos;

    public ApiRespuesta() {
    }

    public ApiRespuesta(boolean exito, String mensaje, T datos) {
        this.exito = exito;
        this.mensaje = mensaje;
        this.datos = datos;
    }



    public ApiRespuesta(boolean exito, String mensaje) {
        this.exito = exito;
        this.mensaje = mensaje;
    }

    public boolean isExito() {
        return exito;
    }

    public String getMensaje() {
        return mensaje;
    }

    public T getDatos() {
        return datos;
    }

    public void setExito(boolean exito) {
        this.exito = exito;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public void setDatos(T datos) {
        this.datos = datos;
    }
}
