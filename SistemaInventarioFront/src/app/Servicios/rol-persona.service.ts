import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { environment } from 'src/environments/environment';
import { IRolPersona } from '../Interface/irolpersona';
import { catchError, Observable, pipe, throwError } from 'rxjs';
import { RolPersona } from '../Modelos/rol-persona';

@Injectable({
  providedIn: 'root'
})
export class RolPersonaService implements IRolPersona{

  URL = `${environment.apiUrl}/rolPersonas`;

  constructor(
    private httpClient : HttpClient
  ) { }
  obtenerListaRolPersona(): Observable<RolPersona[]> {
    return this.httpClient.get<RolPersona[]>(this.URL).pipe(catchError(this.manejarError));
  }
  obetnerRolPersonaPorId(id: number): Observable<RolPersona> {
    return this.httpClient.get<RolPersona>(`${this.URL}/${id}`).pipe(catchError(this.manejarError));
  }
  agregarRolPersona(rolPersona : RolPersona): Observable<any> {
    return this.httpClient.post<any>(this.URL, rolPersona).pipe(catchError(this.manejarError));
  }
  editarRolPersona(id: number, rolPersona: RolPersona): Observable<any> {
    return this.httpClient.put<any>(`${this.URL}/${id}`, rolPersona).pipe(catchError(this.manejarError));
  }
  eliminarRolPersona(id: number): Observable<any> {
    return this.httpClient.delete<any>(`${this.URL}/${id}`).pipe(catchError(this.manejarError))
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
