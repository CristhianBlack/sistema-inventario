import { DetalleVenta } from "./detalle-venta";
import { VentaPago } from "./venta-pago";

export class Venta {

    idVenta? : number ;
    fechaVenta : Date = new Date;
    subTotalVenta : number = 0;
    totalImpuestos : number = 0;
    totalVenta: number = 0;
    estado : string = ""; // Pendiente, parcial , pagada
    pagos?: VentaPago[] | null;

    //Relaciones
    idPersona : number | null = null;
    detalles?: DetalleVenta[];
}

