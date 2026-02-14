import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { environment } from 'src/environments/environment';
import { LibroDiario } from '../Modelos/libro-diario';
import { Observable } from 'rxjs';
import { Page } from '../Modelos/page';

@Injectable({
  providedIn: 'root'
})
export class LibroDiarioService {

  URL = `${environment.apiUrl}/contabilidad/libro-diario`

  constructor(private httpClient : HttpClient) { }

  obtenerLibroDiario(desde?: string, hasta?: string, page: number = 0, size: number = 10) {
  
    return this.httpClient.get<Page<LibroDiario>>(this.URL, {
    params: {
      desde: desde ?? '',
      hasta: hasta ?? '',
      page,
      size
    }
  });
 }

 exportarExcel(desde?: string, hasta?: string) {
  return this.httpClient.get(
    `${this.URL}/excel`,
    { params: { desde: desde ?? '', hasta: hasta ?? '' }, responseType: 'blob' }
  );
}

exportarPDF(desde?: string, hasta?: string) {
  return this.httpClient.get(
    `${this.URL}/pdf`,
    { params: { desde: desde ?? '', hasta: hasta ?? '' }, responseType: 'blob' }
  );
}

}
