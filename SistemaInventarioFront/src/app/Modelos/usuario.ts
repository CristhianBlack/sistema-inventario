import { Persona } from "./persona";

export class Usuario {

    idUsuario?: number;
    username: string ="";
    password: string ="";
    activo: boolean= true;
    debeCambiarPassword!: boolean;
    nombre!: string;
    apellido!: string;
    segundoApellido!: string;

    rolSeguridad : string = ""; // rol seguridad es un enum 
    // Relaciones
    idPersona : number | null = null;
}
