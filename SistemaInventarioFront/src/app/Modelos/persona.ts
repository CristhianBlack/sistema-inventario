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
    saldoFavor: number = 0;
    razonSocial: string = '';
    nombreContacto: string ='';
    apellidoContacto: string ='';
    segundoApellidoContacto: string ='';

    nombreTipoDocumento?: string;
    nombreTipoPersona?: String;
    nombreCiudad?: String;

    tipoDocumento? : TipoDocumento;
    ciudad?: Ciudad;
    tipoPersona ? : TipoPersona
}
