export class Proveedor {

  idPersona?: number;
  documentoPersona!: string;
  nombre!: string;
  apellido!: string;
  segundoApellido!: string;
  direccion!: string;
  telefono!: string;
  email!: string;

  // Datos propios del proveedor
    idProveedor? : number;
    descripcionProveedor : String = "";
    fechaCreacion : Date = new Date();
    activo : boolean = true;
}
