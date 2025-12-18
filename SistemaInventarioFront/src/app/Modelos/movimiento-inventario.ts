export class MovimientoInventario {
    
    IdMovimientoInventario? :number;
    tipoMovimiento: String ="";
    origenMovimiento: String = "";
    cantidad : number = 0;
    observacion: String = "";
    fechaMovimiento : Date = new Date;

    //Relacciones
    idProducto : number | null = null;
    idProveedor : number | null = null;

}
