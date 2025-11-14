import { Injectable } from '@angular/core';
import { ITipoPersona } from '../Interface/i-tipo-persona';
import { catchError, Observable, pipe, throwError } from 'rxjs';
import { TipoPersona } from '../Modelos/tipo-persona';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { environment } from 'src/environments/environment';

@Injectable({
  providedIn: 'root'
})
export class TipoPersonaService  implements ITipoPersona{

  URL = `${environment.apiUrl}/tipoPersonas`;

  constructor(
    private httpClient : HttpClient,
    
  ) { }
  obtenerListaTipoPersona(): Observable<TipoPersona[]> {
    return this.httpClient.get<TipoPersona[]>(this.URL).pipe(catchError(this.manejarError));
  }
  obtenerTipoPersonaById(id: number): Observable<TipoPersona> {
    return this.httpClient.get<TipoPersona>(`${this.URL}/${id}`).pipe(catchError(this.manejarError));
  }
  agregarTipoPersona(tipoPersona: TipoPersona): Observable<any> {
    return this.httpClient.post<any>(this.URL, tipoPersona).pipe(catchError(this.manejarError));
  }
  editarTipoPersona(id: number, tipoPersona: TipoPersona): Observable<any> {
    return this.httpClient.put<any>(`${this.URL}/${id}`, tipoPersona).pipe(catchError(this.manejarError));
  }
  eliminarTipoPersona(id: number): Observable<any> {
    return this.httpClient.delete<any>(`${this.URL}/${id}`).pipe(catchError(this.manejarError));
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
