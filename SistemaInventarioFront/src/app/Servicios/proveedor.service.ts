import { Injectable } from '@angular/core';
import { Iproveedor } from '../Interface/iproveedor';
import { catchError, Observable, throwError } from 'rxjs';
import { Proveedor } from '../Modelos/proveedor';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { environment } from 'src/environments/environment';

@Injectable({
  providedIn: 'root',
})
export class ProveedorService implements Iproveedor {
  Url = `${environment.apiUrl}/Proveedores`;

  constructor(private httpClient: HttpClient) {}
  obtenerListaProveedores(): Observable<Proveedor[]> {
    return this.httpClient
      .get<Proveedor[]>(this.Url)
      .pipe(catchError(this.manejarError));
  }
  obtenerProveedorPorId(id: number): Observable<Proveedor> {
    return this.httpClient
      .get<Proveedor>(`${this.Url}/${id}`)
      .pipe(catchError(this.manejarError));
  }
  guardarProveedor(proveedor: Proveedor): Observable<any> {
    return this.httpClient
      .post<any>('http://localhost:8080/Inventario/Proveedor', proveedor)
      .pipe(catchError(this.manejarError));
  }
  actualizarProveedor(id: number, proveedor: Proveedor): Observable<any> {
    return this.httpClient
      .put<any>(`${this.Url}/${id}`, proveedor)
      .pipe(catchError(this.manejarError));
  }
  eliminarProveedor(id: number): Observable<any> {
    return this.httpClient
      .delete<any>(`${this.Url}/${id}`)
      .pipe(catchError(this.manejarError));
  }

  listarProveedorPersona(): Observable<any[]> {
    return this.httpClient
      .get<any[]>(`${this.Url}-persona`)
      .pipe(catchError(this.manejarError));
  }

  // Manejo centralizado de errores
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
      mensaje,
    }));
  }
}