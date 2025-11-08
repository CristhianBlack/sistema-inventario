import { Observable } from "rxjs";
import { TipoDocumento } from "../Modelos/tipo-documento";

export interface ITipoDocumento {

    obtenerListaTipoDocumento(): Observable<TipoDocumento[]>;

    obtenerTipoDocumento(id: number): Observable<TipoDocumento>;
    
    agregarTipoDocumento(tipoDocumento : TipoDocumento): Observable<any>;

    editarTipoDocumento(id : number, tipoDocumento : TipoDocumento): Observable<any>;

    eliminarTipoDocumento(id : number): Observable<any>;
}
