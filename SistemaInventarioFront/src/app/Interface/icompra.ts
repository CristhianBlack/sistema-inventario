import { Observable } from "rxjs";
import { Compra } from "../Modelos/compra";

export interface ICompra {

    obtenerListaCompra() : Observable<Compra[]>;
    obtenerCompraPorId(id : number) : Observable<Compra>;
    agregarCompra(compra : Compra) : Observable<any>;
    editarCompra(id : number, compra : Compra) : Observable<any>;
    eliminarCompra(id : number) : Observable<any>;
}
