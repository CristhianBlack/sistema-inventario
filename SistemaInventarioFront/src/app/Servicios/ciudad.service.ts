import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { catchError, Observable, throwError } from 'rxjs';
import { environment } from 'src/environments/environment';
import { Ciudad } from '../Modelos/ciudad';
import { ICiudadService } from '../Interface/iciudad-service';

@Injectable({
  providedIn: 'root',
})
export class CiudadService implements ICiudadService {
  URL = `${environment.apiUrl}/Ciudades`;
  constructor(private httpClient: HttpClient) {}

  //Obtiene todas las ciudades.
  public obtenerListaciudades(): Observable<Ciudad[]> {
    return this.httpClient
      .get<Ciudad[]>(this.URL)
      .pipe(catchError(this.manejarError));
  }

  //Obtenenos la ciudad por el id
  public obetnerCiudadesPorId(id: number): Observable<Ciudad> {
    return this.httpClient
      .get<Ciudad>(`${this.URL}/${id}`)
      .pipe(catchError(this.manejarError));
  }

  // Agregamos nueva ciudad
  public agregarCiudad(ciudad: Ciudad): Observable<any> {
    return this.httpClient
      .post<any>(this.URL, ciudad)
      .pipe(catchError(this.manejarError));
  }

  //Editar ciudad existente
  public editarCiudad(id: number, ciudad: Ciudad): Observable<any> {
    return this.httpClient
      .put<any>(`${this.URL}/${id}`, ciudad)
      .pipe(catchError(this.manejarError));
  }

  //Eliminar Ciudad
  public eliminarciudad(id: number): Observable<any> {
    return this.httpClient
      .delete<any>(`${this.URL}/${id}`)
      .pipe(catchError(this.manejarError));
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
