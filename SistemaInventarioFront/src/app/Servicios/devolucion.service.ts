import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { environment } from 'src/environments/environment';
import { IDevoluciones } from '../Interface/idevoluciones';
import { Observable } from 'rxjs';
import { DevolucionRequest } from '../Modelos/devolucion-request';

@Injectable({
  providedIn: 'root'
})
export class DevolucionService implements IDevoluciones{

  URL = `${environment.apiUrl}/Devolucion`;

  constructor( private httpClient : HttpClient) { }

  registrarDevolucion(request: DevolucionRequest) {
  return this.httpClient.post<any>(this.URL,request);
  }
}
