package com.cristhian.SistemaInventario.Excepciones;

public class DuplicadoException extends RuntimeException {
    public DuplicadoException(String mensaje) {
        super(mensaje);
    }
}