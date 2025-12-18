import { Injectable } from '@angular/core';
import { IProducto } from '../Interface/iproducto';
import { catchError, Observable, throwError } from 'rxjs';
import { Producto } from '../Modelos/producto';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { environment } from 'src/environments/environment';

@Injectable({
  providedIn: 'root'
})
export class ProductoService implements IProducto{

  URL = `${environment.apiUrl}/Productos`;

  constructor(private httpClient : HttpClient) { }
  obtenerListaProductos(): Observable<Producto[]> {
    return this.httpClient.get<Producto[]>(this.URL).pipe(catchError(this.manejarError));
  }
  obtenerProductoPorId(id: number): Observable<Producto> {
    return this.httpClient.get<Producto>(`${this.URL}/${id}`).pipe(catchError(this.manejarError));
  }
  guardarProducto(producto: Producto): Observable<any> {
    return this.httpClient.post<any>(this.URL, producto).pipe(catchError(this.manejarError));
  }
  actualizarProducto(id: number, producto: Producto): Observable<any> {
    return this.httpClient.put<any>(`${this.URL}/${id}`, producto).pipe(catchError(this.manejarError));
  }
  eliminarProducto(id: number): Observable<any> {
    return this.httpClient.delete<any>(`${this.URL}/${id}`).pipe(catchError(this.manejarError));
  }

  // Manejo centralizado de errores
    private manejarError(error: HttpErrorResponse) {
  console.error("🔥 ERROR COMPLETO DEL SERVIDOR:", error);

  // si el backend envía un JSON con "mensaje", úsalo
  const mensaje =
    error.error?.mensaje ||
    error.error ||
    "Error desconocido en el servidor";

  console.error("🔥 MENSAJE DEL BACKEND:", mensaje);

  // devolvemos el error REAL
  return throwError(() => error);
}
}
