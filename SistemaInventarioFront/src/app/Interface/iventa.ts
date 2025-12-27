import { Observable } from "rxjs";
import { Venta } from "../Modelos/venta";

export interface IVenta {
   

  listarVentas(): Observable<Venta[]>;

  obtenerVentaPorId(id: number): Observable<Venta>;

  guardarVenta(venta: Venta): Observable<any>; 

  cancelar(id: number): Observable<any>;
}
