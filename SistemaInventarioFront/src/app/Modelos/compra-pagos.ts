import { EstadoPago } from "./estado-pago";

export class CompraPagos {

    idCompraPago? : number;
    montoCompra!: number;
    fechaPago?:Date;
    estadoPago!: EstadoPago;
    numeroCuotas?: number;
    fechaVencimientoCuota?: Date;

    //Relaciones
    idCompra!: number;
    idFormaPago!: number | null;
}
