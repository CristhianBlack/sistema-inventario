import { Observable } from "rxjs";
import { UnidadMedida } from "../Modelos/unidad-medida";

export interface IUnidadMedida {

    obtenerListaUnidadMedida(): Observable<UnidadMedida[]>;
    obtenerUnidadMedidaPorId(id: number): Observable<UnidadMedida>;
    guardarUnidadMedida(unidadMedida : UnidadMedida): Observable<any>;
    editarUnidadMedida(id : number, unidadMedida : UnidadMedida): Observable<any>;
    eliminarUnidadMedida(id : number):Observable<any>; 
}
