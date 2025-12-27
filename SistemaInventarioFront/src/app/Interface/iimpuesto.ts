import { Observable } from "rxjs";
import { Impuesto } from "../Modelos/impuesto";

export interface IImpuesto {

    listarImpuestos(): Observable<Impuesto[]>;
    buscarImpuestoPorId(id: number): Observable<Impuesto>;
    elminarIpuesto(id : number): Observable<any>;
}
