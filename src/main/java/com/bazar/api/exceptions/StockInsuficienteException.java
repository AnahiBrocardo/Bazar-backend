package com.bazar.api.exceptions;

public class StockInsuficienteException extends Exception{
    public StockInsuficienteException(String mensaje){
        super(mensaje);
    }
}
