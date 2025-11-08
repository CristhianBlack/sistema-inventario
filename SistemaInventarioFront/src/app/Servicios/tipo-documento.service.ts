import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { environment } from 'src/environments/environment';
import { ITipoDocumento } from '../Interface/itipodocumento';
import { catchError, Observable, throwError } from 'rxjs';
import { TipoDocumento } from '../Modelos/tipo-documento';

@Injectable({
  providedIn: 'root'
})
export class TipoDocumentoService implements ITipoDocumento {

  URL = `${environment.apiUrl}/tipoDocumentos`;

  constructor(
    private httpClient : HttpClient
  ) { }

  //Metodo listar todos los tipo de documento
  obtenerListaTipoDocumento(): Observable<TipoDocumento[]> {
    return this.httpClient.get<TipoDocumento[]>(this.URL).pipe(catchError(this.manejarError));
  }
  //Metodo buscar tipo de documento por id
  obtenerTipoDocumento(id: number): Observable<TipoDocumento> {
    return this.httpClient.get<TipoDocumento>(`${this.URL}/${id}`).pipe(catchError(this.manejarError));
  }
  //Metodo agregar tipo de documento
  agregarTipoDocumento(tipoDocumento : TipoDocumento): Observable<any> {
    return this.httpClient.post<any>(this.URL, tipoDocumento).pipe(catchError(this.manejarError));
  }
  //Metodo editar tipo de documento
  editarTipoDocumento(id : number, tipoDocumento : TipoDocumento): Observable<any> {
    return this.httpClient.put<any>(`${this.URL}/${id}`, tipoDocumento).pipe(catchError(this.manejarError));
  }
  //Metodo eliminar tipo de documento
  eliminarTipoDocumento(id : number): Observable<any> {
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
