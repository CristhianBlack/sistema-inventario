import { Injectable } from '@angular/core';
import { IUnidadMedida } from '../Interface/iunidadmedida';
import { catchError, Observable, throwError } from 'rxjs';
import { UnidadMedida } from '../Modelos/unidad-medida';
import { environment } from 'src/environments/environment';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';

@Injectable({
  providedIn: 'root'
})
export class UnidadMedidaService implements IUnidadMedida{

  URL = `${environment.apiUrl}/Unidades`

  constructor(private httpclient : HttpClient) { }

  obtenerListaUnidadMedida(): Observable<UnidadMedida[]> {
    return this.httpclient.get<UnidadMedida[]>(this.URL).pipe(catchError(this.manejarError));
  }
  obtenerUnidadMedidaPorId(id: number): Observable<UnidadMedida> {
    return this.httpclient.get<UnidadMedida>(`${this.URL}/${id}`).pipe(catchError(this.manejarError));
  }
  guardarUnidadMedida(unidadMedida: UnidadMedida): Observable<any> {
    return this.httpclient.post<any>(this.URL, unidadMedida).pipe(catchError(this.manejarError));
  }
  editarUnidadMedida(id: number, unidadMedida: UnidadMedida): Observable<any> {
    return this.httpclient.put<any>(`${this.URL}/${id}`, unidadMedida).pipe(catchError(this.manejarError));
  }
  eliminarUnidadMedida(id: number): Observable<any> {
    return this.httpclient.delete<any>(`${this.URL}/${id}`).pipe(catchError(this.manejarError));
  }

  // Manejo centralizado de errores
      private manejarError(error : HttpErrorResponse){
        console.error('Error del servidor:', error)
        let mensaje = '';
        if(error.status === 400 && error.error?.mensaje){
          mensaje = error.error.mensaje; // mensaje personalizado del backend
        }else if (error.status === 404) {
              mensaje = 'Recurso no encontrado';
            } else if (error.status === 500) {
              mensaje = 'Error interno del servidor';
            }
            console.error(mensaje);
            return throwError(() => new Error(mensaje));
      }
}
