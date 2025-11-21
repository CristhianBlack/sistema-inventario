package com.cristhian.SistemaInventario.Excepciones;

import java.time.LocalDateTime;

public class ErrorRespuesta {

    private String mensaje;
    private LocalDateTime timestamp;

    public ErrorRespuesta(String mensaje) {
        this.mensaje = mensaje;
        this.timestamp = LocalDateTime.now();
    }

    public String getMensaje() {
        return mensaje;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }
}
