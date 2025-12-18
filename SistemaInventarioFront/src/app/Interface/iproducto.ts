import { Observable } from "rxjs";
import { Producto } from "../Modelos/producto";

export interface IProducto {

    obtenerListaProductos(): Observable<Producto[]>;
    obtenerProductoPorId(id : number): Observable<Producto>;
    guardarProducto(producto : Producto): Observable<any>;
    actualizarProducto(id : number, producto : Producto) : Observable<any>;
    eliminarProducto(id : number) : Observable<any>;

}
