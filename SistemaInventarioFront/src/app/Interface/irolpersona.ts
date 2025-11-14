import { Observable } from "rxjs";
import { RolPersona } from "../Modelos/rol-persona";

export interface IRolPersona {

     obtenerListaRolPersona(): Observable<RolPersona[]>;
     obetnerRolPersonaPorId(id:number): Observable<RolPersona>;
     agregarRolPersona(rolPersona : RolPersona): Observable<any>
     editarRolPersona(id : number, rolPrsona : RolPersona): Observable<any>;
     eliminarRolPersona(id : number): Observable<any>
}
