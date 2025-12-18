package com.cristhian.SistemaInventario.Modelo;

public enum EstadoPago {

    PENDIENTE_CONFIRMACION,
    CONFIRMADO,
    RECHAZADO,
    EN_CUOTAS, // En proceso de pago a cuotas
    CANCELADO
}
