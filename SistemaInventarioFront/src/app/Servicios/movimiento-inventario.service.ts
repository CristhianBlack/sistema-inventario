import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from 'src/environments/environment';
import { MovimientoInventario } from '../Modelos/movimiento-inventario';

@Injectable({
  providedIn: 'root'
})
export class MovimientoInventarioService {

  constructor(private httpClient: HttpClient) { }

  URL = `${environment.apiUrl}/MovimientoInventario`;

  listarMovimientos(): Observable<MovimientoInventario[]>{
    return this.httpClient.get<MovimientoInventario[]>(this.URL);
  }

  getMovimientoInventarioPorFechas(desde: string,hasta: string): Observable<MovimientoInventario[]> {
    return this.httpClient.get<MovimientoInventario[]>(`${this.URL}/filtro?desde=${desde}&hasta=${hasta}`
    );
  }
  
  exportarExcel(desde: string, hasta: string) {
    return this.httpClient.get(
      `${this.URL}/excel?desde=${desde}&hasta=${hasta}`,
      { responseType: 'blob' }
    );
  }
  
  exportarPdf(desde: string, hasta: string) {
    return this.httpClient.get(
      `${this.URL}/pdf?desde=${desde}&hasta=${hasta}`,
      { responseType: 'blob' }
    );
  }
}
