import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { environment } from 'src/environments/environment';
import { Usuario } from '../Modelos/usuario';
import { catchError, Observable, throwError } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class UsuarioService {

  URL = `${environment.apiUrl}/usuario`

  constructor(private httpClient : HttpClient) { }

   crearUsuario(usuario: Usuario): Observable<any> {
    return this.httpClient.post(`${this.URL}`, usuario);
  }

  //Obtiene todas las ciudades.
    public obtenerListaUsuarios(): Observable<Usuario[]> {
      return this.httpClient
        .get<Usuario[]>(this.URL)
        .pipe(catchError(this.manejarError));
    }
  
    //Obtenenos la ciudad por el id
    public obetnerUsuarioPorId(id: number): Observable<Usuario> {
      return this.httpClient
        .get<Usuario>(`${this.URL}/${id}`)
        .pipe(catchError(this.manejarError));
    }
  
    //Editar usuario existente
    public editarUsuario(id: number, usuario: Usuario): Observable<any> {
      return this.httpClient
        .put<any>(`${this.URL}/${id}`, usuario)
        .pipe(catchError(this.manejarError));
    }
  
    //Eliminar Usuario
    public eliminarUsuario(id: number): Observable<any> {
      return this.httpClient
        .delete<any>(`${this.URL}/${id}`)
        .pipe(catchError(this.manejarError));
    }
  
   private manejarError(error: HttpErrorResponse) {
    let mensaje = 'Ocurrió un error';
  
    if (error.error?.mensaje) {
      mensaje = error.error.mensaje;
    } else if (error.status === 404) {
      mensaje = 'Recurso no encontrado';
    } else if (error.status === 500) {
      mensaje = 'Error interno del servidor';
    }
  
    return throwError(() => ({
      status: error.status,
      mensaje
    }));
  }
}
