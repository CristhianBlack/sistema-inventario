import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { environment } from 'src/environments/environment';
import { IVenta } from '../Interface/iventa';
import { Observable } from 'rxjs';
import { Venta } from '../Modelos/venta';

@Injectable({
  providedIn: 'root'
})
export class VentaService implements IVenta{

  private URL = `${environment.apiUrl}/Ventas`;

  constructor(private httpClient: HttpClient) {}


  listarVentas(): Observable<Venta[]> {
    return this.httpClient.get<Venta[]>(this.URL);
  }
  obtenerVentaPorId(id: number): Observable<Venta> {
    return this.httpClient.get<Venta>(`${this.URL}/${id}`);
  }
  guardarVenta(venta: Venta): Observable<any> {
    return this.httpClient.post<Venta>(this.URL, venta);
  }
  cancelar(id: number) {
    return this.httpClient.put(`${this.URL}/${id}/cancelar`, {});
  }

  confirmarVenta(id: number) {
    return this.httpClient.put(`${this.URL}/${id}/confirmar`, {});
  }
}
