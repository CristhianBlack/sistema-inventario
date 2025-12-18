import { Observable } from "rxjs";
import { Proveedor } from "../Modelos/proveedor";

export interface Iproveedor {

    obtenerListaProveedores() : Observable<Proveedor[]>;
    obtenerProveedorPorId(id :number) : Observable<Proveedor>;
    guardarProveedor(proveedor : Proveedor) : Observable<any>;
    actualizarProveedor(id : number, proveedor : Proveedor): Observable<any>;
    eliminarProveedor(id : number): Observable<any>;
}
