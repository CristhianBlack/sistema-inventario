import { Impuesto } from "./impuesto";

export class Producto {

    idProducto? :number;
    codigoProducto : String = '';
    nombreProducto : String ="";
    precioCompra : number = 0; 
    precioVenta : number = 0;
    stock : number = 0;
    stockMinimo : number = 0;
    descripcion : String ="";
    fechaCreacion : Date = new Date();
    activo : boolean = true;

    // Relaciones
    idCategoria : number | null = null;
    idUnidadMedida : number | null = null;
    idProveedor : number | null = null;
    idImpuesto :number |null = null;
}
