import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { environment } from 'src/environments/environment';
import { AperturaCuenta } from '../Modelos/apertura-cuenta';
import { catchError, throwError } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class AperturaCuentaService {

  URL =`${environment.apiUrl}/contabilidad/apertura`;

  constructor(private httpClient : HttpClient) { }

    abrirCuenta(data: AperturaCuenta) {
    return this.httpClient.post(this.URL, data).pipe(catchError(this.manejarError));
  }

  private manejarError(error: HttpErrorResponse) {
  let mensaje = 'Ocurrió un error';

  if (error.error?.mensaje) {
    mensaje = error.error.mensaje;
  } else if (error.status === 404) {
    mensaje = 'Recurso no encontrado';
  } else if (error.status === 500) {
    mensaje = error.error.mensaje;
  }

  return throwError(() => ({
    status: error.status,
    mensaje
  }));
}

}
