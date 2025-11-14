import { Injectable } from '@angular/core';
import { environment } from 'src/environments/environment';
import { Ipersona } from '../Interface/ipersona';
import { catchError, Observable, throwError } from 'rxjs';
import { Persona } from '../Modelos/persona';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';

@Injectable({
  providedIn: 'root'
})
export class PersonaService  implements Ipersona{

  URL = `${environment.apiUrl}/Personas`;

  constructor(
    private httpClient : HttpClient
  ) { }

  obtenerListaPersona(): Observable<Persona[]> {
    return this.httpClient.get<Persona[]>(this.URL).pipe(catchError(this.manejarError));
  }
  obtnerPersonaPorID(id: number): Observable<Persona> {
    return this.httpClient.get<Persona>(`${this.URL}/${id}`).pipe(catchError(this.manejarError));
  }
  agregarPersona(persona: any): Observable<any> {
    return this.httpClient.post<any>(this.URL, persona).pipe(catchError(this.manejarError));
  }
  editarPersona(id: number, persona: any): Observable<any> {
    return this.httpClient.put<any>(`${this.URL}/${id}`, persona).pipe(catchError(this.manejarError));
  }
  eliminarPersona(id: number): Observable<any> {
    return this.httpClient.delete<any>(`${this.URL}/${id}`).pipe(catchError(this.manejarError));
  }

  // Obtener roles de una persona
obtenerRolesPorPersona(idPersona: number): Observable<any[]> {
  return this.httpClient.get<any[]>(`${this.URL}/PersonaRol/${idPersona}`);
}

// Asignar un rol a una persona
asignarRolPersona(idPersona: number, idRol: number): Observable<any> {
  return this.httpClient.post(`${this.URL}/PersonaRol/asignar/${idPersona}/${idRol}`, {});
}

// Eliminar un rol
eliminarRolPersona(idPersonaRol: number): Observable<void> {
  return this.httpClient.delete<void>(`${this.URL}/PersonaRol/${idPersonaRol}`);
}

  // Manejo centralizado de errores
    private manejarError(error: HttpErrorResponse) {
      console.error('Error del servidor:', error);
      let mensaje = '';
      if (error.status === 400 && error.error?.mensaje) {
        mensaje = error.error.mensaje; // mensaje personalizado del backend
      } else if (error.status === 404) {
        mensaje = 'Recurso no encontrado';
      } else if (error.status === 500) {
        mensaje = 'Error interno del servidor';
      }
      console.error(mensaje);
      return throwError(() => new Error(mensaje));
    }
}
