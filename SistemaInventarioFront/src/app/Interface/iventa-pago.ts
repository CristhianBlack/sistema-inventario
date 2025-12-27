import { Observable } from "rxjs";
import { VentaPago } from "../Modelos/venta-pago";

export interface IVentaPago {

    registrarPago(idVenta: number, pago: Partial<VentaPago>): Observable<any>;
    confirmarPago(idPago: number): Observable<any>;
    rechazarPago(idPago: number): Observable<any>;
    listarPagosPorVenta(idVenta: number): Observable<VentaPago[]>;
}
