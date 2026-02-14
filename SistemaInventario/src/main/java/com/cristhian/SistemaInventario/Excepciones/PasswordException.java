package com.cristhian.SistemaInventario.Excepciones;

public class PasswordException extends RuntimeException{

    public PasswordException(String mensaje){
        super(mensaje);
    }
}
