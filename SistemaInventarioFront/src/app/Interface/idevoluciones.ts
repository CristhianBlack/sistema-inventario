import { Observable } from "rxjs";
import { DevolucionRequest } from "../Modelos/devolucion-request";

export interface IDevoluciones {

       
        registrarDevolucion(request: DevolucionRequest): Observable<any>
}
