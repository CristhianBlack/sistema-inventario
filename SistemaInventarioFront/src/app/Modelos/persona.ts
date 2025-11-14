import { Ciudad } from "./ciudad";
import { TipoDocumento } from "./tipo-documento";
import { TipoPersona } from "./tipo-persona";

export class Persona {

    idPersona?: number;
    tipoDocumento? : TipoDocumento;
    documentoPersona : String = '';
    nombre : String = '';
    apellido : String = '';
    segundoApellido : String = '';
    direccion : String = '';
    telefono : String = '';
    email : String = '';
    tipoPersona? : TipoPersona;
    ciudad? : Ciudad;
    activo : boolean = true;
    idsRoles?: number[] = [];
    roles?: any[] = [];
}
