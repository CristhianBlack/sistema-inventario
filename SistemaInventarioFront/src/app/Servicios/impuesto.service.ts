import { Injectable } from '@angular/core';
import { IImpuesto } from '../Interface/iimpuesto';
import { catchError, Observable, throwError } from 'rxjs';
import { Impuesto } from '../Modelos/impuesto';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { environment } from 'src/environments/environment';

@Injectable({
  providedIn: 'root'
})
export class ImpuestoService implements IImpuesto{

  URL = `${environment.apiUrl}/Impuesto`;

  constructor(private httpClient : HttpClient) { }

  listarImpuestos(): Observable<Impuesto[]> {
    return this.httpClient.get<Impuesto[]>(this.URL).pipe(catchError(this.manejarError));
  }
  buscarImpuestoPorId(id: number): Observable<Impuesto> {
    return this.httpClient.get<Impuesto>(`${this.URL}/${id}`).pipe(catchError(this.manejarError));
  }
  elminarIpuesto(id: number): Observable<any> {
    return this.httpClient.delete<any>(`${this.URL}/${id}`).pipe(catchError(this.manejarError));
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
