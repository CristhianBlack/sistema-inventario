package com.cristhian.SistemaInventario.Excepciones;

public class RecursoNoEncontradoException extends RuntimeException {
        public RecursoNoEncontradoException(String mensaje) {
            super(mensaje);
        }
}
