import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { environment } from 'src/environments/environment';
import { BalanceGeneral } from '../Modelos/balance-general';

@Injectable({
  providedIn: 'root'
})
export class BalanceGeneralService {

  URL = `${environment.apiUrl}/contabilidad/balance-general`

  constructor(private httpClient : HttpClient) { }

  obtenerBalance() {
    return this.httpClient.get<BalanceGeneral>(this.URL);
  }

  exportarExcel() {
    return this.httpClient.get(`${this.URL}/excel`,{ responseType: 'blob' });
  }

  exportarPdf() {
    return this.httpClient.get(`${this.URL}/pdf`, { responseType: 'blob' });
  }
}
