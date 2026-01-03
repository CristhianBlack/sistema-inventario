export class DevolucionRequest {

    motivo: string = "";
    cantidad: number = 0;
    tipoDevolucion: string = "";

    idProducto!: number;
    idVenta!: number;
    idProductoCambio?: number | null;
}



