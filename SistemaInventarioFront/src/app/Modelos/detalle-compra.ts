export class DetalleCompra {
    idDetalleCompra? : number;
    
    cantidad : number = 0;

    precioUnitario : number = 0;
    
    subtotalLinea: number = 0;
    
     impuestoLinea: number = 0;

     totalLinea: number = 0;

    nombreProducto ?: String = "";

    //Relacciones
    idCompra : number | null = null;
    idProducto : number | null = null;
}
