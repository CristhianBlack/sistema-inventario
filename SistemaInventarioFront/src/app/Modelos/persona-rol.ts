import { Persona } from "./persona";
import { RolPersona } from "./rol-persona";

export class PersonaRol {

    idPersonaRol?: number;
    
    persona?: Persona;

    rolPersona? : RolPersona;

    fechaAsignacion : String = '';
    
    activo  : boolean = true;
}
