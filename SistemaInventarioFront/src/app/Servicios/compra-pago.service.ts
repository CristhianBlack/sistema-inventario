import { Injectable } from '@angular/core';
import { ICompraPago } from '../Interface/icompra-pago';
import { Observable } from 'rxjs';
import { CompraPagos } from '../Modelos/compra-pagos';
import { environment } from 'src/environments/environment';
import { HttpClient } from '@angular/common/http';

@Injectable({
  providedIn: 'root'
})
export class CompraPagoService implements ICompraPago{

 URL = `${environment.apiUrl}/Pago`
 
   constructor(private httpClient : HttpClient) { }
 
    registrarPago(id: number, pago: Partial<CompraPagos>): Observable<void> {
     return this.httpClient.post<void>(`http://localhost:8080/Inventario/Compras/${id}/Pagos`, pago);
   }
 
   confirmarPago(id: number): Observable<void> {
     return this.httpClient.put<void>(`${this.URL}/${id}/confirmar`, {});
   }
 
   rechazarPago(id: number): Observable<void> {
     return this.httpClient.put<void>(`${this.URL}/${id}/rechazar`, {});
   }
 
   listarPagosPorCompra(id: number): Observable<CompraPagos[]> {
     return this.httpClient.get<CompraPagos[]>(`http://localhost:8080/Inventario/Compras/${id}/Pagos`);
   }
}
