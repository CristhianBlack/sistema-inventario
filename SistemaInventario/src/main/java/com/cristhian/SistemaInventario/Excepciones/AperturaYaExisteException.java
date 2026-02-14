package com.cristhian.SistemaInventario.Excepciones;

public class AperturaYaExisteException extends  RuntimeException{

    public AperturaYaExisteException(String mensaje) {
        super(mensaje);
    }
}
