import { Observable } from "rxjs";
import { CompraPagos } from "../Modelos/compra-pagos";

export interface ICompraPago {
    registrarPago(id: number, pago: Partial<CompraPagos>): Observable<any>;
    confirmarPago(id: number): Observable<any>;
    rechazarPago(id: number): Observable<any>;
    listarPagosPorCompra(id: number): Observable<CompraPagos[]>;
}
