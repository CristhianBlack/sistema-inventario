import { HttpClient, HttpParams } from '@angular/common/http';
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
  
  exportarExcel(desde: string | null, hasta: string | null) {
  let params = new HttpParams();

  if (desde && hasta) {
    params = params
      .set('desde', desde)
      .set('hasta', hasta);
  }

  return this.httpClient.get(
    `${this.URL}/excel`,
    {
      params,
      responseType: 'blob'
    }
  );
}

  
  exportarPdf(desde: string | null, hasta: string | null) {
  let params = new HttpParams();

  if (desde && hasta) {
    params = params
      .set('desde', desde)
      .set('hasta', hasta);
  }

  return this.httpClient.get(
    `${this.URL}/pdf`,
    {
      params,
      responseType: 'blob'
    }
  );
}
}
