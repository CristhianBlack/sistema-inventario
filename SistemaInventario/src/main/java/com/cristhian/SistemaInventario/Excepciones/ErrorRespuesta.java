package com.cristhian.SistemaInventario.Excepciones;

import java.time.LocalDateTime;

public class ErrorRespuesta {

    private String mensaje;
    private int status;
    private LocalDateTime fecha;

    public ErrorRespuesta(String mensaje, int status) {
        this.mensaje = mensaje;
        this.status = status;
        this.fecha = LocalDateTime.now();
    }

    public String getMensaje() {
        return mensaje;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public int getStatus() {
        return status;
    }
}
