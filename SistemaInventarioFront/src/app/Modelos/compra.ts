import { DetalleCompra } from "./detalle-compra";

export class Compra {

    idCompra? : number;

    fechaCompra : Date = new Date();

    total : number = 0;

    activo : boolean = true;

    //Relacciones
    idProveedor : number | null = null;

    detalles?: DetalleCompra[];
}
