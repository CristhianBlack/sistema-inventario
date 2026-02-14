import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { environment } from 'src/environments/environment';
import { DashboardTotales } from '../Modelos/Dashboard-Totales';

@Injectable({
  providedIn: 'root'
})
export class DashboardService {

  URL = `${environment.apiUrl}/totales`;

  constructor(private httpClient: HttpClient) {}

  obtenerTotales() {
    return this.httpClient.get<DashboardTotales>(this.URL);
  }
}
