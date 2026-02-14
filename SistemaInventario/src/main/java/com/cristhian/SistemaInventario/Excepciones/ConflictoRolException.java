package com.cristhian.SistemaInventario.Excepciones;

public class ConflictoRolException extends RuntimeException {

    public ConflictoRolException(String mensaje){
        super(mensaje);
    }
}
