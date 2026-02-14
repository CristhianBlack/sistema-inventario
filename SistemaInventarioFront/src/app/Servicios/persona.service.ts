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
// Obtenemos un listado personas con el rol de proveedor
obtenerPersonasConRolProveedor() {
    return this.httpClient.get<Persona[]>(`${this.URL}/proveedores`).pipe(catchError(this.manejarError));
  }

  // Obtenemos un listado personas con el rol de cliente
obtenerPersonasConRolCliente() {
    return this.httpClient.get<Persona[]>(`${this.URL}/clientes`).pipe(catchError(this.manejarError));
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
