import { DetalleCompra } from "./detalle-compra";

export class Compra {

    idCompra? : number;

    fechaCompra : Date = new Date();

    subTotalCompra : number = 0;
    totalImpuestos : number = 0; 
    totalCompra :number = 0;
    estado : string = ""; // Pendiente, parcial , pagada
    //pagos?: CompraPago[] | null;

    activo : boolean = true;

    //Relacciones
    idProveedor : number | null = null;

    detalles?: DetalleCompra[];
}
