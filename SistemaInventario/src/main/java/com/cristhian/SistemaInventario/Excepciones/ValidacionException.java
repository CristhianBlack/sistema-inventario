package com.cristhian.SistemaInventario.Excepciones;


public class ValidacionException extends RuntimeException {
    public ValidacionException(String mensaje) {
        super(mensaje);
    }
}
