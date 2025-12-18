import { Observable } from "rxjs";
import { FormaPago } from "../Modelos/forma-pago";

export interface IFormaPago {

    obtenerListaFormaPago(): Observable<FormaPago[]>;
    obtenerFormaPagoPorId(id : number): Observable<FormaPago>;
    editarFormaPago(id : number, formaPago : FormaPago): Observable<any>;
    eliminarFormPago(id : number): Observable<any>;
}
