import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { catchError, Observable, throwError } from 'rxjs';
import { Categoria } from '../Modelos/categoria';
import { environment } from 'src/environments/environment';
import { ICategoriaService } from '../Interface/icategoria-service.interface';

/*
  Este servicio maneja todas las operaciones CRUD
  de la entidad Categoría.
  La URL base se obtiene del environment.ts para
  poder cambiar fácilmente entre desarrollo y producción.
*/
@Injectable({
  providedIn: 'root'
})
export class CategoriaService implements ICategoriaService{

  URL = `${environment.apiUrl}/Categorias`;

  constructor(private httpClient : HttpClient) { }

  // Obtener todas las categorías
  public obtenerListaCategoria(): Observable<Categoria[]> {
    return this.httpClient.get<Categoria[]>(this.URL)
      .pipe(catchError(this.manejarError));
  }

  // Obtener categoría por ID
  public obtenerCategoriaPorId(id: number): Observable<Categoria> {
    return this.httpClient.get<Categoria>(`${this.URL}/${id}`)
      .pipe(catchError(this.manejarError));
  }

  // Agregar nueva categoría
  public agregarCategoria(categoria: Categoria): Observable<any> {
    return this.httpClient.post<any>(this.URL, categoria)
      .pipe(catchError(this.manejarError));
  }

  // Editar categoría existente
  public editarCategoria(id: number, categoria: Categoria): Observable<any> {
    return this.httpClient.put<any>(`${this.URL}/${id}`, categoria)
      .pipe(catchError(this.manejarError));
  }

  // Eliminar categoría
  public eliminarCategoria(id: number): Observable<any> {
    return this.httpClient.delete<any>(`${this.URL}/${id}`)
      .pipe(catchError(this.manejarError));
  }

  // Manejo centralizado de errores
  private manejarError(error: HttpErrorResponse) {
    let mensaje = '';
    if (error.error instanceof ErrorEvent) {
      // Error del cliente
      mensaje = `Error del cliente: ${error.error.message}`;
    } else {
      // Error del servidor
      mensaje = `Error del servidor (Código ${error.status}): ${error.message}`;
    }
    console.error(mensaje);
    return throwError(() => new Error(mensaje));
  }
}


