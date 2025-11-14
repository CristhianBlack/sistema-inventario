import { Observable } from "rxjs";
import { TipoPersona } from "../Modelos/tipo-persona";

export interface ITipoPersona {

    obtenerListaTipoPersona(): Observable<TipoPersona[]>;
    obtenerTipoPersonaById(id: number): Observable<TipoPersona>;
    agregarTipoPersona(tipoPersona : TipoPersona): Observable<any>;
    editarTipoPersona(id : number, tipoPersona : TipoPersona): Observable<any>;
    eliminarTipoPersona(id : number): Observable<any>;
}
