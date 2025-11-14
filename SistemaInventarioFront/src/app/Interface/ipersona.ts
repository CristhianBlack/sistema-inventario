import { Observable } from "rxjs";
import { Persona } from "../Modelos/persona";

export interface Ipersona {

    obtenerListaPersona(): Observable<Persona[]>;
    obtnerPersonaPorID(id : number): Observable<Persona>;
    agregarPersona(persona : Persona): Observable<any>;
    editarPersona(id : number, persona : Persona): Observable<any>;
    eliminarPersona(id : number): Observable<any>; 
}
