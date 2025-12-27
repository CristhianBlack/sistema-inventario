import { EstadoPago } from "./estado-pago";

export class VentaPago {

idVentaPago?: number;
  monto!: number;
  fechaPago?: Date;
  estadoPago!: EstadoPago;
  numeroCuotas?: number;
  fechaVencimientoCuota?: Date;

  idVenta!: number;
  idFormaPago!: number | null;
}

