import { Injectable } from '@angular/core';
import { ICompra } from '../Interface/icompra';
import { catchError, Observable, throwError } from 'rxjs';
import { Compra } from '../Modelos/compra';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { environment } from 'src/environments/environment';

@Injectable({
  providedIn: 'root'
})
export class CompraService implements ICompra{

  URL = `${environment.apiUrl}/Compras`;

  constructor(private httpClient : HttpClient) { }

  obtenerListaCompra(): Observable<Compra[]> {
    return this.httpClient.get<Compra[]>(this.URL).pipe(catchError(this.manejarError));
  }
  obtenerCompraPorId(id: number): Observable<Compra> {
    return this.httpClient.get<Compra>(`${this.URL}/${id}`).pipe(catchError(this.manejarError));
  }
  agregarCompra(compra: Compra): Observable<any> {
    return this.httpClient.post<any>(this.URL, compra).pipe(catchError(this.manejarError));
  }
  editarCompra(id: number, compra: Compra): Observable<any> {
    return this.httpClient.put<any>(`${this.URL}/${id}`, compra).pipe(catchError(this.manejarError));
  }
  eliminarCompra(id: number): Observable<any> {
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
