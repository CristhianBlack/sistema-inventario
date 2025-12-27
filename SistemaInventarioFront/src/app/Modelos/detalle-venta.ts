export class DetalleVenta {

    idDetalleVenta? : number;
    cantidad : number = 0;
    precioUnitario : number = 0;
    descuento : number = 0;
    subtotalLinea :number = 0;
    impuestoLinea : number = 0;
    totalLinea : number = 0;

     nombreProducto ?: String = "";

    idVenta : number | null = null;
    idProducto : number | null = null;
    idImpuesto : number | null = null;
}
