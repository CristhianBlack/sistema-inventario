package com.cristhian.SistemaInventario.Excepciones;

public class AperturaNoExisteException extends RuntimeException{

    public AperturaNoExisteException(String mensaje) {
        super(mensaje);
    }
}
