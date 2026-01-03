import { EstadoPago } from "./estado-pago";
import { FormaPago } from "./forma-pago";

export class VentaPago {

idVentaPago?: number;
  monto!: number;
  fechaPago?: Date;
  estadoPago!: EstadoPago;
  numeroCuotas?: number;
  fechaVencimientoCuota?: Date;

  nombreFormaPago?: String = "";

  idVenta!: number;
  //formaPago!: FormaPago | null;
  idFormaPago! : number;
}

