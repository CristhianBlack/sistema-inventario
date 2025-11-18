import { Ciudad } from "./ciudad";
import { TipoDocumento } from "./tipo-documento";
import { TipoPersona } from "./tipo-persona";

export class Persona {

    [key: string]: any;   // ← agregado para permitir acceso dinámico

    idPersona?: number;
    documentoPersona : String = '';
    nombre : String = '';
    apellido : String = '';
    segundoApellido : String = '';
    direccion : String = '';
    telefono : String = '';
    email : String = '';
    idTipoDocumento: number | null = null;
    idCiudad: number | null = null;
    idTipoPersona: number | null = null;
    activo : boolean = true;
    idsRoles?: number[] = [];
    roles?: any[] = [];

    tipoDocumento? : TipoDocumento;
    ciudad?: Ciudad;
    tipoPersona ? : TipoPersona
}
