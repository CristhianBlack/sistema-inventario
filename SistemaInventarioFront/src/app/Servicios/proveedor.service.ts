import { Injectable } from '@angular/core';
import { Iproveedor } from '../Interface/iproveedor';
import { catchError, Observable, throwError } from 'rxjs';
import { Proveedor } from '../Modelos/proveedor';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { environment } from 'src/environments/environment';

@Injectable({
  providedIn: 'root'
})
export class ProveedorService implements Iproveedor{

  Url = `${environment.apiUrl}/Proveedores`

  constructor(private httpClient : HttpClient) { }
  obtenerListaProveedores(): Observable<Proveedor[]> {
    return this.httpClient.get<Proveedor[]>(this.Url).pipe(catchError(this.manejarError));
  }
  obtenerProveedorPorId(id: number): Observable<Proveedor> {
    return this.httpClient.get<Proveedor>(`${this.Url}/${id}`).pipe(catchError(this.manejarError));
  }
  guardarProveedor(proveedor: Proveedor): Observable<any> {
    return this.httpClient.post<any>(this.Url, proveedor).pipe(catchError(this.manejarError));
  }
  actualizarProveedor(id: number, proveedor: Proveedor): Observable<any> {
    return this.httpClient.put<any>(`${this.Url}/${id}`, proveedor).pipe(catchError(this.manejarError));
  }
  eliminarProveedor(id: number): Observable<any> {
    return this.httpClient.delete<any>(`${this.Url}/${id}`).pipe(catchError(this.manejarError));
  }

  listarProveedorPersona(): Observable<any[]> {
    return this.httpClient.get<any[]>(`${this.Url}-persona`).pipe(catchError(this.manejarError));
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
