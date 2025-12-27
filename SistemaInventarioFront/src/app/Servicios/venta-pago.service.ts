import { Injectable } from '@angular/core';
import { IVentaPago } from '../Interface/iventa-pago';
import { Observable } from 'rxjs';
import { HttpClient } from '@angular/common/http';
import { environment } from 'src/environments/environment';
import { VentaPago } from '../Modelos/venta-pago';

@Injectable({
  providedIn: 'root'
})
export class VentaPagoService  implements IVentaPago{


  URL = `${environment.apiUrl}/Pagos`

  constructor(private httpClient : HttpClient) { }

   registrarPago(idVenta: number, pago: Partial<VentaPago>): Observable<void> {
    return this.httpClient.post<void>(`http://localhost:8080/Inventario/Ventas/${idVenta}/Pagos`, pago);
  }

  confirmarPago(idPago: number): Observable<void> {
    return this.httpClient.put<void>(`${this.URL}/${idPago}/confirmar`, {});
  }

  rechazarPago(idPago: number): Observable<void> {
    return this.httpClient.put<void>(`${this.URL}/${idPago}/rechazar`, {});
  }

  listarPagosPorVenta(idVenta: number): Observable<VentaPago[]> {
    return this.httpClient.get<VentaPago[]>(`http://localhost:8080/Inventario/Ventas/${idVenta}/Pagos`);
  }

}
