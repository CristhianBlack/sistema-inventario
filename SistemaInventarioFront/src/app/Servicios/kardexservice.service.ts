import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { environment } from 'src/environments/environment';
import { Kardex } from '../Modelos/kardex';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class KardexserviceService {

  URL = `${environment.apiUrl}/Kardex`;

  constructor(private httpClient : HttpClient) { }

  getKardex(id: number): Observable<Kardex[]>  {
    console.log('URL KARDEX:', this.URL);
   return this.httpClient.get<Kardex[]>(`${this.URL}/${id}`);
}

getKardexPorFechas(id: number,desde: string,hasta: string): Observable<Kardex[]> {
  return this.httpClient.get<Kardex[]>(`${this.URL}/${id}/filtro?desde=${desde}&hasta=${hasta}`
  );
}

exportarExcel(id: number, desde: string, hasta: string) {
  return this.httpClient.get(
    `${this.URL}/${id}/excel?desde=${desde}&hasta=${hasta}`,
    { responseType: 'blob' }
  );
}

exportarPdf(id: number, desde: string, hasta: string) {
  return this.httpClient.get(
    `${this.URL}/${id}/pdf?desde=${desde}&hasta=${hasta}`,
    { responseType: 'blob' }
  );
}


}
