import { Injectable } from '@angular/core';
import { IFormaPago } from '../Interface/iforma-pago';
import { catchError, Observable, throwError } from 'rxjs';
import { FormaPago } from '../Modelos/forma-pago';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { environment } from 'src/environments/environment';

@Injectable({
  providedIn: 'root'
})
export class FormaPagoService implements IFormaPago {

  constructor(private httpClient : HttpClient) { }

  URL = `${environment.apiUrl}/FormaPago`;

  obtenerListaFormaPago(): Observable<FormaPago[]> {
    return this.httpClient.get<FormaPago[]>(this.URL).pipe(catchError(this.manejarError));
  }
  obtenerFormaPagoPorId(id: number): Observable<FormaPago> {
    return this.httpClient.get<FormaPago>(`${this.URL}/${id}`).pipe(catchError(this.manejarError));
  }
  editarFormaPago(id: number, formaPago: FormaPago): Observable<any> {
    return this.httpClient.put<any>(`${this.URL}/${id}`, formaPago).pipe(catchError(this.manejarError));
  }
  eliminarFormPago(id: number): Observable<any> {
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
