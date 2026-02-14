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

  cancelar(id: number) {
    return this.httpClient.put(`${this.URL}/${id}/cancelar`, {});
  }

  confirmarCompra(id: number) {
    return this.httpClient.put(`${this.URL}/${id}/confirmar`, {});
  }
  
  eliminarCompra(id: number): Observable<any> {
    return this.httpClient.delete<any>(`${this.URL}/${id}`).pipe(catchError(this.manejarError));
  }

   // Manejo centralizado de errores
  private manejarError(error : HttpErrorResponse){
    console.error('Error del servidor:', error)
    let mensaje = '';
    if(error.status === 400 && error.error?.mensaje){
      mensaje = error.error.mensaje; // mensaje personalizado del backend
    }else if (error.status === 404) {
          mensaje = error.error.mensaje
        } else if (error.status === 500) {
          mensaje = 'Error interno del servidor';
        }
        console.error(mensaje);
        return throwError(() => ({mensaje}));
  }
}
